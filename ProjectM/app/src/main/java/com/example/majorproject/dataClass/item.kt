package com.example.majorproject.dataClass

import java.io.Serializable

data class item(
    val image: String,
    val name: String,
    val price: String,
    val style: String = "",
    val product: Product // Reference to the associated Product
) : Serializable
