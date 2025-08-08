package tl.bnctl.banking.ui.banking.fragments.information.officesAndATMs

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import tl.bnctl.banking.databinding.FragmentOfficesAndAtmsBinding

class OfficesAndATMsFragment : Fragment() {

    private var _binding: FragmentOfficesAndAtmsBinding? = null
    private val binding get() = _binding!!

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            // Nothing happening for now when new location is received
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 600 * 1000
        locationRequest.fastestInterval = 150 * 1000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOfficesAndAtmsBinding.inflate(inflater, container, false)
        binding.toolbar.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.servicesOfficesMapButton.setOnClickListener {
            findNavController().navigate(OfficesAndATMsFragmentDirections.actionNavFragmentOfficesAndAtmsToNavFragmentOfficesAndAtmsMap())
        }
        binding.servicesAtmsMapButton.setOnClickListener {
            findNavController().navigate(
                OfficesAndATMsFragmentDirections.actionNavFragmentOfficesAndAtmsToNavFragmentOfficesAndAtmsMap(
                    false
                )
            )
        }
        return binding.root
    }

}