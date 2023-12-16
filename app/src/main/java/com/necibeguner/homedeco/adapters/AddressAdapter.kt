package com.necibeguner.homedeco.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.necibeguner.homedeco.R
import com.necibeguner.homedeco.data.Address
import com.necibeguner.homedeco.databinding.AddressRvItemBinding

class AddressAdapter : Adapter<AddressAdapter.AddressViewHolder>() {

    // İç sınıf: Her bir adres görünümünü tutan ViewHolder
    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        ViewHolder(binding.root) {

        // ViewHolder'a adres ve seçili olup olmadığı bilgisini bağlar
        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                // Adresin başlık bilgisini buton metnine atar
                buttonAddress.text = address.addressTitle

                // Eğer adres seçiliyse arka plan rengini mavi yapar, seçili değilse beyaz yapar
                if (isSelected){
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                } else {
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }
    }

    // Adresler arasındaki farklılıkları anlamak için DiffUtil kullanılır
    private val diffUtil = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            // Eski ve yeni adresin başlık ve tam adı aynı mı diye kontrol edilir
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            // İçerikler aynı mı diye kontrol edilir
            return oldItem == newItem
        }
    }

    // AsyncListDiffer, RecyclerView için farklılıkları asenkron olarak takip eder
    val differ = AsyncListDiffer(this, diffUtil)

    // ViewHolder oluşturulduğunda çalışan metot
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    // Seçili adresin tutulduğu değişken, başlangıçta -1 olarak atanır
    var selectedAddress = -1

    // ViewHolder'ı bağlama işlemi
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        // Her buton tıklandığında çalışacak olan işlemler
        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }

    // İlk oluşturulduğunda farklılık olduğunda seçili adresin güncellenmesi
    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    // Toplam öğe sayısını döndüren metot
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Adres butonuna tıklandığında çalışacak olan lambda ifadesi
    var onClick: ((Address) -> Unit)? = null
}
