package edu.javeriana.ratatouille_chef_app.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupButtonsToNavigate()
    }

    private fun setupButtonsToNavigate() {
        val goToLogin = Intent(this, LoginActivity::class.java)
        loginButton.setOnClickListener { startActivity(goToLogin) }
    }
}
