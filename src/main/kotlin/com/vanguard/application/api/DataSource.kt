package com.vanguard.application.api

interface DataSource {
    fun registerObserver(observer: DataSourceObserver)
}
