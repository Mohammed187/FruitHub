package com.example.fruithub.model

data class Order(
        val id: String? = null,
        val list: List<Basket>? = null,
        val address: String? = null,
        val payment: String? = null,
        val status: String? = null,
        val created: String? = null
)
