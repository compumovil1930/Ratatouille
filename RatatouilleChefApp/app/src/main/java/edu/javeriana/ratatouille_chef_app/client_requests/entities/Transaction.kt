package edu.javeriana.ratatouille_chef_app.client_requests.entities

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.core.HasId
import java.util.*

data class Transaction(
    override  var id: String = "",
    val chefId: DocumentReference? = null,
    val clientId: DocumentReference? = null,
    val address: GeoPoint = GeoPoint(0.0, 0.0),
    val cost: Float = 0.0f,
    val time: Date? = null,
    val rating: Int = 0,
    val comment: String = "",
    val state: String = "",
    val recipe: DocumentReference? = null
) : HasId

enum class StateTransaction(val value: String) {
    ACCEPTED("ACCEPTED"),
    PENDING("PENDING"),
    COMPLETE("COMPLETE")
}