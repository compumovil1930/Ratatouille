package edu.javeriana.ratatouille_chef_app.client_requests.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction

interface ClientRequestsRepository {
    fun getAllRequestByPosition(locationAddress: LocationAddress): Task<QuerySnapshot>
    fun getRecepiById(id: String): Task<DocumentSnapshot>
    fun updateStateTransaction(state: String, id: String): Task<Void>
    fun updateCostTransaction(cost: Float, id: String): Task<Void>
    fun updateRatapointUser(cost: Float)
    fun createTransaction(transaction: Transaction): Task<DocumentReference>
    fun getAllRequestByClient(): Task<QuerySnapshot>
    fun getTransactionById(transactionId: String): Task<DocumentSnapshot>
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
        val transactions = transactionsRef.whereEqualTo("chef", true)


        return transactions.get().addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    override fun getRecepiById(id: String): Task<DocumentSnapshot> {
        val transactionsRef = db.collection("recipe")

        // val refChef =  db.collection("users").document(chefId)
        // val transactions = transactionsRef.whereEqualTo("chefId", id)
        Log.d(TAG, id)
        val transactions = transactionsRef.document(id)


        return transactions.get().addOnFailureListener{ exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }

    override fun getTransactionById(transactionId: String): Task<DocumentSnapshot> {
        val transactionsRef = db.collection(requestCollection)

        // val refChef =  db.collection("users").document(chefId)
        // val transactions = transactionsRef.whereEqualTo("chefId", id)
        Log.d(TAG, transactionId)
        val transactions = transactionsRef.document(transactionId)


        return transactions.get().addOnFailureListener { exception ->
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

    override fun createTransaction(transaction: Transaction): Task<DocumentReference> {
        return db.collection(requestCollection).add(transaction)
    }

    override fun getAllRequestByClient(): Task<QuerySnapshot> {
        val transactionsRef = db.collection(requestCollection)

        val refChef = db.collection("users").document(firebaseAuth.uid!!)
        // val transactions = transactionsRef.whereEqualTo("chefId", id)
        val transactions = transactionsRef.whereEqualTo("clientId", refChef)


        return transactions.get().addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }
    }
}