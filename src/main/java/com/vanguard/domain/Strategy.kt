package com.vanguard.domain

data class Strategy(
    val strategyId: Int,
    val minRiskLevel: Int,
    val maxRiskLevel: Int,
    val minYearsToRetirement: Int,
    val maxYearsToRetirement: Int,
    val stocksPercentage: Int,
    val cashPercentage: Int,
    val bondsPercentage: Int,
)
