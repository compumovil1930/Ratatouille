package edu.javeriana.ratatouille_chef_app.authentication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.viewmodels.AuthenticationViewModel
import edu.javeriana.ratatouille_chef_app.client_requests.ui.ClientRequests
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var authenticationViewModel: AuthenticationViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupUI()
    }

    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()
        setupButtons()
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

    private val isAuthenticatedObserver = Observer<Boolean> { isAuthenticationSuccessful: Boolean ->
        if (isAuthenticationSuccessful) {
            Toast.makeText(this, "Welcome Chef!", Toast.LENGTH_LONG).show()
            // Johan's code erase
            var intent: Intent = Intent(this, ClientRequests::class.java)
            startActivity(intent)
            ////////////
        }
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }


    private fun setupButtons() {
        loginButton.setOnClickListener {
            val userCredentials = getTypedCredentials()
            authenticationViewModel?.loginUserWithEmailAndPassWord(userCredentials)

        }
    }

    private fun getTypedCredentials(): User {
        return User(
            email = emailEditText.text.toString(),
            password = passwordEditText.text.toString()
        )
    }

}



