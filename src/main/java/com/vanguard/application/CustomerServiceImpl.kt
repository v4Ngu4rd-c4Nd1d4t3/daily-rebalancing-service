package com.vanguard.application

import com.vanguard.application.api.CustomerServiceUpdate
import com.vanguard.domain.Customer
import com.vanguard.util.CsvReader
import com.vanguard.util.classLogger
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class CustomerServiceImpl(private val csvReader: CsvReader) : CustomerServiceUpdate {

    private var customers: List<Customer>? = null

    override fun updateFromFile(filePath: Path) {
        customers = csvReader.readCsv(filePath)

        LOGGER.info("Successfully updated customers from $filePath")
    }

    companion object {
        private val LOGGER = classLogger()
    }
}
