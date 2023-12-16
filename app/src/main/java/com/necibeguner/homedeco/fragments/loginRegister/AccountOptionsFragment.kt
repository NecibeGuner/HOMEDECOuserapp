package com.necibeguner.homedeco.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.databinding.FragmentAccountOptionsBinding

//Bu kod, fragmentın kullanıcı arayüzünü temsil eder. AccountOptionsFragment, Fragment sınıfından
// türetilmiştir ve fragment_account_options adlı layout'u kullanır. onCreateView fonksiyonu fragmentın
// layoutunu bağlar ve onViewCreated fonksiyonu ise view oluşturulduktan sonra yapılacak işlemleri içerir.
// Bu durumda buttonLoginAccountOptions ve buttonRegisterAccountOptions butonlarına tıklanıldığında,
// ilgili fragmentlere geçiş yapmak için NavController kullanılır.

// AccountOptionsFragment sınıfı, Fragment sınıfından türetilmiş ve fragment_account_options layout'unu kullanıyor.
class AccountOptionsFragment : Fragment(R.layout.fragment_account_options) {

    // FragmentAccountOptionsBinding sınıfından bir değişken, fragment'ın layoutunu bağlamak için kullanılıyor.
    private lateinit var binding: FragmentAccountOptionsBinding

    // Fragment'ın görünümünü oluşturma işlemi.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bağlamayı oluştur ve rootView'ü geri döndür.
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // buttonLoginAccountOptions'a tıklandığında, loginFragment'e geçiş yapacak olan bir onClickListener.
        binding.buttonLoginAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
        }

        // buttonRegisterAccountOptions'a tıklandığında, registerFragment'e geçiş yapacak olan bir onClickListener.
        binding.buttonRegisterAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
        }
    }
}
