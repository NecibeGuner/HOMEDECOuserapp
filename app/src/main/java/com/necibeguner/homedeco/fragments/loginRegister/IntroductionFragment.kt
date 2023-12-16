package com.necibeguner.homedeco.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
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
import com.necibeguner.homedeco.activities.ShoppingActivity
import com.necibeguner.homedeco.databinding.FragmentIntroductionBinding
import com.necibeguner.homedeco.viewmodel.IntroductionViewModel
import com.necibeguner.homedeco.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.necibeguner.homedeco.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod, bir fragment sınıfını temsil eder. IntroductionFragment, Fragment sınıfından türetilmiş ve
// fragment_introduction adlı layout'u kullanır. onCreateView fonksiyonu fragmentın layoutunu bağlar
// ve onViewCreated fonksiyonu ise view oluşturulduktan sonra yapılacak işlemleri içerir.
// @AndroidEntryPoint işareti, bu sınıfın Hilt tarafından yönetileceğini belirtir.
// ViewModel'i bağlamak için viewModels kullanılır. viewModel.navigate LiveData'sını izleyen
// bir coroutine başlatılır. Bu LiveData, belirli eylemlere yanıt verir ve bu eylemlere göre farklı
// fragmentlere geçiş yapar veya aktiviteler başlatır. Ayrıca, buttonStart butonuna tıklandığında
// ViewModel'e bir işlem sinyali gönderilir ve NavController kullanılarak fragmentlar arası geçiş yapılır.

// AndroidEntryPoint, Hilt'in bu fragmentı otomatik olarak bağlamasını sağlar.
@AndroidEntryPoint
// IntroductionFragment sınıfı, Fragment sınıfından türetilmiş ve fragment_introduction layout'unu kullanıyor.
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {

    // FragmentIntroductionBinding sınıfından bir değişken, fragment'ın layoutunu bağlamak için kullanılıyor.
    private lateinit var binding: FragmentIntroductionBinding

    // IntroductionViewModel sınıfından bir viewModel değişkeni, ViewModel'ı bu fragmenta bağlamak için kullanılıyor.
    private val viewModel by viewModels<IntroductionViewModel>()

    // Fragment'ın görünümünü oluşturma işlemi.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bağlamayı oluştur ve rootView'ü geri döndür.
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel'dan gelen verileri izleyen ve eylemlere yanıt veren bir coroutine başlatılıyor.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigate.collectLatest {
                    when (it) {
                        SHOPPING_ACTIVITY -> {
                            // Belirli bir aktiviteye geçiş yapmak için Intent kullanılır.
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                // Yeni bir aktivite başlattığınızda mevcut aktiviteyi temizler ve yeni aktiviteyi başlatır.
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        ACCOUNT_OPTIONS_FRAGMENT -> {
                            // NavController kullanarak belirtilen fragmenta geçiş yapılır.
                            findNavController().navigate(it)
                        }
                        else -> Unit
                    }
                }
            }
        }

        // buttonStart'a tıklandığında yapılacak işlemler.
        binding.buttonStart.setOnClickListener {
            // ViewModel'a butona tıklandığını bildiren bir işlem sinyali gönderir.
            viewModel.startButtonClick()
            // NavController kullanarak fragmentlar arası geçiş yapılır.
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}
