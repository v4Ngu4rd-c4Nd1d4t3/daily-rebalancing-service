package com.vanguard.controllers

import com.vanguard.application.api.CustomerServiceUpdate
import com.vanguard.application.api.StrategyServiceUpdate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.FileNotFoundException
import kotlin.io.path.Path

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val customerServiceUpdate: CustomerServiceUpdate,
    private val strategyServiceUpdate: StrategyServiceUpdate
) {

    @PutMapping(path = ["/customers"])
    fun updateCustomers(@RequestParam("file_path") filePath: String): ResponseEntity<String> {
        return try {
            customerServiceUpdate.updateFromFile(Path(filePath))
            ResponseEntity.ok().build()
        } catch (fileNotFoundException: FileNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(fileNotFoundException.message)
        }
    }

    @PutMapping(path = ["/strategies"])
    fun updateStrategy(@RequestParam("file_path") filePath: String): ResponseEntity<String> {
        return try {
            strategyServiceUpdate.updateFromFile(Path(filePath))
            ResponseEntity.ok().build()
        } catch (fileNotFoundException: FileNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(fileNotFoundException.message)
        }
    }
}
