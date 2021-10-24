package com.vanguard.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Component
import java.nio.file.Path

@Component
class CsvReader {

    val csvMapper: ObjectMapper = CsvMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())

    inline fun <reified T> readCsv(path: Path): List<T> {
        val fileContent = readFileFromResources(path)
        return csvMapper.readerFor(T::class.java)
            .with(CsvSchema.emptySchema().withHeader())
            .readValues<T>(fileContent)
            .readAll()
    }
}
