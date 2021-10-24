package com.vanguard.application.api

import java.nio.file.Path

interface StrategyServiceUpdate {

    fun updateFromFile(filePath: Path)
}
