package edu.javeriana.ratatouille_chef_app.profile.entities

import com.google.firebase.firestore.IgnoreExtraProperties
import edu.javeriana.ratatouille_chef_app.core.HasId

@IgnoreExtraProperties
data class Utensil(
    val name: String = "", override var id: String = ""
) : HasId