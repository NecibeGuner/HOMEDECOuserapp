package com.necibeguner.homedeco.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.data.User
import com.necibeguner.homedeco.databinding.FragmentRegisterBinding
import com.necibeguner.homedeco.util.RegisterValidation
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Bu kod, bir kayıt fragment'ında kullanıcı kaydı işlemlerini gerçekleştirmek için bir ViewModel
// kullanıyor . ViewModel, kayıt ve doğrulama işlemlerini sağlayarak, UI üzerinde gerekli güncellemeleri
// yapmaya olanak tanır. E-posta ve şifre doğrulamaları yapılarak kullanıcıya geri bildirim sağlanır
// ve gerekli alanlarda hata mesajları gösterilir.

// Fragment'ın içindeki TAG değişkeni, loglama işlemleri için bir etiket oluşturuyor.
private val TAG = "RegisterFragment"

// AndroidEntryPoint, Hilt tarafından kullanılan bir işaretçidir ve bu sınıfın Hilt tarafından oluşturulmasını sağlar
@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    // Gecikmeli olarak bağlama değişkeni oluşturulur
    private lateinit var binding: FragmentRegisterBinding

    // RegisterViewModel'den bir örnek alınır
    private val viewModel by viewModels<RegisterViewModel>()

    // Fragment'in oluşturulduğu metot, layout için bağlama oluşturulur ve geri döndürülür
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    // View oluşturulduktan sonra yapılacak işlemler bu metotta gerçekleştirilir
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "Hesabınız var mı?" metnine tıklandığında, kayıt fragment'ından giriş fragment'ına geçiş yapılır
        binding.tvHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Kayıt butonuna tıklandığında yapılacak işlemler
        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                // Kullanıcı bilgileri alınır ve User nesnesine atanır
                val user = User(
                    edNameRegister.text.toString().trim(),
                    edSurnameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()

                // ViewModel aracılığıyla e-posta ve şifre ile hesap oluşturma işlemi başlatılır
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        // ViewModel tarafından sağlanan kayıt durumu gözlemlenir ve bu duruma göre UI güncellenir
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonRegisterRegister.startAnimation()
                        }
                        is Resource.Success -> {
                            Log.d("test", it.data.toString())
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        is Resource.Error -> {
                            Log.e(TAG, it.message.toString())
                            binding.buttonRegisterRegister.revertAnimation()
                            // Hata durumunda kullanıcıya bildirim yapılabilir
                        }
                        else -> Unit
                    }
                }
            }

            // ViewModel tarafından sağlanan doğrulama durumu gözlemlenir ve UI güncellenir
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.validation.collectLatest { validation ->
                        if (validation.email is RegisterValidation.Failed) {
                            // E-posta doğrulaması başarısızsa hata mesajı gösterilir
                            withContext(Dispatchers.Main) {
                                binding.edEmailRegister.apply {
                                    requestFocus()
                                    error = validation.email.message
                                }
                            }
                        }
                        if (validation.password is RegisterValidation.Failed) {
                            // Şifre doğrulaması başarısızsa hata mesajı gösterilir
                            withContext(Dispatchers.Main) {
                                binding.edPasswordRegister.apply {
                                    requestFocus()
                                    error = validation.password.message
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
