package com.ralphmarondev.velora.core.data.network

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
                trySend(snapshot.getValue(TrafficRecord::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        trafficRef.addValueEventListener(listener)
        awaitClose {
            trafficRef.removeEventListener(listener)
        }
    }
}