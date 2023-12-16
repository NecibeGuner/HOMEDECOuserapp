package com.necibeguner.homedeco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.necibeguner.homedeco.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
// ViewModel sınıfı, Hilt tarafından yönetilen bir bağımlılık olarak işaretlenir.
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    // Giriş işlemi için MutableSharedFlow kullanılır, FirebaseUser tipindeki bir kaynağı paylaşır.
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()// Dışarıya açılan immutable bir versiyondur.

    //MutableSharedFlow, değiştirilebilirliği ile akışa veri ekleme ve çıkarma yeteneği sunarken,
    // asSharedFlow() ile dışarıya immutable(değiştirilemez) bir sürüm sağlar, yani akışın içeriğini değiştirmek
    // için yeni bir referans oluşturulması gerekir.

    // Şifre sıfırlama işlemi için MutableSharedFlow kullanılır, String tipinde bir kaynağı paylaşır.
    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()// Dışarıya açılan immutable bir versiyondur.

    // Kullanıcı girişi için işlemi yapan fonksiyon
    fun login(email: String, password: String){
        // Giriş işlemi başlamadan önce yükleme durumu sinyali gönderilir.
        viewModelScope.launch { _login.emit(Resource.Loading()) }
        firebaseAuth.signInWithEmailAndPassword(
            email,password// kayıtlı email ve şifre ile giriş yapılır.
        ).addOnSuccessListener {
            // Başarılı giriş durumunda FirebaseUser kaynağıyla başarılı sinyal gönderilir.
            viewModelScope.launch {
                it.user?.let {
                    _login.emit(Resource.Success(it))
                }
            }
        }.addOnFailureListener {
            // Girişte bir hata oluştuğunda hata mesajıyla hata sinyali gönderilir.
            viewModelScope.launch {
                _login.emit(Resource.Error(it.message.toString()))
            }
        }
    }

    // Şifre sıfırlama işlemini gerçekleştiren fonksiyon
    fun resetPassword(email: String){
        viewModelScope.launch {
            // Şifre sıfırlama işlemi başlamadan önce yükleme durumu sinyali gönderilir.
            _resetPassword.emit(Resource.Loading())
        }
            firebaseAuth
                // Verilen email adresine şifre sıfırlama email'i gönderilir.
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    // Şifre sıfırlama başarılı olduğunda email ile başarılı sinyal gönderilir.
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Success(email))
                    }
                }
                .addOnFailureListener {
                    // Şifre sıfırlama işleminde hata oluştuğunda hata mesajıyla hata sinyali gönderilir.
                    viewModelScope.launch {
                        _resetPassword.emit(Resource.Error(it.message.toString()))
                    }
                }

    }
}