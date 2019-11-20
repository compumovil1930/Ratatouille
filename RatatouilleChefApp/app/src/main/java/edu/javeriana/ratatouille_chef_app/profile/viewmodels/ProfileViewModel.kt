package edu.javeriana.ratatouille_chef_app.profile.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Ingredient
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.core.toObjectsWithId
import edu.javeriana.ratatouille_chef_app.profile.entities.Utensil
import edu.javeriana.ratatouille_chef_app.profile.repositories.FirebaseProfileRepository
import edu.javeriana.ratatouille_chef_app.profile.repositories.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val repository: ProfileRepository = FirebaseProfileRepository()
    val userDataLiveData = MutableLiveData<User>()
    val messagesLiveData = MutableLiveData<String>()

    val utensilsListLiveData = MutableLiveData<List<Utensil>>()
    val ingredientsListLiveData = MutableLiveData<List<Ingredient>>()

    val selectedIngredientsLiveData = MutableLiveData<MutableList<DocumentReference>>(mutableListOf())
    val selectedUtensilsLiveData = MutableLiveData<MutableList<DocumentReference>>(mutableListOf())



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
            utensilsListLiveData.value = it.toObjectsWithId()
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

    fun findAllIngredients() {
        repository.findAllIngredients().addOnSuccessListener {
            ingredientsListLiveData.value = it.toObjectsWithId()
        }
    }

    fun createRecipe(recipe: Recipe) {
        return repository.createRecipe(recipe)
    }


}