package com.necibeguner.homedeco.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

//HomeViewpagerAdapter sınıfı, ViewPager içinde gösterilecek fragmentlerin listesini alarak,
// bu fragmentleri yönetmek için bir adaptör oluşturur.
class HomeViewpagerAdapter(
    private val fragments: List<Fragment>, // Gösterilecek fragmentlerin listesi
    fm: FragmentManager, //fm parametresi, fragmentlerin yönetimini sağlayan bir FragmentManager nesnesidir.
    lifecycle: Lifecycle //lifecycle parametresi, adaptörün yaşam döngüsünü takip etmesini sağlayan bir Lifecycle nesnesidir
) : FragmentStateAdapter(fm, lifecycle) {

    // Toplam fragment sayısını döndüren işlev
    override fun getItemCount(): Int {
        return fragments.size
    }

    // Belirli bir konum için fragment oluşturan işlev
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
