package edu.javeriana.ratatouille_chef_app.profile.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.core.askPermission
import edu.javeriana.ratatouille_chef_app.profile.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var profileViewModel: ProfileViewModel? = null
    private val externalStorageRequestId = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setupUI()
    }

    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()
        setupButtons()
        profileViewModel?.findLoggedUserInformation()
    }

    private fun setupButtons() {
        profileImageView.setOnClickListener {
            requestExternalStoragePermissions()
        }
    }

    private val messagesObserver = Observer<String> { message ->
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val profileImageUriObserver = Observer<Uri> {
        Log.d("ProileActivity", it.toString())
        Picasso.get().load(it).into(profileImageView)
    }


    private fun fetchViewModels() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    private fun setUpLiveDataListeners() {
        profileViewModel?.userDataLiveData?.observe(this, loggerUserInfoObserver)
        profileViewModel?.messagesLiveData?.observe(this, messagesObserver)
        profileViewModel?.profileImageLiveData?.observe(this, profileImageUriObserver)
    }

    private val loggerUserInfoObserver = Observer<User> { user ->
        nameTextView.text = user.fullName
        biographyTextView.text = user.biography
    }

    private fun requestExternalStoragePermissions() {
        askPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            externalStorageRequestId
        ) { openPhotoGallery() }
    }

    private fun openPhotoGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, externalStorageRequestId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            externalStorageRequestId -> if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Acceso a imágenes concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Acceso a imágenes denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                externalStorageRequestId -> {
                    val selectedImage = data?.data
                    profileImageView.setImageURI(selectedImage)
                    profileImageView.isDrawingCacheEnabled = true
                    profileImageView.buildDrawingCache()
                    val bitmap = (profileImageView.drawable as BitmapDrawable).bitmap
                    profileViewModel?.changeProfileImage(bitmap)
                }
            }
        }
    }
}
