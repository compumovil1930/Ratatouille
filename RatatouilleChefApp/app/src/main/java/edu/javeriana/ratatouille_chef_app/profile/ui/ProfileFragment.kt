package edu.javeriana.ratatouille_chef_app.profile.ui


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.GeoPoint
import com.squareup.picasso.Picasso
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.core.askPermission
import edu.javeriana.ratatouille_chef_app.profile.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_profile.*

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var profileViewModel: ProfileViewModel? = null
    private val externalStorageRequestId = 10
    private val locationRequestCode = 11
    private var selectedUtensils = mutableListOf<String>()

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
        setUpLocation()
        profileViewModel?.findAllUtensils()
        profileViewModel?.findLoggedUserInformation()
    }

    private fun setUpLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        buildLocationRequest()
        buildLocationCallBack()
    }

    private fun setupButtons() {
        profileImageView.setOnClickListener {
            requestExternalStoragePermissions()
        }
        switchAvailable.setOnCheckedChangeListener { _, b -> changeSwitch(b) }
        profileImageView.setOnClickListener { requestExternalStoragePermissions() }
        goToRequests.setOnClickListener { goToClientRequestsActivity() }
    }

    private fun goToClientRequestsActivity() {
        view?.findNavController()?.navigate(R.id.action_profileFragment_to_clientRequestsFragment)
    }

    private fun setUpLiveDataListeners() {
        profileViewModel?.utensilsListLiveData?.observe(this, utensilListObserver)
        profileViewModel?.userDataLiveData?.observe(this, loggerUserInfoObserver)
        profileViewModel?.messagesLiveData?.observe(this, messagesObserver)
    }

    private val messagesObserver = Observer<String> { message ->
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private val loggerUserInfoObserver = Observer<User> { user ->
        nameTextView.text = user.fullName
        biographyTextView.text = user.biography
        selectedUtensils = user.utensils.toMutableList()
        Log.d("ProfileActivity", user.photoUrl ?: "")
        user.photoUrl?.let { Picasso.get().load(it).into(profileImageView) }
        if (user.isAvailable) {
            switchAvailable.isChecked = true
            subscribeToLocationWhioutPermission()
        }
    }

    private val utensilListObserver = Observer<List<Pair<String, Boolean>>> { utensils ->
        utensils.forEach {
            val utensilChip = Chip(utensilsChipGroup.context)
            utensilChip.text = it.first
            utensilChip.isCheckable = true
            utensilChip.isChecked = it.second
            utensilChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedUtensils.add(it.first) else selectedUtensils.remove(it.first)
                profileViewModel?.updateUserUtensils(selectedUtensils)
            }
            utensilsChipGroup.addView(utensilChip)
        }
    }

    private fun changeSwitch(isChecked: Boolean) {
        if (!isChecked) {
            profileViewModel?.updateUserAvailable(false)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        } else {
            profileViewModel?.updateUserAvailable(true)
            subscribeToLocationWhioutPermission()
        }
    }


    private fun fetchViewModels() {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
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
            locationRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    subscribeToLocation()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo acceder a la localización!",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
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

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                val location = p0?.lastLocation
                if (location != null) {
                    profileViewModel?.updateUserCurrentAddresss(
                        GeoPoint(
                            location.latitude,
                            location.longitude
                        )
                    )
                }
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f

    }

    private fun subscribeToLocationWhioutPermission() {
        askPermission(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationRequestCode
        )
        {
            subscribeToLocation()
        }
    }

    private fun subscribeToLocation() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


}
