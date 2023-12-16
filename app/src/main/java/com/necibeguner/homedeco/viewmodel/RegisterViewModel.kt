package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.necibeguner.homedeco.data.User
import com.necibeguner.homedeco.util.Constants.USER_COLLECTION
import com.necibeguner.homedeco.util.RegisterFieldsState
import com.necibeguner.homedeco.util.RegisterValidation
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.util.validateEmail
import com.necibeguner.homedeco.util.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
//bu sınıf kayıt olma işlemi sırasındaki bilgileri alıyor
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore //db = dataBase ifadesinin kısaltması
): ViewModel() {

    // MutableStateFlow değiştirilebilir bir durumu temsil ederken, asStateFlow() ise bu durumun
    // güvenli bir kopyasını oluşturur, bu sayede verilerin güvenli bir şekilde paylaşılmasını ve işlenmesini sağlar.
    // Kullanıcının kaydını takip etmek için MutableStateFlow kullanılır
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecifed())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    // Kullanıcının e-posta ve şifre ile hesap oluşturması için bir fonksiyon
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }

            // FirebaseAuth kullanarak kullanıcıyı oluştur
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { authResult ->
                    // Başarılı olduğunda FirebaseUser'ı alıp Resource.Success ile kaydet
                    authResult.user?.let {
                        saveUserInfo(it.uid,user)

                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }else{
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email),validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String):Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
        return shouldRegister
    }

}