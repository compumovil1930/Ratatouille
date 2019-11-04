package edu.javeriana.ratatouille_chef_app.authentication.ui

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.LocationAddress
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.viewmodels.AuthenticationViewModel
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private var authenticationViewModel: AuthenticationViewModel? = null
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupUI()
    }


    private fun setupUI() {
        geocoder = Geocoder(this)
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
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private val isAuthenticatedObserver = Observer<Boolean> { isAuthenticationSuccessful: Boolean ->
        if (isAuthenticationSuccessful) {
            Toast.makeText(this, "User Created!", Toast.LENGTH_LONG).show()
            val goToLoginIntent = Intent(this, LoginActivity::class.java)
            startActivity(goToLoginIntent)
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
