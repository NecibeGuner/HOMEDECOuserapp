package com.necibeguner.homedeco.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.necibeguner.homedeco.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.SizesViewHolder>() {
    //SizesViewHolder iç içe sınıfı, RecyclerView'da her bir boyut öğesini temsil eden görünüm
    // tutucularını (view holder) yönetir ve boyut verilerini bağlar.

    // Seçili boyutun pozisyonunu(index) izlemek için kullanılır, başlangıçta seçili bir pozisyon yok (-1).
    private var selectedPosition = -1

    inner class SizesViewHolder(private val binding: SizeRvItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Boyutları bağlama işlemi
        fun bind(size: String, position: Int) {
            binding.tvSize.text = size

            // Eğer bu pozisyon seçili pozisyona eşitse
            if (position == selectedPosition) {
                binding.apply {
                    // Görünürlüğü ayarla: Boyut seçildiyse, gölgeyi görünür yap
                    imageShadow.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    // Boyut seçilmediyse, gölgeyi gizle
                    imageShadow.visibility = View.INVISIBLE
                }
            }
        }
    }

    // Boyut öğeleri arasındaki farkları belirlemek için DiffUtil kullanılır
    //differCallback, RecyclerView'daki değişiklikleri algılamak için kullanılan DiffUtil.ItemCallback'i uygular.
    private val differCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem // Öğeler aynı mı?
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem // Öğe içerikleri aynı mı?
        }
    }

    // Liste üzerinde asenkron farkları işlemek için AsyncListDiffer
    val differ = AsyncListDiffer(this, differCallback)
    //differ, asenkron olarak farkları işlemek için AsyncListDiffer nesnesini tanımlar.

    //onCreateViewHolder, bir görünüm tutucu oluşturur ve bağlama işlemi yapar.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    //onBindViewHolder, boyut verilerini bağlar ve tıklama olaylarını işler.
    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        // Her bir boyut öğesine tıklama olayını ekleme
        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0)
                notifyItemChanged(selectedPosition) // Önceki seçili boyutun durumunu güncelle

            selectedPosition = holder.adapterPosition // Yeni seçilen boyutun pozisyonunu güncelle
            notifyItemChanged(selectedPosition) // Yeni seçilen boyutun durumunu güncelle

            onItemClick?.invoke(size) // Boyut üzerinde tıklama işlevselliğini tetikleme
        }
    }

    //getItemCount, listenin mevcut öğe sayısını döndürür.
    override fun getItemCount(): Int {
        return differ.currentList.size // Listenin mevcut öğe sayısını döndürme
    }

    // Boyut öğesine tıklama olayını işleyen lambda ifadesi
    var onItemClick: ((String) -> Unit)? = null
}
