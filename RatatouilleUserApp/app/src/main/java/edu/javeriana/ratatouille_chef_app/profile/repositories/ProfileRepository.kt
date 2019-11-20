package edu.javeriana.ratatouille_chef_app.profile.repositories

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import java.io.ByteArrayOutputStream

interface ProfileRepository {
    fun findLoggedUserInformation(): Task<DocumentSnapshot>
    fun changeUsersProfileImage(imageBitMap: Bitmap): UploadTask
    fun findAllUtensil(): Task<QuerySnapshot>
    fun updateUserUtensils(utensils: List<String>): Task<Void>
    fun updateStateChef(state: Boolean): Task<Void>
    fun updateCurrentAddressChef(geoPoint: GeoPoint): Task<Void>
    fun findAllIngredients(): Task<QuerySnapshot>
    fun createRecipe(recipe: Recipe)
}

class FirebaseProfileRepository : ProfileRepository {



    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val usersCollection = "users"
    private val utensilsCollection = "utensils"
    private val recipeCollection = "recipe"
    private val ingredientsCollection = "ingredients"
    private val availableField = "available"
    private val currentAddressField = "currentAddress"

    override fun findLoggedUserInformation(): Task<DocumentSnapshot> {
        val loggedUserId = firebaseAuth.currentUser?.uid ?: ""
        return db.collection(usersCollection).document(loggedUserId).get()
    }

    override fun changeUsersProfileImage(imageBitMap: Bitmap): UploadTask {
        val loggedUserId = firebaseAuth.currentUser?.uid ?: ""
        val baos = ByteArrayOutputStream()
        imageBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imagePath = "profile/${firebaseAuth.currentUser?.uid}.jpeg"
        val profileImageRef = storage.reference.child(imagePath)
        profileImageRef.downloadUrl.addOnSuccessListener {
            db.collection(usersCollection).document(loggedUserId).update("photoUrl", it.toString())
        }
        return profileImageRef.putBytes(data)
    }


    override fun findAllUtensil(): Task<QuerySnapshot> {
        return db.collection(utensilsCollection).get()
    }

    override fun updateUserUtensils(utensils: List<String>): Task<Void> {
        val loggedUserId = firebaseAuth.currentUser?.uid ?: ""
        return db.collection(usersCollection).document(loggedUserId)
            .update(utensilsCollection, utensils)
    }

    override fun updateStateChef(state: Boolean): Task<Void> {
        val loggedUserId = firebaseAuth.currentUser?.uid ?: ""
        return db.collection(usersCollection).document(loggedUserId)
            .update(availableField, state)
    }

    override fun updateCurrentAddressChef(geoPoint: GeoPoint): Task<Void> {
        val loggedUserId = firebaseAuth.currentUser?.uid ?: ""
        return db.collection(usersCollection).document(loggedUserId)
            .update(currentAddressField, geoPoint)
    }

    override fun findAllIngredients(): Task<QuerySnapshot> {
        return db.collection(ingredientsCollection).get()
    }

    override fun createRecipe(recipe: Recipe) {
        db.collection(recipeCollection).add(recipe).addOnSuccessListener {recipeRef ->
            db.collection(usersCollection).document(firebaseAuth.currentUser?.uid ?: "").get().addOnSuccessListener {
                val currUser = it.toObject(User::class.java)
                val recepies = currUser?.recipes?.toMutableList()
                recepies?.add(recipeRef)
                Log.d("REPO", recepies.toString())
                Log.d("REPO", currUser.toString())
                db.collection(usersCollection).document(firebaseAuth.currentUser?.uid ?: "").update("recipes", recepies)
            }
        }
    }
}