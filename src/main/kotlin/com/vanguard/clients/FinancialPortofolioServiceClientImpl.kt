package com.vanguard.clients

import com.fasterxml.jackson.databind.ObjectMapper
import com.vanguard.clients.api.FinancialPortofolioServiceClient
import com.vanguard.domain.Customer
import com.vanguard.domain.Portfolio
import com.vanguard.domain.PortfolioDiff
import com.vanguard.domain.Strategy
import com.vanguard.util.classLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class FinancialPortofolioServiceClientImpl(
    @Value("\${financial-protofolio-service.base-uri}") baseUri: String,
    private val objectMapper: ObjectMapper,
) : FinancialPortofolioServiceClient {

    private val webClient = WebClient.create(baseUri)

    override fun retrievePortfolios(customersAndStrategies: Iterable<Pair<Customer, Strategy>>) =
        Flux.fromIterable(customersAndStrategies)
            .flatMap { (customer, strategy) ->
                webClient.get()
                    .uri("/customer/${customer.customerId}")
                    .retrieve()
                    .bodyToFlux(Portfolio::class.java)
                    .map { portfolio -> Pair(strategy, portfolio) }
            }

    override fun updatePortfolios(portfolioDiffs: Flux<PortfolioDiff>): Mono<List<PortfolioDiff>> {
        return portfolioDiffs.collectList().flatMap { trades ->
            val body = objectMapper.writeValueAsString(trades)

            webClient.post()
                .uri("/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchangeToMono { response ->
                    if (response.statusCode().equals(HttpStatus.CREATED)) {
                        Mono.just(trades)
                    } else {
                        Mono.just(emptyList())
                    }
                }
        }
    }

    companion object {
        val LOGGER = classLogger()
    }
}
