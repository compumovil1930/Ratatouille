package edu.javeriana.ratatouille_chef_app.authentication.entities

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties
import edu.javeriana.ratatouille_chef_app.core.HasId


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
    val chef: Boolean = false,
    val recipes: List<DocumentReference> = emptyList(),
    val ratapoints: Int = 0, override var id: String = ""
): HasId