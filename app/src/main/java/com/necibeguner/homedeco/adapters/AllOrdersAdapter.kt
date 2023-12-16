package com.necibeguner.homedeco.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.data.order.Order
import com.necibeguner.homedeco.data.order.OrderStatus
import com.necibeguner.homedeco.data.order.getOrderStatus
import com.necibeguner.homedeco.databinding.OrderItemBinding

class AllOrdersAdapter: Adapter<AllOrdersAdapter.OrdersViewHolder>() {

    //OrdersViewHolder iç içe sınıfı, RecyclerView'da her bir sipariş öğesini temsil eden görünüm
    // tutucularını (view holder) yönetir ve sipariş verilerini bağlar.
    inner class OrdersViewHolder ( private val binding: OrderItemBinding): ViewHolder(binding.root){
        // Sipariş öğesini bağlama işlemi
        fun bind(order: Order){
            binding.apply {
                // Sipariş öğesinin bileşenlerine verileri yerleştirme
                tvOrderId.text = order.orderId.toString()
                tvOrderDate.text = order.date
                val resources = itemView.resources
                // Sipariş durumuna göre renkli daire gösterimi
                val colorDrawable = when (getOrderStatus(order.orderStatus)) {
                    is OrderStatus.Ordered -> { ColorDrawable(resources.getColor(R.color.g_orange_yellow)) }
                    is OrderStatus.Confirmed -> { ColorDrawable(resources.getColor(R.color.g_green)) }
                    is OrderStatus.Delivered -> { ColorDrawable(resources.getColor(R.color.g_green)) }
                    is OrderStatus.Shipped -> { ColorDrawable(resources.getColor(R.color.g_green)) }
                    is OrderStatus.Canceled -> { ColorDrawable(resources.getColor(R.color.g_red)) }
                    is OrderStatus.Returned -> { ColorDrawable(resources.getColor(R.color.g_red)) }
                }
                imageOrderState.setImageDrawable(colorDrawable)
            }
        }
    }
    // Sipariş öğeleri arasındaki farkları belirlemek için DiffUtil
    //diffUtil, RecyclerView'daki değişiklikleri algılamak için kullanılan DiffUtil.ItemCallback'i uygular.
    private val diffUtil = object : DiffUtil.ItemCallback<Order>(){
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Siparişlerin ürünlerinin aynı olup olmadığını kontrol etme
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            // Siparişlerin içeriklerinin aynı olup olmadığını kontrol etme
            return oldItem == newItem
        }
    }
    // Liste üzerinde asenkron farkları işlemek için AsyncListDiffer
    val differ = AsyncListDiffer(this,diffUtil)
    //differ, asenkron olarak farkları işlemek için AsyncListDiffer nesnesini tanımlar.
    // "Asenkron" kelimesi, bir işlemin başlatılmasıyla sonuçlanması arasında geçen sürenin
    // kullanıcının beklemesi gerektiği anlamına gelmez.

    //onCreateViewHolder, bir görünüm tutucu oluşturur ve bağlama işlemi yapar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    //onBindViewHolder, sipariş verilerini bağlar ve tıklama olaylarını işler
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            // Sipariş öğesine tıklama olayı ekleme
            onClick?.invoke(order)
        }
    }

    //getItemCount, listenin mevcut öğe sayısını döndürür
    override fun getItemCount(): Int {
        return differ.currentList.size// Listenin mevcut öğe sayısını döndürme
    }

    // Sipariş öğesine tıklama olayını işleyen lambda ifadesi
    var onClick: ((Order) -> Unit)? = null

}