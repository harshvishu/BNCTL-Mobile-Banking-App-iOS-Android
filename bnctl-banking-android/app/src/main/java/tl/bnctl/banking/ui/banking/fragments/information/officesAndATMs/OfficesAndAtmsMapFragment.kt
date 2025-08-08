package tl.bnctl.banking.ui.banking.fragments.information.officesAndATMs

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import tl.bnctl.banking.BuildConfig
import tl.bnctl.banking.R
import tl.bnctl.banking.databinding.FragmentOfficesAndAtmsMapBinding
import tl.bnctl.banking.ui.banking.fragments.information.news.NewsFragment
import tl.bnctl.banking.ui.utils.DialogFactory


class OfficesAndAtmsMapFragment : Fragment() {

    companion object {
        val CENTRAL_BULGARIA_COORDINATES = LatLng(
            BuildConfig.MAPS_COUNTRY_LATITUDE.toDouble(),
            BuildConfig.MAPS_COUNTRY_LONGITUDE.toDouble()
        )
        val TAG: String = NewsFragment::class.java.name
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private var _binding: FragmentOfficesAndAtmsMapBinding? = null
    private val binding get() = _binding!!

    private val navArgs: OfficesAndAtmsMapFragmentArgs by navArgs()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel: OfficesAndAtmsMapViewModel by viewModels { OfficesAndATMsViewModelFactory() }

    private lateinit var callback: OnMapReadyCallback

    private lateinit var locationRequest: LocationRequest

    private var infoDialog: AlertDialog? = null

    private var googleMapObj: GoogleMap? = null

    private var lastLocation: Location? = null

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(location: LocationResult) {
            if (lastLocation == null) {
                lastLocation = location.lastLocation
                val latitude = location.lastLocation!!.latitude
                val longitude = location.lastLocation!!.longitude
                val zoom = 15f
                // set marker for your current location
                googleMapObj!!.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
                googleMapObj!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(latitude, longitude), zoom
                    )
                )
            }
        }
    }

    private val callbackOffices = OnMapReadyCallback { googleMap ->
        googleMapObj = googleMap
        createMarkerForEachLocation(googleMap, R.drawable.ic_office_marker)
        getCurrentLocation(googleMap)
        viewModel.fetchBranchesLocations()
    }

    private val callbackATMs = OnMapReadyCallback { googleMap ->
        googleMapObj = googleMap
        createMarkerForEachLocation(googleMap, R.drawable.ic_atm_marker)
        getCurrentLocation(googleMap)
        viewModel.fetchAtmsLocations()
    }

    private fun createMarkerForEachLocation(googleMap: GoogleMap, drawable: Int) {
        viewModel.locations.observe(viewLifecycleOwner) { listLocations ->
            listLocations.forEach { atm ->
                googleMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(atm.latitude, atm.longitude))
                        .title(atm.nameEn)
                        .snippet(atm.address)
                        .icon(
                            getDrawable(resources, drawable, null)?.toBitmap()
                                ?.let { BitmapDescriptorFactory.fromBitmap(it) })
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationPermissionCheck()
        callback = if (navArgs.showOffices) {
            callbackOffices
        } else {
            callbackATMs
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val areAllPermissionsAreApproved = permissions.entries.all { entry ->
                val isGranted = entry.value
                isGranted
            }
            if (areAllPermissionsAreApproved) {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?
                mapFragment?.getMapAsync(callback)
            }
        }

    private fun locationPermissionCheck() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 100
        locationRequest.fastestInterval = 10
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        requestPermissionLauncher.launch(PERMISSIONS)

        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (infoDialog == null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            infoDialog = DialogFactory.createInformativeDialog(
                requireContext(),
                R.string.offices_and_atms_enable_gps_dialog_title,
                R.string.offices_and_atms_enable_gps_dialog_text,
                R.string.offices_and_atms_location_permission_dialog_button_text
            )
        }

        if (infoDialog != null) {
            infoDialog!!.show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfficesAndAtmsMapBinding.inflate(inflater, container, false)
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        if (!navArgs.showOffices) {
            binding.toolbarTitle.text = getString(R.string.atms_title)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(googleMap: GoogleMap) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val zoom = 15f
                // set marker for your current location
                googleMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)))
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(latitude, longitude), zoom
                    )
                )
            } else {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
        lastLocation
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                CENTRAL_BULGARIA_COORDINATES, BuildConfig.MAPS_COUNTRY_ZOOM.toFloat()
            )
        )
    }
}
