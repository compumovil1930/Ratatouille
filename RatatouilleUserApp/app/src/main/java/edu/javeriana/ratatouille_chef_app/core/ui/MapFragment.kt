package edu.javeriana.ratatouille_chef_app.core.ui


import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.core.askPermission
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val locationRequestCode = 110
    private var currentLocation: LatLng? = null
    private val args: MapFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }


    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        googleMap?.setOnMarkerDragListener(this)
        googleMap?.setMinZoomPreference(15f)
        setUpLocation()
        subscribeToLocationWithPermission()
    }

    private fun setUpLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        buildLocationRequest()
        buildLocationCallBack()
    }

    private fun setupUI() {
        confirmLocationButton.setOnClickListener {
            val location = Geocoder(requireContext()).getFromLocation(
                currentLocation?.latitude ?: 0.0,
                currentLocation?.longitude ?: 0.0,
                1
            )
            if(args.isFromChefs) {
                val action = MapFragmentDirections.actionMapFragmentToChefsListFragment(location.first())
                view?.findNavController()?.navigate(action)

            } else {
            val action = MapFragmentDirections.actionMapFragmentToRegisterFragment(location.first())
                view?.findNavController()?.navigate(action)

            }

        }
    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                val location = result?.lastLocation
                Log.d("MapFragment", location.toString())
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    googleMap?.addMarker(MarkerOptions().position(currentLocation!!).draggable(true))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                    setupUI()
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

    private fun subscribeToLocationWithPermission() {
        askPermission(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            locationRequestCode
        ) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
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

    override fun onMarkerDragEnd(marker: Marker?) {
        currentLocation = marker?.position
    }

    override fun onMarkerDragStart(marker: Marker?) {
        currentLocation = marker?.position
    }

    override fun onMarkerDrag(marker: Marker?) {
        currentLocation = marker?.position
    }
}

