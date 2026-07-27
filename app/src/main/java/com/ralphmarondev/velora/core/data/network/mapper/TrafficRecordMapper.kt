package com.ralphmarondev.velora.core.data.network.mapper

import com.ralphmarondev.velora.core.data.network.dto.TrafficRecordDto
import com.ralphmarondev.velora.core.domain.model.TrafficRecord

fun TrafficRecordDto.toTrafficRecord(): TrafficRecord {
    return TrafficRecord(
        isCongested = isCongested,
        isUnderConstruction = isUnderConstruction,
        timestamp = timestamp
    )
}