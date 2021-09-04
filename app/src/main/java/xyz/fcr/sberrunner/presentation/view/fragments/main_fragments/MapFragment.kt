package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments

import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import es.dmoral.toasty.Toasty
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentMapBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.MapViewModel
import xyz.fcr.sberrunner.utils.Constants
import xyz.fcr.sberrunner.utils.Constants.MAP_TRACKING_ZOOM
import xyz.fcr.sberrunner.utils.Constants.MAP_ZOOMED_OUT
import xyz.fcr.sberrunner.utils.Constants.NON_VALID
import xyz.fcr.sberrunner.utils.Constants.RUN_BASIC_PERMISSIONS
import xyz.fcr.sberrunner.utils.hasBasicLocationPermissions
import javax.inject.Inject

/**
 * Фрагмент с картой и кнопкой поиска текущего местоположения.
 * Пользователь может использовать фрагмент для выбора маршрута бега.
 */
class MapFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

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

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.maps_dark_theme))
            }
        }

        viewModel.setToLastKnownLocationIfAny()
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
            getCurrentLocation()
        }

        observeLiveData()
    }

    /**
     * Отслеживание изменений в livedata вьюмодели.
     */
    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.locationLiveData.observe(viewLifecycleOwner, { location: Location -> displayLocation(location) })
        viewModel.historyLiveData.observe(viewLifecycleOwner, { latLng: LatLng -> displayLastKnownLocation(latLng) })
        viewModel.errorLiveData.observe(viewLifecycleOwner, { error: String -> showError(error) })
    }

    private fun showError(text: String) {
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
            addMarker(MarkerOptions().position(currentLocation).title(getString(R.string.current_location)))
            moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
            animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, MAP_TRACKING_ZOOM))
        }
    }

    private fun displayLastKnownLocation(latLng: LatLng) {
        map?.apply {
            moveCamera(CameraUpdateFactory.newLatLng(latLng))
            animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOMED_OUT))
        }
    }

    private fun getCurrentLocation() {
        if (requireContext().hasBasicLocationPermissions()) {
            viewModel.getCurrentLocation()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.dialog_rationale_message),
            Constants.LOCATION_REQUEST_CODE,
            *RUN_BASIC_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionDenied(this, *RUN_BASIC_PERMISSIONS)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularMap.isVisible = isVisible
    }
}