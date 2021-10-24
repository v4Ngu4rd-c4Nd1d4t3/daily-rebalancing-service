package com.vanguard.domain

import java.time.LocalDate

data class Customer(
    val customerId: Int,
    val email: String,
    val dateOfBirth: LocalDate,
    val riskLevel: Int,
    val retirementAge: Int,
)
