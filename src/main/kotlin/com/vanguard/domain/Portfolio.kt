package com.vanguard.domain

data class Portfolio(
    val customerId: Int,
    val stocks: Int,
    val bonds: Int,
    val cash: Int,
)
