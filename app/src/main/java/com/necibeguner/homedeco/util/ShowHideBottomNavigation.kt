package com.necibeguner.homedeco.util

import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.necibeguner.homedeco.activities.ShoppingActivity

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.necibeguner.homedeco.R.id.bottomNavigation)
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(com.necibeguner.homedeco.R.id.bottomNavigation)
    bottomNavigationView.visibility = android.view.View.VISIBLE
}