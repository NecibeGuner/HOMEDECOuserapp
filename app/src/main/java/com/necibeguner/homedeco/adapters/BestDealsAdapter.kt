package com.necibeguner.homedeco.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.data.Product
import com.necibeguner.homedeco.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    // Her bir ürün için bir görünüm tutucusu tanımlanmıştır.
    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root) {

        // Veri bağlama işlevi, ürünlerin görünümünü günceller.
        fun bind(product: Product) {
            binding.apply {
                // Glide kütüphanesiyle ürün resmini yükleme
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)

                // Ürünün indirim yüzdesi varsa fiyatı yeniden hesaplayıp görünüme yazdırma
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "${String.format("%.2f", priceAfterOffer)} TL"
                }

                // Ürünün eski fiyatını görünüme yazdırma
                tvOldPrice.text = "${product.price} TL"

                // Ürünün adını görünüme yazdırma
                tvDealProductName.text = product.name
            }
        }
    }

    // ürünlerin karşılaştırılması için DiffUtil kullanılır
    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            // ürünlerin benzersiz kimlikleri aynı mı diye kontrol eder
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            // ürün içerikleri aynı mı diye kontrol eder
            return oldItem == newItem
        }
    }

    // AsyncListDiffer, ürün farklılıklarını hesaplar ve günceller
    val differ = AsyncListDiffer(this, diffCallback)

    // Yeni bir görünüm tutucusu oluşturur ve döndürür
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    // Görünüm tutucusu ile veri bağlama işlevini çağırır
    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position] // Belirli bir pozisyondaki ürünü alır
        holder.bind(product) // Görünüm tutucusuna ürnü bağlar

        // ürüne tıklanıldığında tanımlı olan onClick işlevini çağırır
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    // ürün sayısını döndürür
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // ürünlere tıklanıldığında çağrılacak işlevi tanımlar
    var onClick: ((Product) -> Unit)? = null
}
