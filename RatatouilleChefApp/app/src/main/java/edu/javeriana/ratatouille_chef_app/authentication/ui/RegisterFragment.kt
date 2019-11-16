package edu.javeriana.ratatouille_chef_app.authentication.ui


import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.viewmodels.AuthenticationViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterFragment : Fragment() {

    private var authenticationViewModel: AuthenticationViewModel? = null
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        geocoder = Geocoder(requireContext())
        fetchViewModels()
        setUpLiveDataListeners()
        setupButtons()
        setTextListeners()

    }

    private fun setTextListeners() {
        addressEditText.setOnFocusChangeListener { _, isFocused ->
            if (!isFocused) {
                val address =
                    geocoder.getFromLocationName(addressEditText.text.toString(), 5).firstOrNull()
                if (address == null) {
                    loginButton.isEnabled = false
                    addressEditText.error = "Dirección no encontrada."
                } else {
                    loginButton.isEnabled = true
                    addressEditText.error = null
                }
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
            val userCredentials = getTypedCredentials()
            authenticationViewModel?.createNewUser(userCredentials)

        }
    }

    private fun getCoordinatesFromAddress(): LocationAddress {
        val address = geocoder.getFromLocationName(addressEditText.text.toString(), 5).first()
        return LocationAddress(
            address = address.getAddressLine(0),
            latitude = address.latitude,
            longitude = address.longitude
        )
    }


    private fun getTypedCredentials(): User {
        return User(
            email = emailEditText.text.toString(),
            password = passwordEditText.text.toString(),
            fullName = nameEditText.text.toString(),
            address = getCoordinatesFromAddress(),
            age = ageEditText.text.toString().toIntOrNull() ?: 0,
            yearsOfExperience = yearsOfExpreianceEditText.text.toString().toIntOrNull() ?: 0,
            biography = biographyEditText.text.toString()
        )
    }


}
