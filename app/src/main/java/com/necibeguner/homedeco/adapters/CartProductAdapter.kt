package com.necibeguner.homedeco.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.necibeguner.homedeco.data.CartProduct
import com.necibeguner.homedeco.databinding.CartProductItemBinding
import com.necibeguner.homedeco.helper.getProductPrice

class CartProductAdapter: RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    // Her bir öğe için bir görünüm tutucusu tanımlanmıştır.
    inner class CartProductsViewHolder(val binding: CartProductItemBinding):
        RecyclerView.ViewHolder(binding.root){

        // Veri bağlama işlevi: Öğelerin görünümünü günceller.
        fun bind(cartProduct: CartProduct){
            binding.apply {
                // Glide kütüphanesiyle ürün resmini yükleme
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageCartProduct)

                // Ürün adını görünümde ayarlama
                tvProductCartName.text = cartProduct.product.name

                // Sepetteki ürünün miktarını görünümde ayarlama
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                // İndirim yüzdesine göre fiyatı hesaplayıp görünüme yazdırma
                val priceAfterPercentage = cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)
                tvProductCartPrice.text = "${String.format("%.2f", priceAfterPercentage)} TL"

                // Seçilen rengi görünüme ayarlama, eğer yoksa varsayılan rengi göster
                imageCartProductColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor ?: Color.TRANSPARENT))

                // Seçilen boyutu görünüme ayarlama, eğer yoksa varsayılanı göster
                tvCartProductSize.text = cartProduct.selectedSize ?: "".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }
            }
        }
    }

    // Öğelerin karşılaştırılması için DiffUtil kullanılır
    private val diffCallback = object: DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // Öğelerin benzersiz kimlikleri aynı mı diye kontrol eder
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            // Öğe içerikleri aynı mı diye kontrol eder
            return oldItem == newItem
        }
    }

    // AsyncListDiffer, öğe farklılıklarını hesaplar ve günceller
    val differ = AsyncListDiffer(this,diffCallback)

    // Yeni bir görünüm tutucusu oluşturur ve döndürür
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    // Görünüm tutucusu ile veri bağlama işlevini çağırır
    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position] // Belirli bir pozisyondaki öğeyi alır
        holder.bind(cartProduct) // Görünüm tutucusuna öğeyi bağlar

        // Öğeye tıklanıldığında tanımlı olan onProductClick işlevini çağırır
        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        // Artı butonuna tıklanıldığında tanımlı olan onPlusClick işlevini çağırır
        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        // Eksi butonuna tıklanıldığında tanımlı olan onMinusClick işlevini çağırır
        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    // Öğe sayısını döndürür
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Öğelere tıklanıldığında çağrılacak işlevi tanımlar
    var onProductClick:((CartProduct) -> Unit) ?= null

    // Artı butonuna tıklanıldığında çağrılacak işlevi tanımlar
    var onPlusClick:((CartProduct) -> Unit) ?= null

    // Eksi butonuna tıklanıldığında çağrılacak işlevi tanımlar
    var onMinusClick:((CartProduct) -> Unit) ?= null
}

