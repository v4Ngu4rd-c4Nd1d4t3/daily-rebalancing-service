package com.vanguard.application

import com.vanguard.application.api.StrategyServiceUpdate
import com.vanguard.domain.Strategy
import com.vanguard.util.CsvReader
import com.vanguard.util.classLogger
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class StrategyServiceImpl(private val csvReader: CsvReader) : StrategyServiceUpdate {

    private var strategies: List<Strategy>? = null

    override fun updateFromFile(filePath: Path) {
        strategies = csvReader.readCsv(filePath)

        LOGGER.info("Successfully updated strategies from $filePath")
    }

    companion object {
        private val LOGGER = classLogger()
    }
}
