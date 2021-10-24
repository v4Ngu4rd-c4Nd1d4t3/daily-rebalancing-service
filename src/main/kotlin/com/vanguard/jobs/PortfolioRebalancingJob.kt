package com.vanguard.jobs

import com.vanguard.application.api.ReblancingProvider
import com.vanguard.clients.api.FinancialPortofolioServiceClient
import com.vanguard.util.classLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeoutException

@Component
class PortfolioRebalancingJob(
    private val rebalancingProvider: ReblancingProvider,
    private val financialPortofolioServiceClient: FinancialPortofolioServiceClient,
    @Value("\${jobs.timeout}") private val jobTimeout: Int,
    @Value("\${financial-protofolio-service.batch-size}") private val batchSize: Int,
) {

    @Scheduled(fixedDelayString = "\${jobs.interval-millis}")
    fun rebalance() {
        val startTime = System.currentTimeMillis()

        val pendingRebalances = rebalancingProvider.pendingRebalances()
        for (batch in pendingRebalances.asIterable().chunked(batchSize)) {
            if (System.currentTimeMillis() - startTime > jobTimeout) {
                throw TimeoutException("Rebalancing job timed out after $jobTimeout ms")
            }

            val trades = financialPortofolioServiceClient
                .retrievePortfolios(batch)
                .map { (strategy, portfolio) -> rebalancingProvider.computeDifference(portfolio, strategy) }

            financialPortofolioServiceClient
                .updatePortfolios(trades)
                .map { finishedTrade ->
                    finishedTrade.forEach { portfolioDiff ->
                        rebalancingProvider.removeFinishedRebalancing(portfolioDiff.customerId)
                    }
                }.subscribe {
                    LOGGER.info("Rebalancing finished for ${batch.map { it.first }}")
                }
        }
    }

    companion object {
        val LOGGER = classLogger()
    }
}
