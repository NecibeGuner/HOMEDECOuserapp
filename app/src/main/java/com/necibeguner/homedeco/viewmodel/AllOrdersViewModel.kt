package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.order.Order
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//@HiltViewModel: Hilt tarafından yönetilen ve enjekte edilen ViewModel sınıfı olduğunu belirtir.
@HiltViewModel
//AllOrdersViewModel: Tüm siparişleri yönetmek için kullanılan ViewModel sınıfı.
class AllOrdersViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    // Hilt tarafından yönetilen ve enjekte edilen ViewModel sınıfı.

    // Tüm siparişleri temsil eden MutableStateFlow.
    private val _allOrders = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecifed())
    val allOrders = _allOrders.asStateFlow()

    // ViewModel oluşturulduğunda çalışan ve tüm siparişleri getiren fonksiyon.
    init {
        getAllOrders()
    }

    // Tüm siparişleri getirmek için Firestore'dan sorgu yapılır.
    fun getAllOrders(){
        // Loading durumu yayını başlatılır
        viewModelScope.launch {
            _allOrders.emit(Resource.Loading())
        }

        // Firestore'dan kullanıcının siparişlerinin bulunduğu koleksiyondan veriler alınır.
        firestore.collection("user").document(auth.uid!!).collection("orders").get()
            .addOnSuccessListener {
                // Veriler başarılı şekilde alınırsa, siparişler alınır ve Success durumu yayını yapılır.
                val orders = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _allOrders.emit(Resource.Success(orders))
                }
            }.addOnFailureListener {
                // Veri alımında hata oluşursa, Error durumu yayını yapılır.
                viewModelScope.launch {
                    _allOrders.emit(Resource.Error(it.message.toString()))
                }
            }
        //getAllOrders(): Firestore'dan kullanıcının siparişlerini getiren fonksiyondur. Loading durumu
        // yayını başlatır, Firestore'dan veri alınır ve alınan verilere göre Success veya Error durumu
        // yayını yapılır. Bu durumlar StateFlow ile dışarıya iletilir ve UI tarafında kullanılabilir.
    }
}