package com.necibeguner.homedeco.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.necibeguner.homedeco.adapters.BillingProductsAdapter
import com.necibeguner.homedeco.data.order.OrderStatus
import com.necibeguner.homedeco.data.order.getOrderStatus
import com.necibeguner.homedeco.databinding.FragmentOrderDetailBinding
import com.necibeguner.homedeco.util.VerticalItemDecoration
import com.necibeguner.homedeco.util.hideBottomNavigationView

//Bu kod parçası, bir siparişin detaylarını gösteren bir fragment.
//Geçiş argümanlarından sipariş bilgisini alıp, bu bilgiye göre UI'yı güncelliyor. Ayrıca, siparişin
//durumunu adım adım gösteren bir adım görünümü bulunmakta ve bu duruma göre ilgili adımın
//tamamlanmış olarak işaretlenmesini sağlıyor. Siparişin ürünleri RecyclerView üzerinde gösteriliyor
//ve geri butonuna basıldığında belirli bir navigasyon gerçekleştiriliyor.

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding // Bağlama değişkeni tanımlanır
    private val billingProductsAdapter by lazy { BillingProductsAdapter() } // Tembel yükleme ile BillingProductsAdapter oluşturulur
    private val args by navArgs<OrderDetailFragmentArgs>() // Geçiş argümanlarını almak için kullanılan değişken tanımlanır

    // Fragment'in oluşturulduğu metot, layout için bağlama oluşturulur ve geri döndürülür
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    // View oluşturulduktan sonra yapılacak işlemler bu metotta gerçekleştirilir
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Geçiş argümanlarından sipariş bilgisini alır
        val order = args.order
        hideBottomNavigationView() // Alt gezinti çubuğunu gizler

        // Sipariş RecyclerView'ını ayarlar
        setupOrderRv()

        binding.apply {
            // Sipariş numarasını gösterir
            tvOrderId.text = "Sipariş #${order.orderId}"

            // Adım görünümüne sipariş durumlarını ayarlar
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )

            // Siparişin mevcut durumunu belirler
            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }

            // Adım görünümünü mevcut duruma göre ayarlar
            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }

            // Sipariş teslimat bilgilerini gösterir
            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = order.address.phone

            // Toplam fiyatı gösterir
            tvTotalPrice.text = "${order.totalPrice} TL"
        }

        // Sipariş ürünlerini RecyclerView'a yükler
        billingProductsAdapter.differ.submitList(order.products)

        // Geri butonuna tıklama işlemi
        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    // Sipariş RecyclerView'ını ayarlayan yardımcı metot
    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter // Adapter RecyclerView'a atanır
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false) // LayoutManager atanır
            addItemDecoration(VerticalItemDecoration()) // Dikey süsleme eklenir
        }
    }
}
