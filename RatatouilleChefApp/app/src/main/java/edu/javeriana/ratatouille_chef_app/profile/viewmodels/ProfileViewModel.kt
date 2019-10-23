package edu.javeriana.ratatouille_chef_app.profile.viewmodels

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.profile.repositories.FirebaseProfileRepository
import edu.javeriana.ratatouille_chef_app.profile.repositories.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val repository: ProfileRepository = FirebaseProfileRepository()
    val userDataLiveData = MutableLiveData<User>()
    val messagesLiveData = MutableLiveData<String>()
    val profileImageLiveData = MutableLiveData<Uri>()

    fun findLoggedUserInformation() {
        repository.findLoggedUserInformation().addOnCompleteListener {
            userDataLiveData.value = it.result?.toObject(User::class.java)
        }
        findProfileImageUrl()
    }

    fun changeProfileImage(imageBitMap: Bitmap) {
        repository.changeUsersProfileImage(imageBitMap).addOnCompleteListener {
            if (it.isSuccessful) {
                messagesLiveData.value = "Imagen actualizada exitosamente."
            } else {
                messagesLiveData.value = it.exception?.message
            }
        }
    }

    private fun findProfileImageUrl() {
        val profileImageUri = repository.findProfileImageUrl()
        Log.d("ProfileViewModel", profileImageUri.toString())
        profileImageUri.let { profileImageLiveData.value = it }
    }


}