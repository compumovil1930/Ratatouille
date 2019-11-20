package edu.javeriana.ratatouille_chef_app.authentication.entities

import com.google.firebase.firestore.GeoPoint


data class LocationAddress(
    val address: String = "",
    val location: GeoPoint? = null
)