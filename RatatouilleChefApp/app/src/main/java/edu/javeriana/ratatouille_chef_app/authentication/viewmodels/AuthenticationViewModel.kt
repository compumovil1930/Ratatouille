package edu.javeriana.ratatouille_chef_app.authentication.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.repositories.AuthenticationRepository
import edu.javeriana.ratatouille_chef_app.authentication.repositories.FirebaseAuthenticationRepository


class AuthenticationViewModel : ViewModel() {
    // TODO - Add dependency injection.
    private val repository: AuthenticationRepository = FirebaseAuthenticationRepository()
    val isAuthenticationSuccessfulLiveData = MutableLiveData<Boolean>()
    val errorMessageLiveData = MutableLiveData<String>()

    fun createNewUser(user: User) {
        repository.createNewUser(user).addOnCompleteListener {
            isAuthenticationSuccessfulLiveData.value = it.isSuccessful
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }
    }


    fun loginUserWithEmailAndPassWord(userCredentials: User) {
        repository.loginUserWithEmailAndPassWord(userCredentials).addOnCompleteListener {
            isAuthenticationSuccessfulLiveData.value = it.isSuccessful
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }
    }

    fun checkIfUserIsAuthenticated() {
        if (repository.isUserAuthenticated()) isAuthenticationSuccessfulLiveData.value = true
    }

}