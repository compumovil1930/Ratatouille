package edu.javeriana.ratatouille_chef_app.profile.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

interface ProfileRepository {
    fun findLoggedUserInformation(): Task<DocumentSnapshot>
    fun findProfileImageUrl(): Uri?
    fun changeUsersProfileImage(imageBitMap: Bitmap): UploadTask
}

class FirebaseProfileRepository : ProfileRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val usersCollection = "users"

    override fun findLoggedUserInformation(): Task<DocumentSnapshot> {
        val loggedUserId = firebaseAuth.currentUser?.uid ?: ""
        return db.collection(usersCollection).document(loggedUserId).get()
    }

    override fun changeUsersProfileImage(imageBitMap: Bitmap): UploadTask {
        val baos = ByteArrayOutputStream()
        imageBitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val imagePath = "profile/${firebaseAuth.currentUser?.uid}.jpeg"
        val profileImageRef = storage.reference.child(imagePath)
        profileImageRef.downloadUrl.addOnSuccessListener {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(it)
                .build()
            firebaseAuth.currentUser?.updateProfile(profileUpdates)
        }
        return profileImageRef.putBytes(data)
    }

    override fun findProfileImageUrl(): Uri? {
        return firebaseAuth.currentUser?.photoUrl
    }
}