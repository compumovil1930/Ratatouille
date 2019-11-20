package edu.javeriana.ratatouille_chef_app.authentication.entities

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class User(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val address: LocationAddress = LocationAddress(),
    val age: Int = 0,
    val utensils: List<DocumentReference> = emptyList(),
    val photoUrl: String? = null,
    val available: Boolean = false,
    val currentAddress: GeoPoint = GeoPoint(0.0, 0.0),
    val biography: Biography? = null,
    val isChef: Boolean = true,
    val recipes: List<DocumentReference> = emptyList(),
    val ratapoints: Int = 0
)