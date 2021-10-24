package com.vanguard.application.api

import java.nio.file.Path

interface CustomerServiceUpdate {

    fun updateFromFile(filePath: Path)
}
