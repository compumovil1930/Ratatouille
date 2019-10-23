package edu.javeriana.ratatouille_chef_app.authentication.entities

import android.net.Uri
import android.provider.ContactsContract
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class User(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val address: LocationAddress = LocationAddress(),
    val biography: String = "",
    val age: Int = 0,
    val yearsOfExperience: Int = 0,
    val utensils: List<String> = listOf(),
    val photoUrl: String? = null,
    val available: Boolean = false,
    val currentAddress: GeoPoint = GeoPoint(0.0, 0.0)

)