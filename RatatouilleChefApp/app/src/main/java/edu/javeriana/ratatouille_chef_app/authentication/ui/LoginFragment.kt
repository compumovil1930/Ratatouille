package edu.javeriana.ratatouille_chef_app.authentication.ui


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.viewmodels.AuthenticationViewModel
import edu.javeriana.ratatouille_chef_app.profile.ui.ProfileActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginFragment : Fragment() {

    private var authenticationViewModel: AuthenticationViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        fetchViewModels()
        authenticationViewModel?.checkIfUserIsAuthenticated()
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
            val goToProfileIntent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(goToProfileIntent)
        }
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
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
