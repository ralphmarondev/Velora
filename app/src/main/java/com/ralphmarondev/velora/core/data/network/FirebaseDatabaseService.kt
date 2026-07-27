package com.ralphmarondev.velora.core.data.network

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ralphmarondev.velora.core.data.network.dto.TrafficRecordDto
import com.ralphmarondev.velora.core.data.network.mapper.toTrafficRecord
import com.ralphmarondev.velora.core.domain.model.TrafficRecord
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseDatabaseService(
    private val database: FirebaseDatabase
) {
    private val trafficRef = database.reference
        .child("traffic")

    fun observeTrafficRecord(): Flow<TrafficRecord?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recordDto = snapshot.getValue(TrafficRecordDto::class.java)
                Log.d("FirebaseDatabaseService", "Received: $recordDto")
                trySend(recordDto?.toTrafficRecord())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseDatabaseService", "Cancelled: ${error.message}", error.toException())
                close(error.toException())
            }
        }

        trafficRef.addValueEventListener(listener)
        awaitClose {
            trafficRef.removeEventListener(listener)
        }
    }
}