package com.necibeguner.homedeco.fragments.shopping

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
import androidx.navigation.fragment.navArgs
import com.necibeguner.homedeco.data.Address
import com.necibeguner.homedeco.databinding.FragmentAddressBinding
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod parçası, kullanıcı adres bilgilerini eklemek veya güncellemek için bir fragment. ViewModel
// üzerinden adres ekleme işlemleri ve hata durumları gözlemleniyor. Kullanıcıya gösterilecek alanlar
// EditText'lerde dolduruluyor ve ardından ViewModel aracılığıyla adres bilgileri kaydediliyor.
// Hata durumlarında kullanıcıya Toast mesajları ile bilgi veriliyor.

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding // Bağlama değişkeni tanımlanır
    val viewModel by viewModels<AddressViewModel>() // AddressViewModel'den bir örnek alınır
    val args by navArgs<AddressFragmentArgs>() // Geçiş argümanları alınır

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Yaşam döngüsü kapsamında ViewModel'den yeni adres eklemesi işlemi gözlemlenir
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addNewAddress.collectLatest { addAddressResult ->
                    when (addAddressResult) {
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE // Yeni adres eklenirken ilerleme gösterilir
                        }
                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE // Yeni adres ekleme başarılıysa ilerleme gizlenir
                            findNavController().navigateUp() // Geri navigasyon gerçekleştirilir
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), addAddressResult.message, Toast.LENGTH_SHORT).show() // Hata durumunda kullanıcıya bilgi verilir
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Yaşam döngüsü kapsamında ViewModel'den gelen hata mesajları gözlemlenir
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collectLatest { errorMessage ->
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show() // Hata mesajları kullanıcıya gösterilir
                }
            }
        }
    }

    // Fragment'in oluşturulduğu metot, layout için bağlama oluşturulur ve geri döndürülür
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adres ekranını kapatma işlemi
        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp() // Geri navigasyon gerçekleştirilir
        }

        // Geçiş argümanlarından gelen adres bilgisi alınır
        val address = args.address
        if (address == null) {
            binding.buttonDelelte.visibility = View.GONE // Eğer adres bilgisi yoksa silme butonu gizlenir
        } else {
            // Eğer adres bilgisi varsa EditText'lerde gösterilir
            binding.apply {
                edAddressTitle.setText(address.addressTitle)
                edFullName.setText(address.fullName)
                edStreet.setText(address.street)
                edPhone.setText(address.phone)
                edCity.setText(address.city)
                edState.setText(address.state)
            }
        }

        // Adres bilgilerini kaydetme işlemi
        binding.apply {
            buttonSave.setOnClickListener {
                // EditText'lerden adres bilgileri alınır
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val state = edState.text.toString()
                val address = Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address) // ViewModel üzerinden adres eklemesi yapılır
            }
        }
    }
}
