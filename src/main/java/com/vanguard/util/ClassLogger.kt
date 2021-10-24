package com.vanguard.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun Any.classLogger(): Logger = LoggerFactory.getLogger(getClassName(this::class.java))

private fun <T : Any> getClassName(clazz: Class<T>) = clazz.name.removeSuffix("\$Companion")
