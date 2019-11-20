package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.StateTransaction
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.ui.adapters.RequestAdapter
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import edu.javeriana.ratatouille_chef_app.core.askPermission
import kotlinx.android.synthetic.main.fragment_client_requests.*

class ClientRequestsFragment : Fragment() {

    private val locationRequestCode = 1101
    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private var requestAdapter: RequestAdapter? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LocationAddress? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_client_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
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
                    Toast.makeText(
                        requireContext(),
                        "No se pudo acceder a la localización!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    }

    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()
        setUpLocation()
        askPermission(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            locationRequestCode
        ) { getAllRequests() }

        requestsListView.setOnItemClickListener { parent, view, position, _ ->
            goToRequestDetailFragment(parent, view, position)
        }

    }

    private fun setUpLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
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
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private val requestsSuccessfulObserver =
        Observer<List<User>> { transactions: List<User> ->

            for (request in transactions) {
                Log.d("CLIENT_REQUEST", request.toString())
            }

            requestAdapter = RequestAdapter(requireContext(), transactions, currentLocation!!)
            requestsListView.adapter = requestAdapter
        }

    private fun getAllRequests() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo obtener la localización",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    currentLocation = LocationAddress(
                        location = GeoPoint(location.latitude, location.longitude)
                    )
                    Log.d("CLIENT_REQUESTS", currentLocation.toString())
                    clientRequestsViewModel?.getAllRequests(
                        locationAddress = currentLocation!!
                    )
                }
            }

    }

    private fun goToRequestDetailFragment(parent: AdapterView<*>, view: View, position: Int) {
        val element: Transaction =
            parent.getItemAtPosition(position) as Transaction // The item that was clicked
        Log.d("GO_TO_REQUEST", element.id)
        val action: NavDirections?
        action = when {
            element.state == StateTransaction.ACCEPTED.value -> ClientRequestsFragmentDirections.actionClientRequestsFragmentToMapRequestFragment(
                element.id
            )
            element.state == StateTransaction.COMPLETE.value -> ClientRequestsFragmentDirections.actionClientRequestsFragmentToCompleteRequestFragment(
                element.id
            )
            else -> ClientRequestsFragmentDirections.actionClientRequestsFragmentToNewRequestDetail(
                element.id
            )
        }

        view.findNavController().navigate(action)

    }


}
