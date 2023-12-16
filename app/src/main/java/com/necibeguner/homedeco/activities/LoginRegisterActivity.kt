package com.necibeguner.homedeco.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.necibeguner.homedeco.R
import dagger.hilt.android.AndroidEntryPoint


// login register sayfasını ekrana getirir
@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)
    }
}