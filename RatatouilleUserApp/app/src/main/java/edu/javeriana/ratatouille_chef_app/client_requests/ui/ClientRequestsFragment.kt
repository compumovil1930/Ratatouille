package edu.javeriana.ratatouille_chef_app.client_requests.ui


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
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.client_requests.entities.StateTransaction
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.ui.adapters.RequestAdapterRequest
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import kotlinx.android.synthetic.main.fragment_client_requests.*

class ClientRequestsFragment : Fragment() {

    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private var requestAdapter: RequestAdapterRequest? = null

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


    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()

        requestsListView.setOnItemClickListener { parent, view, position, _ ->
            goToRequestDetailFragment(parent, view, position)
        }

        getAllRequests()

    }


    private fun fetchViewModels() {
        clientRequestsViewModel =
            ViewModelProviders.of(this).get(ClientRequestsViewModel::class.java)
    }

    private fun setUpLiveDataListeners() {
        clientRequestsViewModel?.requestsSuccessfulLiveDataTransaction?.observe(
            this, requestsSuccessfulObserver

        )
        clientRequestsViewModel?.errorMessageLiveData?.observe(this, errorMessageObserver)
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private val requestsSuccessfulObserver =
        Observer<List<Transaction>> { transactions: List<Transaction> ->

            for (request in transactions) {
                Log.d("CLIENT_REQUEST", request.toString())
            }

            requestAdapter = RequestAdapterRequest(requireContext(), transactions)
            requestsListView.adapter = requestAdapter
        }

    private fun getAllRequests() {


        clientRequestsViewModel?.getAllRequestsClient()

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
            else -> null
        }
        if (action != null) {
            view.findNavController().navigate(action)
        }


    }


}
