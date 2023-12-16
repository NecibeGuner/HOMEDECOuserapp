package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.User
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Bu ViewModel, kullanıcı profili verilerini yönetmek için oluşturulmuş. _user adında bir
// MutableStateFlow kullanılarak kullanıcı verileri akış halinde tutulur. Bu akış, kullanıcı
// bilgilerini yükleme durumu, başarı ve hata durumları için Resource ile temsil eder.
// MutableStateFlow değiştirilebilir bir durumu temsil ederken, asStateFlow() ise bu durumun güvenli
// bir kopyasını oluşturur, bu sayede verilerin güvenli bir şekilde paylaşılmasını ve işlenmesini sağlar.
//getUser() fonksiyonu Firestore'dan belirli bir kullanıcı belirleyicisi (UID) ile belirtilen
// belirli bir kullanıcının verilerini dinler. Firestore'daki verilerde herhangi bir değişiklik
// olduğunda, bu değişikliklerin akışa emit edilmesini sağlar. logOut() fonksiyonu ise
// kullanıcının çıkış yapmasını sağlar, yani Firebase Authentication üzerinden oturumu kapatır.
// Bu şekilde kullanıcı işlem yaparken oturumu kapatma yeteneğine sahip olur.

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecifed())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser(){
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error!= null){
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                }else{
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(user))
                        }
                    }
                }
            }
    }

    fun logOut(){
        auth.signOut()
    }
}