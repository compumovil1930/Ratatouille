package edu.javeriana.ratatouille_chef_app.client_requests.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.repositories.ClientRequestsRepository
import edu.javeriana.ratatouille_chef_app.client_requests.repositories.FireBaseClientRequestsRepository
import edu.javeriana.ratatouille_chef_app.core.distanceTo
import edu.javeriana.ratatouille_chef_app.core.toObjectsWithId

class ClientRequestsViewModel : ViewModel() {
    private val repository: ClientRequestsRepository = FireBaseClientRequestsRepository()
    val requestsSuccessfulLiveData = MutableLiveData<List<Transaction>>()
    val errorMessageLiveData = MutableLiveData<String>()
    val requestsSuccessfulLiveDataSingle = MutableLiveData<Transaction>()

    fun getAllRequests(locationAddress: LocationAddress) {
        repository.getAllRequestByPosition(locationAddress).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val tempRequests = it.toObjectsWithId<Transaction>()
            val requests = mutableListOf<Transaction>()
            for (request in tempRequests) {
                Log.d("DISTANCE", request.toString())
                Log.d("GO_TO_REQUEST", request.id)
                if (distanceTo(
                        request.address.latitude,
                        request.address.longitude,
                        locationAddress.latitude,
                        locationAddress.longitude
                    ) <= 5000
                ) {
                    requests += request
                }
            }
            requestsSuccessfulLiveData.value = requests
        }
    }

    fun getTransactionById(id: String) {
        repository.getTransactionById(id).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val transaction = it.toObject(Transaction::class.java)
            requestsSuccessfulLiveDataSingle.value = transaction
        }
    }
}