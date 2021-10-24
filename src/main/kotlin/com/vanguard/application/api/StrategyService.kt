package com.vanguard.application.api

import com.vanguard.domain.Strategy

interface StrategyService : DataSource {
    fun getStrategies(): List<Strategy>
}
