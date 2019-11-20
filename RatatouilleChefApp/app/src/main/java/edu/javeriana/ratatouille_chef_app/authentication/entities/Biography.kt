package edu.javeriana.ratatouille_chef_app.authentication.entities

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Biography(
    val formation: String = "",
    val yearsOfExperience: Int = 0,
    val certificates: List<String> = emptyList(),
    val specialities: List<String> = emptyList()
)