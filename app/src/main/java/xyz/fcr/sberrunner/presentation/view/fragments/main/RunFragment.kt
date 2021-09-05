package xyz.fcr.sberrunner.presentation.view.fragments.main

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.service.RunningService
import xyz.fcr.sberrunner.databinding.FragmentRunBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.viewmodels.main.RunViewModel
import xyz.fcr.sberrunner.utils.Constants.ACTION_PAUSE_SERVICE
import xyz.fcr.sberrunner.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import xyz.fcr.sberrunner.utils.Constants.ACTION_STOP_SERVICE
import xyz.fcr.sberrunner.utils.Constants.MAP_TRACKING_ZOOM
import xyz.fcr.sberrunner.utils.Constants.POLYLINE_WIDTH
import xyz.fcr.sberrunner.utils.Constants.REQUEST_CODE_BACKGROUND_PERMISSION
import xyz.fcr.sberrunner.utils.Constants.REQUEST_CODE_BASIC_PERMISSION
import xyz.fcr.sberrunner.utils.Constants.RUN_ADDITIONAL_PERMISSION_Q
import xyz.fcr.sberrunner.utils.Constants.RUN_BASIC_PERMISSIONS
import xyz.fcr.sberrunner.utils.Constants.UNIT_RATIO
import xyz.fcr.sberrunner.utils.Constants.WEIGHT_INT_DEFAULT
import xyz.fcr.sberrunner.utils.TrackingUtility
import xyz.fcr.sberrunner.utils.hasBackgroundLocationPermission
import xyz.fcr.sberrunner.utils.hasBasicLocationPermissions
import java.util.*
import javax.inject.Inject
import kotlin.math.round

/**
 * Фрагмент для запуска сервиса бега и отслеживаний текущей статистики бега,
 * включая маршрут, дистанцию, время, затраченные калории и среднюю скорость.
 */
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

    private var isMetric = false
    private var weight = WEIGHT_INT_DEFAULT

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

        viewModel.setUnits()
        viewModel.setWeight()

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
            if (isEnoughDataToFinish()) {
                zoomToWholeTrack()
                endRunAndSaveToDB()
            } else {
                showWarningDialog()
            }
        }

        observeLiveData()
    }

    /**
     * Вывод диалога при нажатии окончания забегаа, предупреждающего пользователя о том,
     * что сервис не получил достаточное количество опорных точек геопозиции для сохранения объекта бега.
     */
    private fun showWarningDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.not_enough_data))
            .setMessage(getString(R.string.not_enough_data_msg))
            .setPositiveButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.finish)) { dialog, _ ->
                dialog.dismiss()
                stopRun()
            }
            .show()
    }

    /**
     * Проверка количества опорных точек геопозиции для сохранения объекта бега.
     */
    private fun isEnoughDataToFinish(): Boolean {
        var pointsCounter = 0

        for (polyline in pathPoints) {
            for (point in polyline) {
                pointsCounter++
            }
        }

        return pointsCounter >= 2
    }

    /**
     * Загрузка темной карты при включенной в приложении темной темы.
     */
    private fun enableDarkThemeIfRequired() {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.maps_dark_theme))
        }
    }

    /**
     * Отправка интента на сервис (start/resume/pause/finish)
     */
    private fun sendActionToService(action: String) {
        Intent(requireContext(), RunningService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    /**
     * Отслеживание изменений в livedata вьюмодели и сервиса.
     */
    private fun observeLiveData() {
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

        RunningService.avgSpeed.observe(viewLifecycleOwner, {
            if (isMetric) {
                binding.speedTv.text = it
                    .toString()
                binding.speedInfo.setText(R.string.km_h)
            } else {
                binding.speedTv.text = String
                    .format("%.02f", it * UNIT_RATIO)
                binding.speedInfo.setText(R.string.mph)
            }
        })

        RunningService.distance.observe(viewLifecycleOwner, {
            if (isMetric) {
                binding.distanceTv.text = String.format("%.02f", it)
                binding.distanceInfo.setText(R.string.kilometers)
            } else {
                binding.distanceTv.text = String.format("%.02f", it * UNIT_RATIO)
                binding.distanceInfo.setText(R.string.miles)
            }
        })

        RunningService.calories.observe(viewLifecycleOwner, {
            binding.caloriesTv.text = ((it * weight).toInt()).toString()
            binding.caloriesInfo.setText(R.string.kcal)
        })

        viewModel.historyLiveData.observe(viewLifecycleOwner, {
            map?.apply {
                moveCamera(CameraUpdateFactory.newLatLng(it))
                animateCamera(CameraUpdateFactory.newLatLngZoom(it, MAP_TRACKING_ZOOM))
            }
        })

        viewModel.unitsLiveData.observe(viewLifecycleOwner) { isMetricFromViewModel: Boolean ->
            isMetric = isMetricFromViewModel
        }

        viewModel.weightLiveData.observe(viewLifecycleOwner) { weightFromViewModel: Int ->
            weight = weightFromViewModel
        }
    }

    /**
     * Передвигает камеру на последнюю геопозицию
     */
    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_TRACKING_ZOOM)
            )
            viewModel.saveLastLocation(pathPoints.last().last())
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

    /**
     * Обновление видимости кнопок.
     */
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

    /**
     * Обновление текста кнопки Start (Выводит Resume, если активность уже была запущена и находится в паузе)
     */
    private fun updatePausedUI(isPaused: Boolean) {
        binding.fabFinish.isVisible = isPaused
        binding.fabStart.text = if (isPaused) getString(R.string.resume) else getString(R.string.start)
    }

    /**
     * Перемещает камеру, захватывая весь пройденный путь (для сохранения изображения)
     */
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

    /**
     * Проверка разрешений бега и отпраавляет интект о запуске/паузе в сервис.
     */
    private fun toggleRun() {
        if (requireContext().hasBasicLocationPermissions()) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!requireContext().hasBackgroundLocationPermission()) {
                    requestBackgroundPermission()
                    return
                }
            }

            if (isTracking) {
                sendActionToService(ACTION_PAUSE_SERVICE)
            } else {
                sendActionToService(ACTION_START_OR_RESUME_SERVICE)
            }
        } else {
            requestBasicPermissions()
        }
    }

    /**
     * Делает изображение маршрута и сохраняет объект бега
     */
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
            Toasty.success(requireContext(), getString(R.string.run_saved), Toasty.LENGTH_SHORT).show()
            stopRun()
        }
    }

    /**
     * Запрос на базовые разрешения отслеживания геопозиции.
     */
    private fun requestBasicPermissions() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.permission_rationale),
            REQUEST_CODE_BASIC_PERMISSION,
            *RUN_BASIC_PERMISSIONS
        )
    }

    /**
     * Запрос на фоновое разрешение отслеживания геопозиции.
     */
    private fun requestBackgroundPermission() {
        EasyPermissions.requestPermissions(
            this,
            getString(R.string.permission_rationale_q),
            REQUEST_CODE_BACKGROUND_PERMISSION,
            *RUN_ADDITIONAL_PERMISSION_Q
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            REQUEST_CODE_BASIC_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackgroundPermission()
                } else {
                    toggleRun()
                }
            }
            REQUEST_CODE_BACKGROUND_PERMISSION -> toggleRun()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_CODE_BASIC_PERMISSION) {
            if (EasyPermissions.somePermissionDenied(this, *RUN_BASIC_PERMISSIONS)) {
                AppSettingsDialog.Builder(this).build().show()
            }
        } else if (requestCode == REQUEST_CODE_BACKGROUND_PERMISSION) {
            if (EasyPermissions.somePermissionDenied(this, *RUN_ADDITIONAL_PERMISSION_Q)) {
                AppSettingsDialog.Builder(this).build().show()
            }
        }
    }

    /**
     * Остановка забега.
     */
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