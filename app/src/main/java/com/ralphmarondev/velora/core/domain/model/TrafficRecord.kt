package com.ralphmarondev.velora.core.domain.model

data class TrafficRecord(
    val isCongested: Boolean = false,
    val isUnderConstruction: Boolean = false,
    val timestamp: Long = 0L
)