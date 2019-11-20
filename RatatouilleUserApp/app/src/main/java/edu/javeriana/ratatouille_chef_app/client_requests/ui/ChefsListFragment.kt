package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.location.Address
import android.location.Geocoder
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.ui.RegisterFragmentArgs
import edu.javeriana.ratatouille_chef_app.client_requests.ui.adapters.RequestAdapter
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import kotlinx.android.synthetic.main.fragment_chefs_list.*

/**
 * A simple [Fragment] subclass.
 */
class ChefsListFragment : Fragment() {

    private lateinit var geocoder: Geocoder
    private val args: RegisterFragmentArgs by navArgs()
    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private var requestAdapter: RequestAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chefs_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        Log.d("RegisterFragment", args.address.toString())
        setLocationAddressField()
    }

    private fun setupUI() {
        geocoder = Geocoder(requireContext())
        setTextListeners()

        fetchViewModels()
        setUpLiveDataListeners()
        requestsListView.setOnItemClickListener { parent, view, position, _ ->
            goToRequestDetailFragment(parent, view, position)
        }
        Log.d("HELLO", "Antes SetupUI")
        if (args.address != null) {
            Log.d("HELLO", "SetupUI")
            getAllRequests()
        }

    }

    private fun getCoordinatesFromAddress(): LocationAddress {
        val address = args.address
        return LocationAddress(
            address = address?.getAddressLine(0) ?: "No address",
            location = GeoPoint(address?.latitude ?: 0.0, address?.longitude ?: 0.0)
        )
    }

    private fun setTextListeners() {
        addressEditText.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                val action = ChefsListFragmentDirections.actionChefsListFragmentToMapFragment(true)
                view?.findNavController()?.navigate(action)
            }
        }
    }

    private fun setLocationAddressField() {
        val address: Address? = args.address
        address?.let {
            addressEditText.setText(it.getAddressLine(0))
        }
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

            requestAdapter =
                RequestAdapter(requireContext(), transactions, getCoordinatesFromAddress())
            requestsListView.adapter = requestAdapter
        }

    private fun getAllRequests() {

        clientRequestsViewModel?.getAllRequests(
            locationAddress = getCoordinatesFromAddress()
        )


    }

    private fun goToRequestDetailFragment(parent: AdapterView<*>, view: View, position: Int) {
        val element: User =
            parent.getItemAtPosition(position) as User // The item that was clicked
        Log.d("GO_TO_REQUEST", element.id)
        val action = ChefsListFragmentDirections.actionChefsListFragmentToBiographyFragment(
            element.id,
            args.address
        )
        view.findNavController().navigate(action)

    }


}
