package com.vanguard.application

import com.vanguard.application.api.CustomerService
import com.vanguard.application.api.CustomerServiceUpdate
import com.vanguard.application.api.DataSourceObserver
import com.vanguard.domain.Customer
import com.vanguard.util.CsvReader
import com.vanguard.util.classLogger
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class CustomerServiceImpl(private val csvReader: CsvReader) : CustomerServiceUpdate, CustomerService {

    private var customers: List<Customer> = emptyList()
    private var observers: MutableList<DataSourceObserver> = mutableListOf()

    override fun updateFromFile(filePath: Path) {
        customers = csvReader.readCsv(filePath)
        observers.forEach { it.updated() }

        LOGGER.info("Successfully updated customers from $filePath")
    }

    override fun getCustomers() = customers

    override fun registerObserver(observer: DataSourceObserver) {
        observers.add(observer)
    }

    companion object {
        private val LOGGER = classLogger()
    }
}
