package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Ingredient
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Recipe
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import edu.javeriana.ratatouille_chef_app.profile.entities.Utensil
import kotlinx.android.synthetic.main.fragment_new_request_detail.*
import java.util.*
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class NewRequestDetailFragment : Fragment() {

    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private val args: NewRequestDetailFragmentArgs by navArgs()
    private var utensilsMarks: HashMap<String, Chip>? = null
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var transactionGlobal: Transaction? = null

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
        acceptButton.setOnClickListener { acceptTransaction() }
    }

    private fun acceptTransaction() {
        //clientRequestsViewModel?.updateStateTransaction(StateTransaction.ACCEPTED.value, args.transactionId)
        var totalCost = 0.0f
        //clientRequestsViewModel?.updateCostTransaction(totalCost, args.transactionId)
        clientRequestsViewModel?.createTransaction(transactionGlobal!!)

        view?.findNavController()?.navigate(R.id.action_newRequestDetail_to_clientRequestsFragment)
    }

    private fun fetchData() {
        Log.d("GO_TO_REQUEST", args.recepieId)
        val clientID = db.collection("users").document(firebaseAuth.currentUser!!.uid)
        val repipe = db.collection("recipe").document(args.recepieId)
        val geopoint = GeoPoint(args.address!!.latitude, args.address!!.longitude)
        val transaction: Transaction =
            Transaction(clientId = clientID, recipe = repipe, address = geopoint)
        transactionGlobal = transaction
        putDataInUI(transaction)
    }

    private fun fetchViewModels() {
        clientRequestsViewModel =
            ViewModelProviders.of(this).get(ClientRequestsViewModel::class.java)
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private val requestsSuccessfulObserver =
        Observer<Recipe> { recipe: Recipe ->
            val clientID = db.collection("users").document(firebaseAuth.currentUser!!.uid)
            val repipe = db.collection("recipe").document(args.recepieId)
            val geopoint = GeoPoint(args.address!!.latitude, args.address!!.longitude)
            val transaction: Transaction =
                Transaction(clientId = clientID, recipe = repipe, address = geopoint)
            transactionGlobal = transaction
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
