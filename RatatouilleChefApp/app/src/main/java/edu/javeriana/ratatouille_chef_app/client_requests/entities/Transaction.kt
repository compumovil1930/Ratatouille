package edu.javeriana.ratatouille_chef_app.client_requests.entities

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import java.util.*

data class Transaction(
    val id: String = "",
    val chefId: DocumentReference? = null,
    val clientId: DocumentReference? = null,
    val address: GeoPoint = GeoPoint(0.0, 0.0),
    val cost: Int = 0,
    val time: Date? = null,
    val rating: Int = 0,
    val comment: String = "",
    val state: String = "",
    val recipe: DocumentReference? = null
)