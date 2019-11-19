package edu.javeriana.ratatouille_chef_app.authentication.entities

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class User(
    val address: LocationAddress = LocationAddress(),
    val age: Int = 0,
    val ratapoints: Int = 0,
    val isChef: Boolean = true,
    val isAvailable: Boolean = false,
    val email: String = "",
    val fullName: String = "",
    val photoUrl: String? = null,
    val biography: String = "",
    val utensils: List<String> = listOf(),
    val password: String = "",
    val yearsOfExperience: Int = 0,
    val currentAddress: GeoPoint = GeoPoint(0.0, 0.0)

)