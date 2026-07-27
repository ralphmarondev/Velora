package com.ralphmarondev.velora.core.data.network.dto

import com.google.firebase.database.PropertyName

data class TrafficRecordDto(
    @get:PropertyName("isCongested")
    @set:PropertyName("isCongested")
    var isCongested: Boolean = false,

    @get:PropertyName("isUnderConstruction")
    @set:PropertyName("isUnderConstruction")
    var isUnderConstruction: Boolean = false,

    var timestamp: Long = 0L
)