package com.vanguard.application.api

import com.vanguard.domain.Customer
import com.vanguard.domain.Portfolio
import com.vanguard.domain.PortfolioDiff
import com.vanguard.domain.Strategy

interface ReblancingProvider {
    fun pendingRebalances(): Iterable<Pair<Customer, Strategy>>
    fun removeFinishedRebalancing(customerIds: List<Int>)
    fun computeDifference(portfolio: Portfolio, strategy: Strategy): PortfolioDiff
}
