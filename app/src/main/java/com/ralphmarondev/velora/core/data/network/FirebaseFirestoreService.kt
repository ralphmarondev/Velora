package com.ralphmarondev.velora.core.data.network

import com.google.firebase.firestore.FirebaseFirestore
import com.ralphmarondev.velora.features.auth.domain.model.User
import kotlinx.coroutines.tasks.await

class FirebaseFirestoreService(
    private val fireStore: FirebaseFirestore
) {
    suspend fun createUser(user: User) {
        fireStore.collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "uid" to user.uid,
                    "email" to user.email,
                    "displayName" to user.displayName,
                    "createDate" to user.createDate,
                    "imagePath" to user.imagePath
                )
            )
            .await()
    }

    suspend fun readUser(uid: String): User {
        val doc = fireStore.collection("users")
            .document(uid)
            .get()
            .await()

        val user = User(
            uid = doc.getString("uid") ?: "",
            email = doc.getString("email") ?: "",
            displayName = doc.getString("displayName") ?: "",
            createDate = doc.getLong("createDate") ?: 0, // invalid date
            imagePath = doc.getLong("imagePath")?.toInt() ?: 1
        )
        return user
    }
}