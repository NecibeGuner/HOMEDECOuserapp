package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.CartProduct
import com.necibeguner.homedeco.firebase.FirebaseCommon
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Bu sınıf, DetailsViewModel, bir ürünün sepete eklenmesini veya sepet içinde güncellenmesini sağlar.
//addUpdateProductInCart işlevi, Firestore'da belirli bir kullanıcının sepetine bakar. Eğer ürün
//zaten sepette varsa, ürün miktarını artırır. Eğer ürün sepette yoksa, yeni ürünü sepete ekler.
//Bu işlemler sırasında Resource tipindeki durumları kullanarak, işlemin yükleme durumunu, başarılı
//olma durumunu veya hata durumunu UI katmanına aktarır. Bu şekilde, kullanıcı arayüzü bu işlemlerin
//durumunu doğru şekilde gösterebilir.

//@HiltViewModel: Hilt tarafından yönetilen ve enjekte edilen ViewModel sınıfı olduğunu belirtir.
@HiltViewModel
//DetailsViewModel: Ürünlerin detaylarıyla ilgili işlemleri yönetmek için kullanılan ViewModel sınıfı.
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    // Sepete eklenen veya güncellenen ürünü depolayan değişken
    //_addToCart ve addToCart: Kullanıcının sepetindeki ürünleri temsil eden MutableStateFlow
    // ve onun dışa açık bir versiyonu olan StateFlow. Ürünlerin durumu ve akışı bu değişkenler üzerinden yönetilir.
    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecifed())
    val addToCart = _addToCart.asStateFlow()

    // Sepete ürün eklemek veya güncellemek için işlev
    fun addUpdateProductInCart(cartProduct: CartProduct){
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }// İşlem başladığında yükleme durumu başlatılıyor
        //firestore dan ürünlerin bilgilerini alıp ekrana getiriyor
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id",cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){//sepete yeni ürün ekleme
                        addNewProduct(cartProduct)
                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct){//miktarı arttırma
                            val documentId = it.first().id
                            increaseQuantity(documentId,cartProduct)
                        }else{//yeni ürürn ekleme
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString()))}
            }
    }

    // Yeni bir ürünü sepete ekleyen işlev
    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    // Ürün miktarını artıran işlev
    private fun increaseQuantity(documentId: String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){_, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}