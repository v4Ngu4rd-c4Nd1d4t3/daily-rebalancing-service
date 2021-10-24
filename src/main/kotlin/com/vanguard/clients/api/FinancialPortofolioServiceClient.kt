package com.vanguard.clients.api

import com.vanguard.domain.Customer
import com.vanguard.domain.Portfolio
import com.vanguard.domain.PortfolioDiff
import com.vanguard.domain.Strategy
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FinancialPortofolioServiceClient {
    fun retrievePortfolios(customersAndStrategies: Iterable<Pair<Customer, Strategy>>): Flux<Pair<Strategy, Portfolio>>

    fun updatePortfolios(portfolioDiffs: Flux<PortfolioDiff>): Mono<List<PortfolioDiff>>
}
