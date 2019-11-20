package edu.javeriana.ratatouille_chef_app.profile.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import edu.javeriana.ratatouille_chef_app.authentication.entities.Biography
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.profile.repositories.FirebaseProfileRepository
import edu.javeriana.ratatouille_chef_app.profile.repositories.ProfileRepository

class BiographyViewModel  : ViewModel() {
    private val repository: ProfileRepository = FirebaseProfileRepository()
    val biographyLiveData = MutableLiveData<Biography>()
    val recipesListLiveData = MutableLiveData<Recipe>()

    fun findUserBiography() {
        repository.findLoggedUserInformation().addOnCompleteListener {
            biographyLiveData.value = it.result?.toObject(User::class.java)?.biography
        }
    }

    fun findUserReference(id: String): Task<DocumentSnapshot> {
        return repository.findChefBiographyById(id)
    }

    fun findChefBiographyById(id: String) {
        repository.findChefBiographyById(id).addOnSuccessListener {
            val biography = it.toObject(User::class.java)?.biography
            biographyLiveData.value = biography
        }
    }
}