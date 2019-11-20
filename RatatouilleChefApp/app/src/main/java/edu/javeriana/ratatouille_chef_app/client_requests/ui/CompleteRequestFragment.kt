package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import androidx.lifecycle.Observer

import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Ingredient
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import kotlinx.android.synthetic.main.fragment_complete_request.*
import kotlinx.android.synthetic.main.fragment_new_request_detail.*
import kotlinx.android.synthetic.main.fragment_new_request_detail.addressTextView
import kotlinx.android.synthetic.main.fragment_new_request_detail.clientNameTextView
import kotlinx.android.synthetic.main.fragment_new_request_detail.recipeNameTextView
import kotlinx.android.synthetic.main.fragment_new_request_detail.totalTextView
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class CompleteRequestFragment : Fragment() {

    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private val args: NewRequestDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {

        fetchViewModels()
        setUpLiveDataListeners()
        fetchData()

    }

    private fun fetchData() {
        Log.d("GO_TO_REQUEST", args.transactionId)
        clientRequestsViewModel?.getTransactionById(args.transactionId)
    }

    private fun fetchViewModels() {
        clientRequestsViewModel =
            ViewModelProviders.of(this).get(ClientRequestsViewModel::class.java)
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private val requestsSuccessfulObserver =
        Observer<Transaction> { transaction: Transaction ->
            putDataInUI(transaction)
        }

    private fun putDataInUI(transaction: Transaction) {
        transaction.clientId?.get()?.addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            clientNameTextView.text = user?.fullName

        }
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(transaction.address.latitude, transaction.address.longitude, 3)
        addressTextView.text = "Not found"
        if( address.isNotEmpty() )
        {
            addressTextView.text = address[0].getAddressLine(0)
        }


        totalTextView.text = transaction.cost.toString()
        transaction.recipe?.get()?.addOnSuccessListener { document ->
            val recipe = document.toObject(Recipe::class.java)
            recipeNameTextView.text = recipe?.name
            recipeDescriptionTextView.text = recipe?.description


        }
        ratingTextView.text = transaction.rating.toString()
        commentTextView.text = transaction.comment

    }

    private fun setUpLiveDataListeners() {
        clientRequestsViewModel?.requestsSuccessfulLiveDataSingle?.observe(
            this, requestsSuccessfulObserver

        )
        clientRequestsViewModel?.errorMessageLiveData?.observe(this, errorMessageObserver)
    }
}
