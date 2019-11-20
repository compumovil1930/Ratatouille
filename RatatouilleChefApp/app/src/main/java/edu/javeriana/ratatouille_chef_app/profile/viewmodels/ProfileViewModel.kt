package edu.javeriana.ratatouille_chef_app.profile.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.profile.entities.Utensil
import edu.javeriana.ratatouille_chef_app.profile.repositories.FirebaseProfileRepository
import edu.javeriana.ratatouille_chef_app.profile.repositories.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val repository: ProfileRepository = FirebaseProfileRepository()
    val userDataLiveData = MutableLiveData<User>()
    val messagesLiveData = MutableLiveData<String>()
    val utensilsListLiveData = MutableLiveData<List<Pair<String, Boolean>>>()

    fun findLoggedUserInformation() {
        repository.findLoggedUserInformation().addOnCompleteListener {
            userDataLiveData.value = it.result?.toObject(User::class.java)
        }

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


    fun findAllUtensils() {
        repository.findAllUtensil().addOnSuccessListener {
            val utensils = it.toObjects(Utensil::class.java)
            repository.findLoggedUserInformation().addOnCompleteListener {
                val userInfo = it.result?.toObject(User::class.java)
                val markedUtensils = utensils.map { utensil ->
                    Pair(
                        utensil.name,
                        userInfo?.utensils?.contains(utensil.name) ?: false
                    )
                }
                utensilsListLiveData.value = markedUtensils
            }
        }
    }

    fun updateUserUtensils(utensils: List<String>) {
        repository.updateUserUtensils(utensils).addOnSuccessListener {
            messagesLiveData.value = "Utensilios modificados exitosamente."
        }
    }

    fun updateUserAvailable(state: Boolean) {
        repository.updateStateChef(state).addOnSuccessListener {
            messagesLiveData.value = "Estado modificado exitosamente."
        }
    }

    fun updateUserCurrentAddress(geoPoint: GeoPoint) {
        repository.updateCurrentAddressChef(geoPoint).addOnSuccessListener {

        }
    }


}