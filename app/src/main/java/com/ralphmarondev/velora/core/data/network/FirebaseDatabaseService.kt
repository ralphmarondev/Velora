package com.ralphmarondev.velora.core.data.network

import com.google.firebase.database.FirebaseDatabase
import com.ralphmarondev.velora.core.domain.model.TrafficRecord
import kotlinx.coroutines.tasks.await

class FirebaseDatabaseService(
    private val database: FirebaseDatabase
) {
    private val trafficRef = database.reference
        .child("traffic")

    suspend fun readLatestTrafficRecord(): TrafficRecord? {
        val snapshot = trafficRef
            .orderByChild("timestamp")
            .limitToLast(1)
            .get()
            .await()
        return snapshot.children.firstOrNull()
            ?.getValue(TrafficRecord::class.java)
    }
}