package com.vanguard.domain

data class PortfolioDiff(
    val customerId: Int,
    val stocks: Int,
    val bonds: Int,
    val cash: Int,
)
