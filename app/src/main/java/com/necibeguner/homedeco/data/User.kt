package com.necibeguner.homedeco.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath: String = ""
):Parcelable{
    constructor() : this("","","","")
}
