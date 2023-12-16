package com.necibeguner.homedeco.fragments.shopping

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.adapters.AddressAdapter
import com.necibeguner.homedeco.adapters.BillingProductsAdapter
import com.necibeguner.homedeco.data.Address
import com.necibeguner.homedeco.data.CartProduct
import com.necibeguner.homedeco.data.order.Order
import com.necibeguner.homedeco.data.order.OrderStatus
import com.necibeguner.homedeco.databinding.FragmentBillingBinding
import com.necibeguner.homedeco.util.HorizontalItemDecoration
import com.necibeguner.homedeco.util.Resource
import com.necibeguner.homedeco.viewmodel.BillingViewModel
import com.necibeguner.homedeco.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

//Bu kod, fragment içerisinde fatura işlemlerini gerçekleştiren bir ekranı oluşturuyor.
// Özellikle, ödeme yapılıp yapılmayacağına bağlı olarak görüntünün nasıl oluşturulacağı ve siparişin
// nasıl tamamlanacağı gibi adımları içeriyor. Uygulama içindeki farklı bileşenler arasında geçiş yapmak
// için Navigation Component kullanılmış gibi görünüyor.

@AndroidEntryPoint
class BillingFragment : Fragment() {

    // Gerekli importlar ve sınıf içi değişkenlerin tanımlanması
    private lateinit var binding: FragmentBillingBinding // Fragment'ın bağlamını tutmak için bir değişken tanımlanıyor
    private val addressAdapter by lazy { AddressAdapter() } // Adresleri göstermek için bir adaptör oluşturuluyor
    private val billingProductsAdapter by lazy { BillingProductsAdapter() } // Faturalandırma ürünlerini göstermek için bir adaptör oluşturuluyor
    private val billingViewModel by viewModels<BillingViewModel>() // Faturalandırma veri modeli oluşturuluyor
    private val args by navArgs<BillingFragmentArgs>() // Fragment'a gönderilen argümanlar alınıyor
    private var products = emptyList<CartProduct>() // Ürünler listesi başlangıçta boş oluşturuluyor
    private var totalPrice = 0f // Toplam fiyat başlangıçta sıfır olarak ayarlanıyor

    private var selectedAddress: Address? = null // Seçili adres tutulacak değişken tanımlanıyor
    private val orderViewModel by viewModels<OrderViewModel>() // Sipariş veri modeli oluşturuluyor

    // Fragment oluşturulduğunda çalışacak işlemler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Argümanlardan ürünleri ve toplam fiyatı alıp ilgili değişkenlere atama yapılıyor
        products = args.products.toList()
        totalPrice = args.totalPrice
    }

    // Fragment'ın görünümünün oluşturulduğu metot
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Bağlam oluşturulup geri döndürülüyor
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    // Görünüm oluşturulduktan sonra yapılacak işlemler
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Görünümlerin hazırlanması için metotlar çağrılıyor
        setupBillingProductsRv() // Faturalandırma ürünlerini göstermek için RecyclerView hazırlanıyor
        setupAddressRv() // Adresleri göstermek için RecyclerView hazırlanıyor

        // Eğer ödeme yapılmayacaksa, bazı bileşenlerin görünürlüğü kapatılıyor
        if (!args.payment) {
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        // Adres eklemek için butona tıklama işlemi
        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        // Fatura ekranını kapatmak için butona tıklama işlemi
        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        // Adreslerin yüklenmesi ve güncellenmesi için bir yaşam döngüsü oluşturuluyor
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                billingViewModel.address.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            addressAdapter.differ.submitList(it.data)
                            binding.progressbarAddress.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.GONE
                            Toast.makeText(requireContext(), "Hata ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Siparişlerin yüklenmesi ve güncellenmesi için bir yaşam döngüsü oluşturuluyor
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orderViewModel.order.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            binding.buttonPlaceOrder.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            findNavController().navigateUp()
                            Snackbar.make(requireView(), "Siparişiniz Alındı", Snackbar.LENGTH_LONG)
                                .show()
                        }
                        is Resource.Error -> {
                            binding.buttonPlaceOrder.revertAnimation()
                            Toast.makeText(requireContext(), "Hata ${it.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit
                    }
                }
            }
        }

        // Ürünleri RecyclerView'a yükleme
        billingProductsAdapter.differ.submitList(products)
        // Toplam fiyatı gösterme
        binding.tvTotalPrice.text = "$totalPrice TL"

        // Adres seçildiğinde yapılacak işlemler
        addressAdapter.onClick = { address ->
            selectedAddress = address
            if (!args.payment) {
                val bundle = Bundle().apply {
                    putParcelable("address", selectedAddress)
                }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, bundle)
            }
        }

        // Sipariş verme butonuna tıklama işlemi
        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                Toast.makeText(requireContext(), "Adres Seçiniz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    // Sipariş onaylama dialogunu gösterme
    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Siparişi Tamamla")
            setMessage("Sepetinizdeki ürünleri sipariş vermek istiyor musunuz? ")
            setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Evet") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    // Adres RecyclerView'unun hazırlanması
    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    // Faturalandırma ürünleri RecyclerView'unun hazırlanması
    private fun setupBillingProductsRv() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}
