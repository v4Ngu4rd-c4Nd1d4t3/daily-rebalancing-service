package com.vanguard.application.api

import com.vanguard.domain.Customer

interface CustomerService : DataSource {
    fun getCustomers(): List<Customer>
}
