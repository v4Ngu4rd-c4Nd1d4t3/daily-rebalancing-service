package com.vanguard.jobs

import com.vanguard.application.api.DataSourceObserver
import com.vanguard.util.classLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SchedulingJob(private val dataSourceObservers: List<DataSourceObserver>) {

    @Scheduled(fixedDelayString = "\${scheduling.schedule}")
    fun scheduleUpdate() {
        LOGGER.info("Triggering scheduled update")

        dataSourceObservers.forEach { it.updated() }
    }

    companion object {
        val LOGGER = classLogger()
    }

}