package com.vanguard.application

import com.vanguard.application.api.CustomerService
import com.vanguard.application.api.DataSourceObserver
import com.vanguard.application.api.ReblancingProvider
import com.vanguard.application.api.StrategyService
import com.vanguard.domain.Customer
import com.vanguard.domain.Portfolio
import com.vanguard.domain.PortfolioDiff
import com.vanguard.domain.Strategy
import com.vanguard.util.classLogger
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.Period
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.absoluteValue

@Component
class RebalancingService(
    private val customerService: CustomerService,
    private val strategyService: StrategyService,
) : DataSourceObserver, ReblancingProvider {

    private var pendingRebalanances: ConcurrentHashMap<Int, Pair<Customer, Strategy>> = ConcurrentHashMap()

    init {
        customerService.registerObserver(this)
        strategyService.registerObserver(this)
    }

    override fun updated() {
        LOGGER.info("Data sources updated. Considering re-balancing")

        val customers = customerService.getCustomers()
        val strategies = strategyService.getStrategies()

        if (customers.isNotEmpty() && strategies.isNotEmpty()) {
            replaceOpenRebalances(customers, strategies)
        }
    }

    private fun replaceOpenRebalances(
        customers: List<Customer>,
        strategy: List<Strategy>,
    ) {
        pendingRebalanances = determineStrategies(customers, strategy)
    }

    private fun determineStrategies(
        customers: List<Customer>,
        strategies: List<Strategy>
    ): ConcurrentHashMap<Int, Pair<Customer, Strategy>> {
        val fallbackStrategy = Strategy(0, Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE, 0, 100, 0)

        val strategiesWithFallback = strategies + fallbackStrategy

        val strategiesPerCustomer = customers
            .associate { customer ->
                customer.customerId to (customer to strategiesWithFallback.getFor(customer))
            }

        return ConcurrentHashMap(strategiesPerCustomer)
    }

    override fun pendingRebalances() = pendingRebalanances.values

    override fun removeFinishedRebalancing(customerIds: List<Int>) {
        customerIds.forEach { customerId -> pendingRebalanances.remove(customerId) }
    }

    override fun computeDifference(portfolio: Portfolio, strategy: Strategy): PortfolioDiff {
        val totalNumberOfAssets = portfolio.stocks + portfolio.bonds + portfolio.cash

        val portfolioDiff = PortfolioDiff(
            portfolio.customerId,
            differenceToTarget(portfolio.stocks, totalNumberOfAssets, strategy.stocksPercentage),
            differenceToTarget(portfolio.bonds, totalNumberOfAssets, strategy.bondsPercentage),
            differenceToTarget(portfolio.cash, totalNumberOfAssets, strategy.cashPercentage),
        )

        LOGGER.info("Computed portfolio diff as $portfolioDiff from portfolio $portfolio and strategy $strategy")

        return portfolioDiff
    }

    private fun differenceToTarget(numberOfAssets: Int, totalNumberOfAssets: Int, targetPercentage: Int): Int {
        val targetNumberOfAssets = (totalNumberOfAssets * targetPercentage) / 100
        return targetNumberOfAssets - numberOfAssets
    }

    companion object {
        val LOGGER = classLogger()
    }
}

private fun List<Strategy>.getFor(customer: Customer) = this.first { strategy -> strategy.applicableFor(customer) }

private fun Customer.yearsToRetirement(): Int {
    val dateToday = LocalDate.now()
    require(dateToday >= this.dateOfBirth)

    val age = Period.between(this.dateOfBirth, dateToday).years
    return (this.retirementAge - age).absoluteValue
}

private fun Strategy.applicableFor(customer: Customer) =
    this.minRiskLevel <= customer.riskLevel &&
        customer.riskLevel <= this.maxRiskLevel &&
        this.minYearsToRetirement <= customer.yearsToRetirement() &&
        customer.yearsToRetirement() <= this.maxYearsToRetirement
