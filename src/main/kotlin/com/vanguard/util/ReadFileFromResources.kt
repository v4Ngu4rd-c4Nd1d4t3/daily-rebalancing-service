package com.vanguard.util

import org.springframework.util.ResourceUtils
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path

fun readFileFromResources(path: Path): String {
    try {
        val file = ResourceUtils.getFile("classpath:$path")
        return String(FileInputStream(file).readAllBytes())
    } catch (FileNotFoundException: Exception) {
        throw FileNotFoundException("Error loading $path")
    }
}
