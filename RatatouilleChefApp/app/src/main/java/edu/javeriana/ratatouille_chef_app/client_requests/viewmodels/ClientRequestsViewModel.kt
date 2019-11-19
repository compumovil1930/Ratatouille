package edu.javeriana.ratatouille_chef_app.client_requests.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Request
import edu.javeriana.ratatouille_chef_app.client_requests.repositories.ClientRequestsRepository
import edu.javeriana.ratatouille_chef_app.client_requests.repositories.FireBaseClientRequestsRepository
import edu.javeriana.ratatouille_chef_app.core.distanceTo

class ClientRequestsViewModel : ViewModel() {
    private val repository: ClientRequestsRepository = FireBaseClientRequestsRepository()
    val requestsSuccessfulLiveData = MutableLiveData<List<Request>>()
    val errorMessageLiveData = MutableLiveData<String>()

    fun getAllRequests(locationAddress: LocationAddress) {
        repository.getAllRequestByPosition(locationAddress).addOnCompleteListener {
            if (!it.isSuccessful) errorMessageLiveData.value = it.exception?.message
        }.addOnSuccessListener {
            val tempRequests = it.toObjects(Request::class.java)
            val requests = mutableListOf<Request>()
            for (request in tempRequests) {
                // Log.d("DISTANCE", request.toString())
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
}