package edu.javeriana.ratatouille_chef_app.client_requests.entities

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

data class Request(
    val id: String = "",
    val chefId: DocumentReference? = null,
    val clientId: DocumentReference? = null,
    val clientLocation: GeoPoint = GeoPoint(0.0, 0.0),
    val description: String = ""
)