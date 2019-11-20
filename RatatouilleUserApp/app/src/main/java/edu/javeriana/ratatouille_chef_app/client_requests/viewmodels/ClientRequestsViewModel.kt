package edu.javeriana.ratatouille_chef_app.client_requests.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.repositories.ClientRequestsRepository
import edu.javeriana.ratatouille_chef_app.client_requests.repositories.FireBaseClientRequestsRepository
import edu.javeriana.ratatouille_chef_app.core.distanceTo
import edu.javeriana.ratatouille_chef_app.core.toObjectsWithId

class ClientRequestsViewModel : ViewModel() {
    private val repository: ClientRequestsRepository = FireBaseClientRequestsRepository()
    val requestsSuccessfulLiveData = MutableLiveData<List<User>>()
    val requestsSuccessfulLiveDataTransaction = MutableLiveData<List<Transaction>>()
    val errorMessageLiveData = MutableLiveData<String>()
    val requestsSuccessfulLiveDataSingle = MutableLiveData<Recipe>()
    val requestsSuccessfulLiveDataSingleTransaction = MutableLiveData<Transaction>()

    fun getAllRequests(locationAddress: LocationAddress) {
        repository.getAllRequestByPosition(locationAddress).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val tempRequests = it.toObjectsWithId<User>()
            val requests = mutableListOf<User>()
            for (request in tempRequests) {
                Log.d("DISTANCE", request.toString())
                Log.d("GO_TO_REQUEST", request.id)
                Log.d("DISTANCE", locationAddress.toString())
                Log.d("DISTANCE", request.currentAddress.toString())
                Log.d("DISTANCE", distanceTo(
                    request.currentAddress.latitude,
                    request.currentAddress.longitude,
                    locationAddress.location?.latitude ?: 0.0,
                    locationAddress.location?.longitude ?: 0.0
                ).toString())

                if (request.available && distanceTo(
                        request.currentAddress.latitude,
                        request.currentAddress.longitude,
                        locationAddress.location?.latitude ?: 0.0,
                        locationAddress.location?.longitude ?: 0.0
                    ) <= 5000
                ) {
                    Log.d("PASO", request.id)
                    requests += request
                }
            }
            requestsSuccessfulLiveData.value = requests
        }
    }

    fun getRecepiById(id: String) {
        repository.getRecepiById(id).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val transaction = it.toObject(Recipe::class.java)
            requestsSuccessfulLiveDataSingle.value = transaction
        }
    }

    fun updateStateTransaction(state: String, id: String) {
        repository.updateStateTransaction(state, id).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }
    }

    fun updateCostTransaction(cost: Float, id: String) {
        repository.updateCostTransaction(cost, id).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }
    }

    fun createTransaction(transaction: Transaction) {
        repository.createTransaction(transaction).addOnSuccessListener {

        }
    }

    fun getAllRequestsClient() {
        repository.getAllRequestByClient().addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val tempRequests = it.toObjectsWithId<Transaction>()
            requestsSuccessfulLiveDataTransaction.value = tempRequests
        }
    }

    fun getTransactionById(transactionId: String) {
        repository.getTransactionById(transactionId).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val transaction = it.toObject(Transaction::class.java)
            requestsSuccessfulLiveDataSingleTransaction.value = transaction
        }
    }


}