package com.vanguard.integration

import io.cucumber.java8.En
import org.springframework.test.web.reactive.server.WebTestClient

class AdminControllerStepDefs(private val webTestClient: WebTestClient) : En {

    init {
        Then("Return {int} when requesting to load customers from {word}") { httpStatus: Int, filePath: String ->
            val response = webTestClient.put()
                .uri { uriBuilder ->
                    uriBuilder.path("/api/admin/customers")
                        .queryParam("file_path", filePath)
                        .build()
                }
                .exchange()

            response.expectStatus().isEqualTo(httpStatus)
        }

        Then("Return {int} when requesting to load strategies from {word}") { httpStatus: Int, filePath: String ->
            val response = webTestClient.put()
                .uri { uriBuilder ->
                    uriBuilder.path("/api/admin/strategies")
                        .queryParam("file_path", filePath)
                        .build()
                }
                .exchange()

            response.expectStatus().isEqualTo(httpStatus)
        }
    }
}
