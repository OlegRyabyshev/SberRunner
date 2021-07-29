package xyz.fcr.sberrunner.ui.fragments.main_fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

//    private fun checkPermission() {
//        when {
//            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED -> {
//                getLocation()
//                return
//            }
//            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
//                showRationaleDialog()
//            }
//            else -> {
//                requestPermission()
//            }
//        }
//    }
//
//    private fun requestPermission() {
//        ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//    }
//
//    private fun showRationaleDialog() {
//        AlertDialog.Builder(requireContext())
//            .setTitle(getString(R.string.dialog_rationale_title))
//            .setMessage(getString(R.string.dialog_rationale_message))
//            .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
//                requestPermission()
//            }
//            .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//            .show()
//    }
//
//    private fun getLocation() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//
//            if (isGPSEnabled) {
//                locationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER,
//                    REFRESH_PERIOD,
//                    MINIMAL_DISTANCE,
//                    onLocationListener
//                )
//
//            } else {
//                showRationaleDialog()
//            }
//        }
//    }
//
//    private val onLocationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            context?.let { getAddressAsync(it, location) }
//        }
//
//        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//        override fun onProviderEnabled(provider: String) {}
//        override fun onProviderDisabled(provider: String) {}
//    }
}