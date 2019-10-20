package edu.javeriana.ratatouille_chef_app.authentication.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import edu.javeriana.ratatouille_chef_app.authentication.entities.User

interface AuthenticationRepository {
    fun createNewUser(user: User): Task<AuthResult>
    fun loginUserWithEmailAndPassWord(userCredentials: User): Task<AuthResult>
}

class FirebaseAuthenticationRepository : AuthenticationRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun createNewUser(user: User): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
    }

    override fun loginUserWithEmailAndPassWord(userCredentials: User): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(
            userCredentials.email,
            userCredentials.password
        )

    }
}