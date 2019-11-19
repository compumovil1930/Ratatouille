package edu.javeriana.ratatouille_chef_app.client_requests.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.QuerySnapshot
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress

interface ClientRequestsRepository {
    fun getAllRequestByPosition(locationAddress: LocationAddress): Task<QuerySnapshot>
}

class FireBaseClientRequestsRepository : ClientRequestsRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val requestCollection = "transactions"
    private val TAG = "CLIENT_REQUESTS"

    override fun getAllRequestByPosition(locationAddress: LocationAddress): Task<QuerySnapshot> {


        val transactionsRef = db.collection(requestCollection)
        val chefId = firebaseAuth.currentUser?.uid ?: ""
        val refChef =  db.collection("users").document(chefId)
        val transactions = transactionsRef.whereEqualTo("chefId", refChef)


        return transactions.get().addOnFailureListener{ exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }


}