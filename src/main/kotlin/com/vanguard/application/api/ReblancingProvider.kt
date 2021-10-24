package com.vanguard.application.api

import com.vanguard.domain.*

interface ReblancingProvider {
    fun pendingRebalances(): Iterable<Pair<Customer, Strategy>>
    fun removeFinishedRebalancing(customerIds: List<Int>)
    fun computeDifference(portfolio: Portfolio, strategy: Strategy): PortfolioDiff
}
