package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.Category
import com.necibeguner.homedeco.data.Product
import com.necibeguner.homedeco.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Bu sınıf, CategoryViewModel, Firebase Firestore'dan belirli bir kategorideki ürünleri almak için
// kullanılır. fetchOfferProducts ve fetchBestProducts fonksiyonları, Firestore sorgularını kullanarak
// belirli bir kategorideki teklifler ve en iyi ürünlerin listesini alır. Bu sorgular, belirli bir
// kategoriye ait ürünleri ve ilgili özellikleri (örneğin offerPercentage özelliği null olmayanları)
// almak için kullanılır. Firestore'dan veri başarıyla alınırsa, bu ürünler Resource.Success durumuyla
// birlikte yayınlanır. Eğer bir hata oluşursa, Resource.Error durumu ile birlikte hata mesajı yayınlanır.
// Bu sınıf, bu ürünleri almak ve bunları UI katmanına aktarmak için StateFlow'ları kullanır.

class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModel() {

    // Teklif ürünlerini depolayan değişken
    private val _offerProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecifed())
    val offerProducts = _offerProducts.asStateFlow()

    //MutableStateFlow değiştirilebilir bir durumu temsil ederken, asStateFlow() ise bu durumun
    // güvenli bir kopyasını oluşturur, bu sayede verilerin güvenli bir şekilde paylaşılmasını ve işlenmesini sağlar.

    // En iyi ürünleri depolayan değişken
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecifed())
    val bestProducts = _bestProducts.asStateFlow()

    // Sınıf başlatıldığında teklif ve en iyi ürünleri getirme işlevi
    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    // products dosyasındaki category e ait teklif ürünlerini getirme işlevi
    fun fetchOfferProducts() {
        viewModelScope.launch {
            _offerProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _offerProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    // products dosyasındaki category e ait en iyi ürünlerini getirme işlevi
    fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category",category.category)
            .whereNotEqualTo("offerPercentage",null).get()
            .addOnSuccessListener {
                val products = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(products))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}