package edu.javeriana.ratatouille_chef_app.landing.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.ui.LoginActivity
import edu.javeriana.ratatouille_chef_app.authentication.ui.RegisterActivity
import kotlinx.android.synthetic.main.activity_landing.*

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        setupButtonsToNavigate()
    }

    private fun setupButtonsToNavigate() {
        val goToLogin = Intent(this, LoginActivity::class.java)
        loginButton.setOnClickListener { startActivity(goToLogin) }
        val goToRegister = Intent(this, RegisterActivity::class.java)
        registerButton.setOnClickListener { startActivity(goToRegister) }
    }
}
