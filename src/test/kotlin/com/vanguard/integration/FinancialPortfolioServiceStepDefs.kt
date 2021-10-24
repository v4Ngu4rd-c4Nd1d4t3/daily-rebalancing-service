package com.vanguard.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.vanguard.domain.Portfolio
import com.vanguard.domain.PortfolioDiff
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.net.URI
import java.time.Duration

class FinancialPortfolioServiceStepDefs(
    @Value("\${financial-protofolio-service.base-uri}") baseUri: URI,
    private val objectMapper: ObjectMapper,
) : En {

    private val mockServer = WireMockServer(
        wireMockConfig().port(baseUri.port).gzipDisabled(true).notifier(ConsoleNotifier(true))
    )

    init {
        Before { _ ->
            mockServer.start()
        }

        After { _ ->
            mockServer.stop()
        }

        When("Financial Portfolio Service returns portfolios") { dataTable: DataTable ->
            val portfoliosRaw = dataTable.asLists()
            portfoliosRaw.forEach { portfolioRow ->
                val (customerId, stocks, bonds, cash) = portfolioRow.map { value -> value.toInt() }
                val portfolio = Portfolio(customerId, stocks, bonds, cash)

                mockServer.stubFor(
                    get(urlPathEqualTo("/customer/$customerId"))
                        .willReturn(
                            aResponse()
                                .withHeader(
                                    org.springframework.http.HttpHeaders.CONTENT_TYPE,
                                    MediaType.APPLICATION_JSON_VALUE
                                )
                                .withBody(objectMapper.writeValueAsString(portfolio))
                        )
                )
            }
        }

        When("Financial Portfolio Service accepts all trades") {
            mockServer.stubFor(
                post(urlPathEqualTo("/execute"))
                    .willReturn(
                        aResponse()
                            .withStatus(HttpStatus.CREATED.value())
                    )
            )
        }

        Then("Financial Portfolio Service received trades") { dataTable: DataTable ->
            val reader = objectMapper.readerForListOf(PortfolioDiff::class.java)

            val executedTrades = mockServer.findAll(postRequestedFor(urlPathEqualTo("/execute")))
                .flatMap {
                    val executedTrades: List<PortfolioDiff> = reader.readValue(it.body)
                    executedTrades
                }

            val tradesRaw = dataTable.asLists()
            tradesRaw.forEach { tradeRow ->
                val (customerId, stocks, bonds, cash) = tradeRow.map { value -> value.toInt() }
                val requiredTrade = PortfolioDiff(customerId, stocks, bonds, cash)

                assertTrue(executedTrades.contains(requiredTrade))
            }
        }

        Then("Wait {int} seconds") { waitSeconds: Int ->
            Thread.sleep(Duration.ofSeconds(waitSeconds.toLong()).toMillis())
        }
    }
}
