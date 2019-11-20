package edu.javeriana.ratatouille_chef_app.authentication.ui


import android.location.Address
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
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.Biography
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.viewmodels.AuthenticationViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    private var authenticationViewModel: AuthenticationViewModel? = null
    private lateinit var geocoder: Geocoder
    private val args: RegisterFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        Log.d("RegisterFragment", args.address.toString())
        setLocationAddressField()
    }

    private fun setupUI() {
        geocoder = Geocoder(requireContext())
        fetchViewModels()
        setUpLiveDataListeners()
        setupButtons()
        setTextListeners()

    }

    private fun isValidForm(): Boolean {
        if (nameEditText.text.isNullOrEmpty()) return false
        if (biographyEditText.text.isNullOrEmpty()) return false
        if (specialitiesEditText.text.isNullOrEmpty()) return false
        if (certificationsEditText.text.isNullOrEmpty()) return false
        if (addressEditText.text.isNullOrEmpty()) return false
        if (ageEditText.text.isNullOrEmpty()) return false
        if (yearsOfExpreianceEditText.text.isNullOrEmpty()) return false
        if (emailEditText.text.isNullOrEmpty()) return false
        if (passwordEditText.text.isNullOrEmpty()) return false
        return true
    }

    private fun setLocationAddressField() {
        val address: Address? = args.address
        address?.let {
            addressEditText.setText(it.getAddressLine(0))
        }
    }

    private fun setTextListeners() {
        addressEditText.setOnFocusChangeListener { _, isFocused ->
            if (isFocused) {
                view?.findNavController()?.navigate(R.id.action_registerFragment_to_mapFragment)
            }
        }
    }

    private fun fetchViewModels() {
        authenticationViewModel =
            ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    private fun setUpLiveDataListeners() {
        authenticationViewModel?.isAuthenticationSuccessfulLiveData?.observe(
            this,
            isAuthenticatedObserver
        )
        authenticationViewModel?.errorMessageLiveData?.observe(this, errorMessageObserver)
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private val isAuthenticatedObserver = Observer<Boolean> { isAuthenticationSuccessful: Boolean ->
        if (isAuthenticationSuccessful) {
            Toast.makeText(requireContext(), "User Created!", Toast.LENGTH_LONG).show()
            view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupButtons() {
        loginButton.setOnClickListener {
            if (isValidForm()) {
                val userCredentials = getTypedCredentials()
                authenticationViewModel?.createNewUser(userCredentials)
            } else {
                authenticationViewModel?.errorMessageLiveData?.value =
                    "Todos los campos son requeridos."
            }

        }
    }

    private fun getBiography(): Biography {
        return Biography(
            formation = biographyEditText.text.toString(),
            yearsOfExperience = yearsOfExpreianceEditText.text.toString().toIntOrNull() ?: 0,
            certificates = certificationsEditText.text.toString().split(","),
            specialities = specialitiesEditText.text.toString().split(",")
        )

    }

    private fun getCoordinatesFromAddress(): LocationAddress {
        val address = args.address
        return LocationAddress(
            address = address?.getAddressLine(0) ?: "No address",
            location = GeoPoint(address?.latitude ?: 0.0, address?.longitude ?: 0.0)
        )
    }


    private fun getTypedCredentials(): User {
        return User(
            email = emailEditText.text.toString(),
            password = passwordEditText.text.toString(),
            fullName = nameEditText.text.toString(),
            address = getCoordinatesFromAddress(),
            age = ageEditText.text.toString().toIntOrNull() ?: 0,
            biography = getBiography()
        )
    }


}
