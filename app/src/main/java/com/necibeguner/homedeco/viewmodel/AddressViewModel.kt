package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.Address
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//ViewModel sınıflarının Hilt tarafından yönetilmesini ve bağımlılıklarının enjekte edilmesini sağlar.
@HiltViewModel
//AddressViewModel: Adresle ilgili işlemleri yöneten ViewModel sınıfı.
class AddressViewModel @Inject constructor(
    //firestore ve auth: Firestore ve FirebaseAuth sınıflarının enjekte edilmiş referansları.
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {
    // Hilt kullanılarak enjekte edilen ve ViewModel'i oluşturan sınıf.

    // Adres eklemek için kullanılan MutableStateFlow.
    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecifed())
    val addNewAddress = _addNewAddress.asStateFlow()

    //StateFlow, tek bir değeri korumak ve güncellemek için kullanılırken,
    // SharedFlow birden fazla değeri eş zamanlı olarak iletmek için kullanılır.

    // Hata durumlarını bildirmek için kullanılan MutableSharedFlow.
    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    fun addAddress(address: Address){
        // Gelen adres verilerini doğrulamak için bir fonksiyon çağrılır.
        val validateInputs = validateInputs(address)

        if (validateInputs){
            // Eğer veriler geçerliyse, işlem başlatılır ve Loading durumu gönderilir.
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            // Firestore'da belirli bir kullanıcının adres koleksiyonuna veriyi ekleme işlemi yapılır.
            firestore.collection("user").document(auth.uid!!).collection("address")
                .document().set(address).addOnSuccessListener {
                    // Başarılı olursa, Success durumu gönderilir.
                    viewModelScope.launch{_addNewAddress.emit(Resource.Success(address))}
                }.addOnFailureListener {
                    // Başarısız olursa, Error durumu ve hata mesajı gönderilir.
                    viewModelScope.launch{_addNewAddress.emit(Resource.Error(it.message.toString()))}
                }
        }else{
            // Eğer gelen veriler geçerli değilse, bir hata mesajı gönderilir.
            viewModelScope.launch {
                _error.emit("Bilgilerinizi doğru girdiğinizden emin olun")
            }
        }
    }

    // Gelen adres verilerini doğrulamak için kullanılan fonksiyon.
    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty()&&
                address.city.trim().isNotEmpty()&&
                address.phone.trim().isNotEmpty()&&
                address.state.trim().isNotEmpty()&&
                address.fullName.trim().isNotEmpty()&&
                address.street.trim().isNotEmpty()
    }
}