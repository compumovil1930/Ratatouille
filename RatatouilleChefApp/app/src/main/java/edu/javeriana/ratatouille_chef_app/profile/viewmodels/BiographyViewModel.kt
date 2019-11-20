package edu.javeriana.ratatouille_chef_app.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.javeriana.ratatouille_chef_app.authentication.entities.Biography
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.profile.repositories.FirebaseProfileRepository
import edu.javeriana.ratatouille_chef_app.profile.repositories.ProfileRepository

class BiographyViewModel  : ViewModel() {
    private val repository: ProfileRepository = FirebaseProfileRepository()
    val biographyLiveData = MutableLiveData<Biography>()

    fun findUserBiography() {
        repository.findLoggedUserInformation().addOnCompleteListener {
            biographyLiveData.value = it.result?.toObject(User::class.java)?.biography
        }
    }
}