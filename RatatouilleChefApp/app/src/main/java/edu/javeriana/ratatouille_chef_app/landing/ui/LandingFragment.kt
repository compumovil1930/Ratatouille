package edu.javeriana.ratatouille_chef_app.landing.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import edu.javeriana.ratatouille_chef_app.R
import kotlinx.android.synthetic.main.fragment_landing.*

class LandingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_landing, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtonsToNavigate()
    }


    private fun setupButtonsToNavigate() {
        loginButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_landingFragment_to_loginFragment)
        }
        registerButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_landingFragment_to_registerFragment)
        }
    }
}
