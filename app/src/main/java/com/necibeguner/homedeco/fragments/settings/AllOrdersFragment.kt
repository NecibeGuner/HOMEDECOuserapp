package com.necibeguner.homedeco.fragments.settings

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.necibeguner.homedeco.adapters.AllOrdersAdapter
import com.necibeguner.homedeco.databinding.FragmentOrdersBinding
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod parçası, bir siparişler fragment'ı  üzerinde çalışıyor .
// ViewModel üzerinden siparişlerin alınması ve bu siparişlerin RecyclerView üzerinde gösterilmesi sağlanıyor.
// Ayrıca, siparişlere tıklama işlemi ve geri butonuna basıldığında belirli navigasyon işlemleri gerçekleştiriliyor.

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding // Bağlama değişkeni tanımlanır
    val viewModel by viewModels<AllOrdersViewModel>() // AllOrdersViewModel'den bir örnek alınır
    val ordersAdapter by lazy { AllOrdersAdapter() } // Tembel yükleme ile OrdersAdapter oluşturulur

    // Fragment'in oluşturulduğu metot, layout için bağlama oluşturulur ve geri döndürülür
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater)
        return binding.root
    }

    // View oluşturulduktan sonra yapılacak işlemler bu metotta gerçekleştirilir
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Siparişler RecyclerView'ı ayarlanır
        setupOrdersRv()

        // ViewModel'den sağlanan tüm siparişler gözlemlenir ve UI buna göre güncellenir
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allOrders.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            // Veri yüklenirken ilerleme çubuğu gösterilir
                            binding.progressbarAllOrders.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            // Veri başarıyla yüklendiyse ilerleme çubuğu gizlenir ve RecyclerView'a veri seti atanır
                            binding.progressbarAllOrders.visibility = View.GONE
                            ordersAdapter.differ.submitList(it.data)
                            if (it.data.isNullOrEmpty()) {
                                // Eğer veri yoksa kullanıcıya bilgi vermek için bir metin görüntülenir
                                binding.tvEmptyOrders.visibility = View.VISIBLE
                            }
                        }
                        is Resource.Error -> {
                            // Veri yüklenirken hata oluşursa kullanıcıya bir Toast ile hata mesajı gösterilir
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                            binding.progressbarAllOrders.visibility = View.GONE
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Siparişler RecyclerView'ı üzerinde tıklama işlemi
        ordersAdapter.onClick = {
            val action = AllOrdersFragmentDirections.actionOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }

        // Geri butonuna tıklama işlemi
        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    // Siparişler RecyclerView'ının ayarlandığı yardımcı metot
    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = ordersAdapter // Adapter RecyclerView'a atanır
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false) // LayoutManager atanır
        }
    }
}
