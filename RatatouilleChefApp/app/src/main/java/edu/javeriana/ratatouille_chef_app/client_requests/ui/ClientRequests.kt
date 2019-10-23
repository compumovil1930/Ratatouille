package edu.javeriana.ratatouille_chef_app.client_requests.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Request
import edu.javeriana.ratatouille_chef_app.client_requests.ui.adapters.RequestAdapter
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import edu.javeriana.ratatouille_chef_app.core.askPermission
import kotlinx.android.synthetic.main.activity_client_requests.*

class ClientRequests : AppCompatActivity() {

    private val locationRequestCode = 1101
    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private var requestAdapter: RequestAdapter? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LocationAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_requests)
        setupUI()
        askPermission(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationRequestCode
        ) { getAllRequests() }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            locationRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllRequests()
                } else {
                    Toast.makeText(this, "No se pudo acceder a la localización!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()
        setUpLocation()
    }

    private fun setUpLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun fetchViewModels() {
        clientRequestsViewModel =
            ViewModelProviders.of(this).get(ClientRequestsViewModel::class.java)
    }

    private fun setUpLiveDataListeners() {
        clientRequestsViewModel?.requestsSuccessfulLiveData?.observe(
            this, requestsSuccessfulObserver

        )
        clientRequestsViewModel?.errorMessageLiveData?.observe(this, errorMessageObserver)
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private val requestsSuccessfulObserver =
        Observer<List<Request>> { requests: List<Request> ->

            for (request in requests) {
                Log.d("CLIENT_REQUEST", request.toString())
            }

            requestAdapter = RequestAdapter(this, requests, currentLocation!!)
            requestsListView.adapter = requestAdapter
        }

    private fun getAllRequests() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Toast.makeText(this, "No se pudo obtener la localización", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    currentLocation = LocationAddress(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    Log.d("CLIENT_REQUESTS", currentLocation.toString())
                    clientRequestsViewModel?.getAllRequests(
                        locationAddress = currentLocation!!
                    )
                }
            }

    }
}
