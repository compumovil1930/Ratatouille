package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.beust.klaxon.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import edu.javeriana.ratatouille_chef_app.R
import edu.javeriana.ratatouille_chef_app.authentication.entities.User
import edu.javeriana.ratatouille_chef_app.client_requests.entities.StateTransaction
import edu.javeriana.ratatouille_chef_app.client_requests.entities.Transaction
import edu.javeriana.ratatouille_chef_app.client_requests.viewmodels.ClientRequestsViewModel
import kotlinx.android.synthetic.main.fragment_map_request.*
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.net.URL

/**
 * A simple [Fragment] subclass.
 */
class MapRequestFragment : Fragment(), OnMapReadyCallback {


    private var googleMap: GoogleMap? = null

    private val locationRequestCode = 110
    private var currentLocation: LatLng? = null
    //private var currentLocation: Location? = null
    private var positionToGo: GeoPoint? = null
    private var clientRequestsViewModel: ClientRequestsViewModel? = null
    private val args: MapRequestFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_request, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapViewRoute.onCreate(savedInstanceState)
        mapViewRoute.onResume()
        mapViewRoute.getMapAsync(this)

        fetchViewModels()
        setUpLiveDataListeners()
        fetchData()
    }

    private fun goToCompleteRequestFragment() {
        clientRequestsViewModel?.updateStateTransaction(
            StateTransaction.COMPLETE.value,
            args.transactionId
        )
        //clientRequestsViewModel?.updateCostUser(args.transactionId)

        val action =
            MapRequestFragmentDirections.actionMapRequestFragmentToCompleteRequestFragment(args.transactionId)
        view?.findNavController()?.navigate(action)
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map


    }


    private fun drawInPoint(lat: Double, lon: Double) {
        val mMap = googleMap!!
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        mMap.clear() //clear old markers

        val googlePlex = CameraPosition.builder()
            .target(LatLng(lat, lon))
            .zoom(10f)
            .bearing(0f)
            .tilt(45f)
            .build()

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lon))
                .title("Posición actual")
                .icon(bitmapDescriptorFromVector(activity, R.drawable.ic_room_service_black_24dp))
        )


        if (positionToGo != null) {
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(positionToGo!!.latitude, positionToGo!!.longitude))
                    .title("Destino")
            )

            drawPath(mMap)
        }

    }

    private fun drawPath(mMap: GoogleMap) {
        val options = PolylineOptions()
        options.color(Color.RED)
        options.width(5f)
        val origin = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        val end = LatLng(positionToGo!!.latitude, positionToGo!!.longitude)
        val url = getURL(origin, end)
        Log.d("RUTE--", url)
        val LatLongB = LatLngBounds.Builder()
        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            val now: DateTime = DateTime()
            //val result = DirectionsApi.newRequest(getGeoContext()).mode(TravelMode.DRIVING).origin(origin.toString()).destination(end.toString()).departureTime(now).await()
            Log.d("RUTE---", result.toString())
            uiThread {
                // When API call is done, create parser and convert into JsonObjec
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result.toString())
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                val routes = json.array<JsonObject>("routes")
                if (routes!!.isNotEmpty()) {


                    val points = routes["legs"]["steps"][0] as JsonArray<JsonObject>
                    // For every element in the JsonArray, decode the polyline string and pass all points to a List
                    val polypts =
                        points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                    val distances = points["distance"]["value"]
                    var distance = 0
                    Log.d("ROUTE-R", distance.toString())
                    for (d in distances) {
                        distance += Integer.valueOf(d.toString())
                    }
                    // Add  points to polyline and bounds
                    options.add(origin)
                    LatLongB.include(origin)
                    for (point in polypts) {
                        options.add(point)
                        LatLongB.include(point)
                    }
                    options.add(end)
                    LatLongB.include(end)
                    // build bounds
                    val bounds = LatLongB.build()
                    // add polyline to the map
                    mMap.addPolyline(options)
                    // show map with route centered
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    Toast.makeText(
                        context,
                        "La distancia total es: $distance m",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


    private fun getURL(from: LatLng, to: LatLng): String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val key = resources.getString(R.string.google_maps_key)
        val params = "$origin&$dest&$sensor&key=$key"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(requireContext(), vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }


    private fun fetchData() {
        Log.d("GO_TO_REQUEST", args.transactionId)
        clientRequestsViewModel?.getTransactionById(args.transactionId)
    }

    private fun fetchViewModels() {
        clientRequestsViewModel =
            ViewModelProviders.of(this).get(ClientRequestsViewModel::class.java)
    }

    private val errorMessageObserver = Observer<String> { errorMessage: String ->
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }

    private val requestsSuccessfulObserver =
        Observer<Transaction> { transaction: Transaction ->
            positionToGo = transaction.address
            transaction.chefId?.get()?.addOnSuccessListener {
                val chef = it.toObject(User::class.java)
                currentLocation =
                    LatLng(chef?.currentAddress!!.latitude, chef.currentAddress.longitude)
                drawInPoint(chef.currentAddress.latitude, chef.currentAddress.longitude)

            }


        }

    private fun setUpLiveDataListeners() {
        clientRequestsViewModel?.requestsSuccessfulLiveDataSingleTransaction?.observe(
            this, requestsSuccessfulObserver

        )
        clientRequestsViewModel?.errorMessageLiveData?.observe(this, errorMessageObserver)
    }
}
