package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.Product
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Bu kod, bir ViewModel sınıfı içinde Firestore'dan ürünleri çeken üç farklı işlevi içeriyor.
// MutableStateFlow ve StateFlow kullanılarak üç farklı akış tanımlanır: specialProducts,
// bestDealsProducts ve bestProducts. Bu akışlar, ürünleri yükleme durumu, başarı ve hata durumları
// için kaynakları temsil eder. Üç farklı türde ürün getirme işlevi bulunmaktadır:
// fetchSpecialProducts, fetchBestDeals ve fetchBestProducts. Bu işlevler, Firestore'dan belirli
// kategorilere göre ürünleri getirir ve bu ürünleri ilgili MutableStateFlow'lara emit eder. Ayrıca,
// sayfalama bilgilerini tutan bir iç veri sınıfı da bulunmaktadır (PagingInfo), bu bilgiler en iyi
// ürünlerin sayfalama işlemi için kullanılır.

@HiltViewModel
// ViewModel sınıfı, Hilt tarafından yönetilen bir bağımlılık olarak işaretlenir.
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    // Özel ürünleri temsil eden MutableStateFlow
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecifed())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    // En iyi fırsatları temsil eden MutableStateFlow
    private val _bestDealsProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecifed())
    val bestDealsProducts: StateFlow<Resource<List<Product>>> = _bestDealsProducts

    // En iyi ürünleri temsil eden MutableStateFlow
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecifed())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    // Sayfalama bilgilerini içeren iç veri sınıfı
    private val pagingInfo = PagingInfo()

    // ViewModel oluşturulduğunda çalışacak olan init bloğu
    init {
        fetchSpecialProducts()// Özel ürünleri getiren fonksiyonu çağırır
        fetchBestDeals()// En iyi fırsatları getiren fonksiyonu çağırır
        fetchBestProducts()// En iyi ürünleri getiren fonksiyonu çağırır
    }

    // Özel ürünleri getiren fonksiyon
    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        // Özel ürünleri getirme işlemi başlamadan önce yükleme durumu sinyali gönderir
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Special Products").get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    // Özel ürünleri başarıyla getirildiğinde sinyal gönderir
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    // Özel ürünleri getirme işleminde hata oluştuğunda hata sinyali gönderir
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDeals() {
        // En iyi fırsatları getiren fonksiyon
        viewModelScope.launch {
            // En iyi fırsatları getirme işlemi başlamadan önce yükleme durumu sinyali gönderir
            _bestDealsProducts.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Best Deals").get().addOnSuccessListener { result ->
                val bestDealsProduct = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsProduct))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    // En iyi ürünleri getiren fonksiyon
    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }
            firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10).get()
                .addOnSuccessListener { result ->
                    val bestProduct = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestProduct == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProduct
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProduct))
                    }
                    pagingInfo.bestProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
    internal data class PagingInfo(
        var bestProductsPage: Long = 1,
        var oldBestProducts:List <Product> = emptyList(),
        var isPagingEnd:Boolean = false
    )

