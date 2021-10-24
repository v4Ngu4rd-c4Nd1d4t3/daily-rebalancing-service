package com.vanguard.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any.classLogger(): Logger = LoggerFactory.getLogger(this::class.java)
