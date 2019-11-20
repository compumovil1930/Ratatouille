package edu.javeriana.ratatouille_chef_app.client_requests.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User

interface ClientRequestsRepository {
    fun getAllRequestByPosition(locationAddress: LocationAddress): Task<QuerySnapshot>
    fun getTransactionById(id: String): Task<DocumentSnapshot>
    fun updateStateTransaction(state: String, id: String): Task<Void>
    fun updateCostTransaction(cost: Float, id: String): Task<Void>
    fun updateRatapointUser(cost: Float)
}

class FireBaseClientRequestsRepository : ClientRequestsRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val requestCollection = "transactions"
    private val TAG = "CLIENT_REQUESTS"
    private val stateField = "state"
    private val costField = "cost"

    override fun getAllRequestByPosition(locationAddress: LocationAddress): Task<QuerySnapshot> {


        val transactionsRef = db.collection("users")
        val transactions = transactionsRef.whereEqualTo("isChef", true)


        return transactions.get().addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    override fun getTransactionById(id: String): Task<DocumentSnapshot> {
        val transactionsRef = db.collection(requestCollection)

        // val refChef =  db.collection("users").document(chefId)
        // val transactions = transactionsRef.whereEqualTo("chefId", id)
        Log.d(TAG, id)
        val transactions = transactionsRef.document(id)


        return transactions.get().addOnFailureListener{ exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    override fun updateStateTransaction(state: String, id: String): Task<Void> {

        return db.collection(requestCollection).document(id)
            .update(stateField, state)
    }

    override fun updateCostTransaction(cost: Float, id: String): Task<Void> {

        return db.collection(requestCollection).document(id)
            .update(costField, cost)
    }

    override fun updateRatapointUser(cost: Float) {
        val usersCollection = "users"
        db.collection(usersCollection).document(firebaseAuth.currentUser?.uid ?: "").get()
            .addOnSuccessListener {
                val currUser = it.toObject(User::class.java)
                db.collection(usersCollection).document(firebaseAuth.currentUser?.uid ?: "")
                    .update("ratapoints", cost + currUser!!.ratapoints)
            }
    }


}