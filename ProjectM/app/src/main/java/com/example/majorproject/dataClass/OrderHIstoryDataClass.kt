package com.example.majorproject.dataClass

data class OrderHistory(
    val orderId: String = "",
    val userEmail: String = "",
    val deliveryFee: Double = 0.0,
    val subTotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0
)
