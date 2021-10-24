package com.vanguard.application

import com.vanguard.application.api.DataSourceObserver
import com.vanguard.application.api.StrategyService
import com.vanguard.application.api.StrategyServiceUpdate
import com.vanguard.domain.Strategy
import com.vanguard.util.CsvReader
import com.vanguard.util.classLogger
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class StrategyServiceImpl(private val csvReader: CsvReader) : StrategyServiceUpdate, StrategyService {

    private var strategies: List<Strategy> = emptyList()
    private var observers: MutableList<DataSourceObserver> = mutableListOf()

    override fun updateFromFile(filePath: Path) {
        strategies = csvReader.readCsv(filePath)
        observers.forEach { it.updated() }

        LOGGER.info("Successfully updated strategies from $filePath")
    }

    override fun getStrategies() = strategies

    override fun registerObserver(observer: DataSourceObserver) {
        observers.add(observer)
    }

    companion object {
        private val LOGGER = classLogger()
    }
}
