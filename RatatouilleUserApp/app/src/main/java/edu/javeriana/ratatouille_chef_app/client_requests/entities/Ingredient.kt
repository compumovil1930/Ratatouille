package edu.javeriana.ratatouille_chef_app.client_requests.entities

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.core.HasId
import java.util.*

data class Ingredient(
    override  var id: String = "",
    val name:String = "",
    val cost:Int = 0
) : HasId