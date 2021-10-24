package com.vanguard.jobs

import com.vanguard.application.api.ReblancingProvider
import com.vanguard.clients.api.FinancialPortofolioServiceClient
import com.vanguard.util.classLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.time.Duration

@Component
class PortfolioRebalancingJob(
    private val rebalancingProvider: ReblancingProvider,
    private val financialPortofolioServiceClient: FinancialPortofolioServiceClient,
    @Value("\${jobs.timeout}") private val jobTimeout: Duration,
    @Value("\${financial-protofolio-service.batch-size}") private val batchSize: Int,
) {

    @Scheduled(fixedDelayString = "\${jobs.interval-millis}")
    fun rebalance() {

        val pendingRebalances = rebalancingProvider.pendingRebalances()

        Flux.fromIterable(pendingRebalances.asIterable().chunked(batchSize))
            .map { batch ->
                val trades = financialPortofolioServiceClient
                    .retrievePortfolios(batch)
                    .map { (strategy, portfolio) -> rebalancingProvider.computeDifference(portfolio, strategy) }

                financialPortofolioServiceClient
                    .updatePortfolios(trades)
                    .map { finishedTrade ->
                        val finishedTradesFor = finishedTrade.map { it.customerId }

                        LOGGER.info("Finished trades for customers with ids: $finishedTradesFor")

                        rebalancingProvider.removeFinishedRebalancing(finishedTradesFor)
                        finishedTradesFor
                    }.subscribe()
            }
            .blockLast(jobTimeout)
    }

    companion object {
        val LOGGER = classLogger()
    }
}
