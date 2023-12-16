package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.Address
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
//BillingViewModel: Fatura işlemleriyle ilgili olarak kullanılan ViewModel sınıfı.
class BillingViewModel @Inject constructor(
    //firestore ve auth: Firestore ve FirebaseAuth bağımlılıklarının enjekte edilmiş referansları.
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {
    // Hilt tarafından yönetilen ve enjekte edilen ViewModel sınıfı.

    // Kullanıcının adreslerini temsil eden MutableStateFlow.
    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecifed())
    val address = _address.asStateFlow()

    // ViewModel oluşturulduğunda çalışan ve kullanıcının adreslerini getiren fonksiyon.
    init {
        getUserAddresses()
    }

    // Kullanıcının adreslerini Firestore'dan dinleyen ve değişiklikleri bildiren fonksiyon.
    fun getUserAddresses(){
        // Loading durumu yayını başlatılır.
        viewModelScope.launch { _address.emit(Resource.Loading()) }

        // Firestore'dan kullanıcının adreslerini dinler ve herhangi bir değişiklik olduğunda bildirim alır.
        firestore.collection("user").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                if (error != null){
                    // Eğer bir hata varsa, Error durumu yayını yapılır.
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                    //getUserAddresses(): Firestore'dan kullanıcının adreslerini dinleyen bir fonksiyondur.
                    // Öncelikle Loading durumu yayını yapar, ardından addSnapshotListener kullanarak
                    // Firestore'dan adres değişikliklerini dinler.
                    // Herhangi bir hata oluştuğunda Error durumu yayınlanır, değişiklik olduğunda ise
                    // adresler alınarak Success durumu yayını yapılır. Bu durumlar StateFlow ile dışarıya
                    // iletilir ve UI tarafında kullanılabilir.
                }

                // Değişiklik olması durumunda adresler alınır ve Success durumu yayını yapılır.
                val addresses = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(addresses!!)) }
            }
    }
}
