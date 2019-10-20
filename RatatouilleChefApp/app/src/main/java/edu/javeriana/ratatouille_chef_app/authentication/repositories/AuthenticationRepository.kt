package edu.javeriana.ratatouille_chef_app.authentication.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.javeriana.ratatouille_chef_app.authentication.entities.User

interface AuthenticationRepository {
    fun createNewUser(user: User): Task<AuthResult>
    fun loginUserWithEmailAndPassWord(userCredentials: User): Task<AuthResult>
}

class FirebaseAuthenticationRepository : AuthenticationRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = "users"

    override fun createNewUser(user: User): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                db.document("$usersCollection/${it.user?.uid}").set(user)
            }
    }

    override fun loginUserWithEmailAndPassWord(userCredentials: User): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(
            userCredentials.email,
            userCredentials.password
        )

    }
}