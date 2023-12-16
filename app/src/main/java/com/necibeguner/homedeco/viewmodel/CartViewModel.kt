package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.CartProduct
import com.necibeguner.homedeco.firebase.FirebaseCommon
import com.necibeguner.homedeco.helper.getProductPrice
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel: Hilt tarafından yönetilen ve enjekte edilen ViewModel sınıfı olduğunu belirtir.
@HiltViewModel
//CartViewModel: Alışveriş sepetiyle ilgili işlemleri yönetmek için kullanılan ViewModel sınıfı.
class CartViewModel @Inject constructor(
    //firestore, auth ve firebaseCommon: Firestore, FirebaseAuth ve FirebaseCommon bağımlılıklarının enjekte edilmiş referansları.
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    //_cartProducts ve cartProducts: Kullanıcının sepetindeki ürünleri temsil eden MutableStateFlow
    // ve onun dışa açık bir versiyonu olan StateFlow. Ürünlerin durumu ve akışı bu değişkenler üzerinden yönetilir.
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecifed())
    val cartProducts = _cartProducts.asStateFlow()

    //productsPrice: Sepetteki ürünlerin toplam fiyatını hesaplayan fonksiyonu StateFlow olarak yayınlar
    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    // _deleteDialog ve deleteDialog: Ürün silme işlemi için MutableSharedFlow ve onun dışa açık bir
    // versiyonu olan SharedFlow. Ürün silme işlemlerinin durumu ve akışı bu değişkenler üzerinden yönetilir.
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    // Sepetteki ürünlerin detaylarını depolayan değişken
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    // Sepet içeriğini silme işlevi
    //deleteCartProduct(): Sepetten bir ürünü silen fonksiyondur.
    // Firestore'dan ilgili ürün belirtilen kullanıcıya ait belgeden silme işlemi gerçekleştirilir.
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }
    }

    // Ürün fiyatlarını hesaplayan özel bir fonksiyon
    //calculatePrice(): Sepetteki ürünlerin toplam fiyatını hesaplayan fonksiyondur.
    // Ürünlerin fiyatı ve miktarı kullanılarak toplam fiyat hesaplanır ve döndürülür.
    private fun calculatePrice(data: List<CartProduct>): Float? {
        return data.sumByDouble {cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)*cartProduct.quantity).toDouble()
        }.toFloat()
    }

    // ViewModel oluşturulduğunda çalışan bloktur. Sepetteki ürünleri getirir.
    init {
        getCartProducts()
    }

    // Sepetteki ürünleri alma işlevi
    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        // Firestore'dan kullanıcının sepetindeki ürünleri bulur ve herhangi bir değişiklik olduğunda bildirim alır.
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener{value, error ->
                if (error != null || value == null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                }else{
                    // Değişiklik olması durumunda sepet ürünlerini alır ve Success durumu yayını yapılır.
                    cartProductDocuments =value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }

    // Ürün miktarını değiştirme işlevi
    //changeQuantity(): Sepetteki bir ürünün miktarını değiştiren fonksiyondur.
    // Artırma veya azaltma işlemi yapılırken, firebaseCommon üzerinden ilgili Firestore işlemleri gerçekleştirilir.
    fun changeQuantity (
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){
        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**Eğer index -1'e eşitse [getCartProducts] fonksiyonu gecikebilir, bu da beklediğimiz
         * sonucun [_cartProducts] içinde gecikmesine neden olabilir.
         * Uygulamanın çökmesini engellemek için bir kontrol yapıyoruz.*/

        // Eğer index -1'e eşitse, sepet ürünleri henüz yüklenmedi demektir.
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch {_deleteDialog.emit(cartProduct)}
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    // Ürün miktarını azaltan fonksiyon.
    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    // Ürün miktarını artırma işlevi
    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

}