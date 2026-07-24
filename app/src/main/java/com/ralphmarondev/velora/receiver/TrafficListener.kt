package com.ralphmarondev.velora.receiver

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class TrafficListener(
    private val notificationHelper: NotificationHelper
) {

    private val trafficRef = FirebaseDatabase
        .getInstance()
        .getReference("traffic")

    private var initialized = false

    fun start() {

        // Ignore existing records
        trafficRef.get().addOnSuccessListener {
            initialized = true
        }

        trafficRef.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) {
                if (!initialized) return

                notificationHelper.showNotification(
                    title = "Traffic Alert",
                    message = "A new traffic event has been detected."
                )
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) = Unit

            override fun onChildRemoved(
                snapshot: DataSnapshot
            ) = Unit

            override fun onChildMoved(
                snapshot: DataSnapshot,
                previousChildName: String?
            ) = Unit

            override fun onCancelled(
                error: DatabaseError
            ) {
                error.toException().printStackTrace()
            }
        })
    }
}