package edu.javeriana.ratatouille_chef_app.client_requests.entities

import com.google.firebase.firestore.DocumentReference
import edu.javeriana.ratatouille_chef_app.core.HasId

data class Recipe(
    override  var id: String = "",
    val name: String = "",
    val description: String = "",
    val ingredients: List<DocumentReference> = emptyList(),
    val utensils: List<DocumentReference> = emptyList()
) : HasId