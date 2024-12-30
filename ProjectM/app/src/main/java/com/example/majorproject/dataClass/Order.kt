package com.example.majorproject.dataClass

data class Order(
    val name: String,
    val price: Comparable<*>,
    val quantity: String?
)
