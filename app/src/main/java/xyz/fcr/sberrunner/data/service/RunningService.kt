package xyz.fcr.sberrunner.data.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.utils.Constants
import xyz.fcr.sberrunner.utils.Constants.ACTION_PAUSE_SERVICE
import xyz.fcr.sberrunner.utils.Constants.ACTION_START_OR_RESUME_SERVICE
import xyz.fcr.sberrunner.utils.Constants.ACTION_STOP_SERVICE
import xyz.fcr.sberrunner.utils.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import xyz.fcr.sberrunner.utils.Constants.LOCATION_UPDATE_INTERVAL
import xyz.fcr.sberrunner.utils.Constants.NOTIFICATION_CHANNEL_ID
import xyz.fcr.sberrunner.utils.Constants.NOTIFICATION_CHANNEL_NAME
import xyz.fcr.sberrunner.utils.Constants.NOTIFICATION_ID
import xyz.fcr.sberrunner.utils.TrackingUtility
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class RunningService : LifecycleService() {

    private val timeRunInSeconds = MutableLiveData<Long>()

    private var isFirstRun = true
    private var serviceKilled = false

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var currentNotification: NotificationCompat.Builder

    init {
        App.appComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        currentNotification = baseNotificationBuilder
        postInitialValues()

        isTracking.observe(this) {
            updateNotificationTrackingState(it)
            updateLocationChecking(it)
        }
    }

    private fun postInitialValues() {
        timeRunInMillis.postValue(0L)
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                        serviceKilled = false
                    } else {
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    stopService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isTimerEnabled = false
        isTracking.postValue(false)
    }

    private fun stopService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationChecking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            if (isTracking.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    private var isTimerEnabled = false
    private var lapTime = 0L // time since we started the timer
    private var timeRun = 0L // total time of the timer
    private var timeStarted = 0L // the time when we started the timer
    private var lastSecondTimestamp = 0L


    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and time started
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new laptime
                timeRunInMillis.postValue(timeRun + lapTime)
                // if a new second was reached, we want to update timeRunInSeconds, too
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    /**
     * This adds the location to the last list of pathPoints.
     */
    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    /**
     * Will add an empty polyline in the pathPoints list or initialize it if empty.
     */
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    /**
     * Starts this service as a foreground service and creates the necessary notification
     */
    private fun startForegroundService() {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, currentNotification.build())
        startTimer()
        isTracking.postValue(true)

        // updating notification
        timeRunInSeconds.observe(this) {
            if (!serviceKilled) {
                val notification =
                    currentNotification.setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000L))

                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        }
    }

    /**
     * Updates the action buttons of the notification
     */
    private fun updateNotificationTrackingState(isTracking: Boolean) {
        val notificationActionText = if (isTracking) "Pause" else "Resume"

        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, RunningService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, RunningService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotification.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotification, ArrayList<NotificationCompat.Action>())
        }

        if (!serviceKilled) {
            currentNotification = baseNotificationBuilder
                .addAction(R.drawable.ic_profile, notificationActionText, pendingIntent)

            notificationManager.notify(NOTIFICATION_ID, currentNotification.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}