package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions
import es.dmoral.toasty.Toasty
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.service.RunningService
import xyz.fcr.sberrunner.databinding.FragmentRunBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.RunViewModel
import xyz.fcr.sberrunner.utils.Constants.ACTION_PAUSE_SERVICE
import xyz.fcr.sberrunner.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import xyz.fcr.sberrunner.utils.Constants.ACTION_STOP_SERVICE
import xyz.fcr.sberrunner.utils.Constants.MAP_TRACKING_ZOOM
import xyz.fcr.sberrunner.utils.Constants.POLYLINE_WIDTH
import xyz.fcr.sberrunner.utils.Constants.REQUEST_CODE_LOCATION_PERMISSION
import xyz.fcr.sberrunner.utils.Constants.RUN_PERMISSIONS
import xyz.fcr.sberrunner.utils.TrackingUtility
import java.util.*
import javax.inject.Inject
import kotlin.math.round

class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null

    private var isTracking = false
    private var curTimeInMillis = 0L
    private var pathPoints = mutableListOf<MutableList<LatLng>>()

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: RunViewModel by viewModels { factory }

    @set:Inject
    var weight: Int = 70

    init {
        App.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync {
            map = it
            enableDarkThemeIfRequired()
            viewModel.setToLastKnownLocationIfAny()
            addAllPolylines()
        }

        binding.fabStart.setOnClickListener {
            toggleRun()
        }

        binding.fabPause.setOnClickListener {
            toggleRun()
        }

        binding.fabFinish.setOnClickListener {
            if (pathPoints.size >= 2) {
                zoomToWholeTrack()
                endRunAndSaveToDB()
            } else {
                stopRun()
                Toasty.info(requireContext(), getString(R.string.not_enough_data), Toasty.LENGTH_SHORT).show()
            }
        }

        subscribeToObservers()
    }

    private fun enableDarkThemeIfRequired() {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.maps_dark_theme))
        }
    }

    private fun sendActionToService(action: String) {
        Intent(requireContext(), RunningService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    private fun subscribeToObservers() {
        RunningService.isTracking.observe(viewLifecycleOwner, {
            updateTrackingUI(it)
        })

        RunningService.isPaused.observe(viewLifecycleOwner, {
            updatePausedUI(it)
        })

        RunningService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })

        RunningService.timeRunInMillis.observe(viewLifecycleOwner, {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(it)
            binding.durationTv.text = formattedTime
        })

        viewModel.historyLiveData.observe(viewLifecycleOwner, {
            map?.apply {
                moveCamera(CameraUpdateFactory.newLatLng(it))
                animateCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_TRACKING_ZOOM))
            }
        })
    }

    /**
     * Передвигает камеру на последнюю геопозицию
     */
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_TRACKING_ZOOM
                )
            )
        }
    }

    /**
     * Добавляет все polyline (линии) в лист pathPoints для отображения
     */
    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(ContextCompat.getColor(requireContext(), R.color.main_green))
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
        }
    }

    /**
     * Отображает polyline (линию) между двумя последними координатами
     */
    private fun addLatestPolyline() {
        // only add polyline if we have at least two elements in the last polyline
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {

            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()

            val polylineOptions = PolylineOptions()
                .color(ContextCompat.getColor(requireContext(), R.color.main_green))
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)

            map?.addPolyline(polylineOptions)
        }
    }

    private fun updateTrackingUI(isTracking: Boolean) {
        this.isTracking = isTracking

        if (!isTracking && curTimeInMillis > 0L) {
            binding.fabStart.isVisible = true
            binding.fabPause.isVisible = false
        } else if (isTracking) {
            binding.fabStart.isVisible = false
            binding.fabPause.isVisible = true
            binding.fabFinish.isVisible = false
        }
    }

    private fun updatePausedUI(isPaused: Boolean) {
        binding.fabFinish.isVisible = isPaused
        binding.fabStart.text = if (isPaused) getString(R.string.resume) else getString(R.string.start)
    }

    private fun zoomToWholeTrack() {
        val bounds = LatLngBounds.Builder()

        for (polyline in pathPoints) {
            for (point in polyline) {
                bounds.include(point)
            }
        }

        val width = binding.mapView.width
        val height = binding.mapView.height

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                width,
                height,
                (height * 0.05f).toInt()
            )
        )
    }

    @AfterPermissionGranted(REQUEST_CODE_LOCATION_PERMISSION)
    private fun toggleRun() {
        if (EasyPermissions.hasPermissions(requireContext(), *RUN_PERMISSIONS)) {
            if (isTracking) {
                sendActionToService(ACTION_PAUSE_SERVICE)
            } else {
                sendActionToService(ACTION_START_OR_RESUME_SERVICE)
            }
        } else {
            requestPermissions()
        }
    }

    private fun endRunAndSaveToDB() {
        map?.snapshot { bmp ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed = round((distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) * 10) / 10f
            val timestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = Run(distanceInMeters, timestamp, curTimeInMillis, avgSpeed, caloriesBurned, bmp)

            viewModel.insertRun(run)
            Toasty.success(requireContext(), "Run saved", Toasty.LENGTH_SHORT).show()
            stopRun()
        }
    }

    private fun requestPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.permission_rationale),
            REQUEST_CODE_LOCATION_PERMISSION,
            *RUN_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toasty.info(requireContext(), "We have your permission", Toasty.LENGTH_SHORT).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionDenied(this, *RUN_PERMISSIONS)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    private fun stopRun() {
        binding.durationTv.text = getString(R.string.duration_zero)
        sendActionToService(ACTION_STOP_SERVICE)
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}