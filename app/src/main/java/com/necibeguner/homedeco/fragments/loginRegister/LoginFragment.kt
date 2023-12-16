package com.necibeguner.homedeco.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.activities.ShoppingActivity
import com.necibeguner.homedeco.databinding.FragmentLoginBinding
import com.necibeguner.homedeco.dialog.setupBottomSheetDialog
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod parçası, bir giriş ekranını temsil eder. LoginFragment, Fragment sınıfından türetilmiştir
//ve fragment_login adlı layout'u kullanır. onCreateView fonksiyonu fragmentın layoutunu bağlar ve
//onViewCreated fonksiyonu ise view oluşturulduktan sonra yapılacak işlemleri içerir. ViewModel'i
//bağlamak için viewModels kullanılır. Kullanıcının eylemlerine yanıt vermek için çeşitli
//onClickListener'lar kullanılır. Kullanıcının giriş yapması, şifresini sıfırlaması ve bu işlemlerin
//sonuçlarını gözlemlemek için ViewModel kullanılır. LiveData'lar aracılığıyla, belirli durumlarda
//Snackbar veya Toast mesajları kullanıcıya geri bildirim sağlar ve yeni aktiviteler başlatır veya fragmentlere geçiş yapar.

// AndroidEntryPoint, Hilt'in bu fragmentı otomatik olarak bağlamasını sağlar.
@AndroidEntryPoint
// LoginFragment sınıfı, Fragment sınıfından türetilmiş ve fragment_login layout'unu kullanıyor.
class LoginFragment : Fragment(R.layout.fragment_login) {

    // FragmentLoginBinding sınıfından bir değişken, fragment'ın layoutunu bağlamak için kullanılıyor.
    private lateinit var binding: FragmentLoginBinding

    // LoginViewModel sınıfından bir viewModel değişkeni, ViewModel'ı bu fragmenta bağlamak için kullanılıyor.
    private val viewModel by viewModels<LoginViewModel>()

    // Fragment'ın görünümünü oluşturma işlemi.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Bağlamayı oluştur ve rootView'ü geri döndür.
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "tvDontHaveAccount" adlı TextView'e tıklandığında, registerFragment'e geçiş yapacak olan bir onClickListener.
        binding.tvDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // "buttonLoginLogin" adlı butona tıklandığında yapılacak işlemler.
        binding.apply {
            buttonLoginLogin.setOnClickListener {
                // Giriş için kullanıcıdan alınan email ve şifre bilgileri.
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password) // ViewModel'daki giriş işlemi çağrılır.
            }
        }

        // "tvForgotPasswordLogin" adlı TextView'e tıklandığında bir bottomSheetDialog gösterilir ve şifre sıfırlama işlemi yapılır.
        binding.tvForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email) // ViewModel'daki şifre sıfırlama işlemi çağrılır.
            }
        }

        // Şifre sıfırlama işlemi sonucunu gözlemleyen bir coroutine başlatılır.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetPassword.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunda yapılacak işlemler buraya yazılabilir.
                        }
                        is Resource.Success -> {
                            // Şifre sıfırlama başarılıysa kullanıcıya Snackbar ile bilgi verilir.
                            Snackbar.make(requireView(), "Link email adresinize gönderildi", Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Error -> {
                            // Hata durumunda Snackbar ile kullanıcıya bilgi verilir.
                            Snackbar.make(requireView(), "Hata: ${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Giriş işleminin sonucunu gözlemleyen bir coroutine başlatılır.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.login.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Yükleme durumunda yapılacak işlemler buraya yazılabilir.
                            binding.buttonLoginLogin.revertAnimation() // Butonun animasyonunu geri al
                        }
                        is Resource.Success -> {
                            // Giriş başarılıysa yeni bir aktivite başlatılır ve mevcut aktivite temizlenir.
                            binding.buttonLoginLogin.revertAnimation() // Butonun animasyonunu geri al
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        is Resource.Error -> {
                            // Hata durumunda kullanıcıya Toast mesajı gösterilir ve butonun animasyonu geri alınır.
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            binding.buttonLoginLogin.revertAnimation() // Butonun animasyonunu geri al
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
}
