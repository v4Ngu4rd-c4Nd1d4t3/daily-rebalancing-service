package com.vanguard

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class DailyRebalancingService {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DailyRebalancingService::class.java, *args)
        }
    }
}
