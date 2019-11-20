package edu.javeriana.ratatouille_chef_app.profile.ui


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.squareup.picasso.Picasso
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.authentication.viewmodels.AuthenticationViewModel
import edu.javeriana.ratatouille_chef_app.core.askPermission
import edu.javeriana.ratatouille_chef_app.profile.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private var profileViewModel: ProfileViewModel? = null
    private val externalStorageRequestId = 10
    private var authenticationViewModel: AuthenticationViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        fetchViewModels()
        setUpLiveDataListeners()
        setupButtons()
        profileViewModel?.findAllUtensils()
        profileViewModel?.findLoggedUserInformation()
    }

    private fun setupButtons() {
//        seeFormation.setOnClickListener { view?.findNavController()?.navigate(R.id.action_profileFragment_to_biographyFragment) }
        profileImageView.setOnClickListener {
            requestExternalStoragePermissions()
        }
        profileImageView.setOnClickListener { requestExternalStoragePermissions() }
        logout.setOnClickListener {
            authenticationViewModel?.logout()
            view?.findNavController()?.navigate(R.id.action_profileFragment_to_landingFragment)
        }
    }

    private fun setUpLiveDataListeners() {
        profileViewModel?.userDataLiveData?.observe(this, loggerUserInfoObserver)
        profileViewModel?.messagesLiveData?.observe(this, messagesObserver)
    }

    private val messagesObserver = Observer<String> { message ->
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private val loggerUserInfoObserver = Observer<User> { user ->
        nameTextView.text = user.fullName
        ratapoints.text = ("Ratapoints:  ${user.ratapoints}")
        user.photoUrl?.let { Picasso.get().load(it).into(profileImageView) }
    }


    private fun fetchViewModels() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        authenticationViewModel =
            ViewModelProviders.of(this).get(AuthenticationViewModel::class.java)
    }

    private fun requestExternalStoragePermissions() {
        askPermission(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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
                Toast.makeText(requireContext(), "Acceso a imágenes concedido", Toast.LENGTH_SHORT)
                    .show()
                openPhotoGallery()
            } else {
                Toast.makeText(requireContext(), "Acceso a imágenes denegado", Toast.LENGTH_SHORT)
                    .show()
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
