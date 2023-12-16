package com.necibeguner.homedeco.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.util.Constants.INTRODUCTION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

//Bu kod parçası, bir ViewModel sınıfını temsil ediyor. ViewModel, SharedPreferences ve FirebaseAuth
// gibi bağımlılıkları enjekte eder. navigate adında bir StateFlow kullanılarak navigasyon işlemleri
// yönetilir. init bloğu, ViewModel oluşturulduğunda çalışır ve kullanıcı durumuna ve bir butona
// tıklanıp tıklanmadığına göre yönlendirme yapar. startButtonClick() fonksiyonu ise bir butona
// tıklandığında SharedPreferences üzerinde belirli bir anahtarın değerini değiştirir.

@HiltViewModel
// ViewModel sınıfı, Hilt tarafından yönetilen bir bağımlılık olarak işaretlenir.
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // Navigasyon için MutableStateFlow kullanılır, başlangıç değeri 0'dır.
    private val _navigate = MutableStateFlow(0)
    val navigate : StateFlow<Int> = _navigate// StateFlow, dışarıya açılan bir immutable versiyondur

    //MutableStateFlow değiştirilebilir bir durumu temsil ederken, asStateFlow() ise bu durumun güvenli
    // bir kopyasını oluşturur, bu sayede verilerin güvenli bir şekilde paylaşılmasını ve işlenmesini sağlar.

    // Companion object, sınıf düzeyindeki sabit değerleri içerir.
    companion object{
        const val SHOPPING_ACTIVITY = 23// Alışveriş aktivitesinin tanımlandığı sabit bir değer.
        val  ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment// Hesap seçeneklerinin fragment'a geçiş için tanımlanan değer.
    }

    init {
        // ViewModel oluşturulduğunda çalışacak blok.

        // "INTRODUCTION_KEY" adındaki değeri alır, varsayılan olarak false döner.
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY,false)
        // Firebase Authentication'dan mevcut kullanıcıyı alır.
        val user = firebaseAuth.currentUser

        // Eğer kullanıcı mevcutsa (oturum açmışsa) alışveriş aktivitesine yönlendirme yapılır.
        if (user!=null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)// Alışveriş aktivitesine yönlendirme sinyali gönderilir.
            }

        }else if (isButtonClicked){
            // Kullanıcı yoksa ve butona tıklanmışsa hesap seçenekleri fragment'ına yönlendirme yapılır.
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT) // Hesap seçeneklerine yönlendirme sinyali gönderilir.
            }

        }else{
            // Hiçbiri durumu, bir şey yapmaz, boş bir bloktur
            Unit
        }
    }

    // Butona tıklandığında tetiklenecek fonksiyon
    fun startButtonClick(){
        // Butona tıklandığında "INTRODUCTION_KEY" adlı değeri true olarak işaretler.
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }
}