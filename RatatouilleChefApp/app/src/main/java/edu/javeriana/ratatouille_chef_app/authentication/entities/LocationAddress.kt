package edu.javeriana.ratatouille_chef_app.authentication.entities

import com.google.firebase.firestore.GeoPoint


data class LocationAddress(
    val address: String = "",
    val location: GeoPoint? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)