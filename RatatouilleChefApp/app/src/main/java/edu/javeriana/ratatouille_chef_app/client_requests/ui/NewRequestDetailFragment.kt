package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.Manifest
import android.annotation.SuppressLint
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
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.DocumentReference

import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.ui.RegisterFragmentArgs
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Ingredient
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.client_requests.entities.StateTransaction
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.ui.adapters.RequestAdapter
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import edu.javeriana.ratatouille_chef_app.core.askPermission
import edu.javeriana.ratatouille_chef_app.profile.entities.Utensil
import kotlinx.android.synthetic.main.fragment_client_requests.*
import kotlinx.android.synthetic.main.fragment_new_request_detail.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class NewRequestDetailFragment : Fragment() {

    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private val args: NewRequestDetailFragmentArgs by navArgs()
    private var utensilsMarks: HashMap<String, Chip>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_request_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        utensilsMarks = HashMap<String, Chip> ()
        fetchViewModels()
        setUpLiveDataListeners()
        fetchData()
        configViews()
    }

    private fun configViews() {
        costServiceEditText.doAfterTextChanged { it ->
            var first = 0.0f
            var second = 0.0f
            if( it.toString().toFloatOrNull() != null )
            {
                first = it.toString().toFloat()
            }
            if( totalIngredientsTextView.text.toString().toFloatOrNull() != null )
            {
                second = totalIngredientsTextView.text.toString().toFloat()
            }

            val value = first +  second
            totalTextView.text = value.toString()
        }
        acceptButton.setOnClickListener { acceptTransaction() }
    }

    private fun acceptTransaction() {
        clientRequestsViewModel?.updateStateTransaction(StateTransaction.ACCEPTED.value, args.transactionId)
        var totalCost = 0.0f
        if( totalTextView.text.toString().toFloatOrNull() != null )
        {
            totalCost = totalTextView.text.toString().toFloat()
        }
        clientRequestsViewModel?.updateCostTransaction(totalCost, args.transactionId)
        val action = ClientRequestsFragmentDirections.actionClientRequestsFragmentToMapRequestFragment(args.transactionId)
        view?.findNavController()?.navigate(action)
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
            if (user != null) {
                addActionsUtensils(user.utensils)
            }
        }
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(transaction.address.latitude, transaction.address.longitude, 3)
        addressTextView.text = "Not found"
        if( address.isNotEmpty() )
        {
            addressTextView.text = address[0].getAddressLine(0)
        }

        var costIngredient = 0
        totalIngredientsTextView.text = costIngredient.toString()
        totalTextView.text = costIngredient.toString()
        transaction.recipe?.get()?.addOnSuccessListener { document ->
            val recipe = document.toObject(Recipe::class.java)
            recipeNameTextView.text = recipe?.name
            recipeDescriptionTextView.text = recipe?.description

            for ( ingredient in recipe!!.ingredients )
            {
                ingredient.get().addOnSuccessListener {documentI ->
                    val ing = documentI.toObject(Ingredient::class.java)
                    costIngredient += ing!!.cost
                    totalIngredientsTextView.text = costIngredient.toString()
                    totalTextView.text = costIngredient.toString()
                    val chips = Chip(ingredientsChipGroup.context)
                    chips.text = ing.name + ": " + ing.cost + "$"
                    chips.isCheckable = false
                    ingredientsChipGroup.addView(chips)
                }
            }

            addActionsUtensils(recipe.utensils)

        }


    }

    private fun addActionsUtensils(utensils: List<DocumentReference>){
        for ( utensil in utensils )
        {
            utensil.get().addOnSuccessListener {documentI ->
                val ing = documentI.toObject(Utensil::class.java)
                val nameU = ing!!.name
                Log.d("ACTION_UTENSILS", nameU)
                if (!utensilsMarks!!.containsKey(nameU)) {
                    val chips = Chip(utensilsRecipeChipGroup.context)
                    chips.text = nameU
                    chips.isCheckable = true
                    utensilsRecipeChipGroup.addView(chips)
                    utensilsMarks!![nameU] = chips
                }
                else
                {
                    Log.d("ACTION_UTENSILS", "Change ......")
                    utensilsMarks!![nameU]?.isChecked= true
                }

            }
        }
    }

    private fun setUpLiveDataListeners() {
        clientRequestsViewModel?.requestsSuccessfulLiveDataSingle?.observe(
            this, requestsSuccessfulObserver

        )
        clientRequestsViewModel?.errorMessageLiveData?.observe(this, errorMessageObserver)
    }

}
