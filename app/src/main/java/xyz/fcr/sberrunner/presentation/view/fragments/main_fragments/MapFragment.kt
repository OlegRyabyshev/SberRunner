package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentMapBinding
import xyz.fcr.sberrunner.utils.Constants.DEFAULT_ZOOM
import xyz.fcr.sberrunner.utils.Constants.LOCATION_REQUEST_CODE
import xyz.fcr.sberrunner.utils.Constants.NON_VALID
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.MapViewModel
import javax.inject.Inject

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: MapViewModel by viewModels { factory }

    init {
        App.appComponent.inject(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
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

        binding.fabFindMe.setOnClickListener {
            checkPermission()
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.locationLiveData.observe(viewLifecycleOwner, { location: Location -> displayLocation(location) })
        viewModel.errorLiveData.observe(viewLifecycleOwner, { error: String -> showError(error) })
    }

    private fun showError(text: String) {
        if (text == NON_VALID)
            when (text) {
                NON_VALID -> Toasty.error(
                    requireContext(),
                    requireContext().getString(R.string.cant_find_location),
                    Toast.LENGTH_SHORT
                ).show()
                else -> Toasty.error(
                    requireContext(),
                    text,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayLocation(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)

        map?.apply {
            addMarker(MarkerOptions().position(currentLocation).title("Current location"))
            moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
            animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, DEFAULT_ZOOM))
        }
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.getCurrentLocation()
                return
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> showRationaleDialog()

            else -> requestPermission()
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_rationale_title))
            .setMessage(getString(R.string.dialog_rationale_message))
            .setPositiveButton(getString(R.string.dialog_rationale_give_access)) { _, _ ->
                requestPermission()
            }
            .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_REQUEST_CODE
        )
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularMap.isVisible = isVisible
        binding.fabFindMe.isVisible = !isVisible
    }
}