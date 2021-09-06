package xyz.fcr.sberrunner.utils

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Хранилище константных значений.
 */
object Constants {
    const val VALID = "valid"
    const val NON_VALID = "non-valid"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

    const val REQUEST_CODE_BASIC_PERMISSION = 100
    const val REQUEST_CODE_BACKGROUND_PERMISSION = 101

    // Room
    const val DB_NAME = "sber_runner_table"

    // Tags or tabs
    const val TAG_HOME = "TAG_HOME"
    const val TAG_MAP = "TAG_MAP"
    const val TAG_RUN = "TAG_RUN"
    const val TAG_PROGRESS = "TAG_PROGRESS"
    const val TAG_SETTINGS = "TAG_SETTINGS"

    const val LOCATION_REQUEST_CODE = 10001
    const val TIMER_UPDATE_INTERVAL = 50L

    //Shared const
    const val THEME_KEY = "theme_key"
    const val WEIGHT_KEY = "weight_key"
    const val NAME_KEY = "name_key"

    const val LOG_OUT = "log_out"
    const val DEL_ACCOUNT = "del_account"

    const val MAP_LAT_KEY = "map_lat_key"
    const val MAP_LON_KEY = "map_lon_key"

    const val RUN_LAT_KEY = "run_lat_key"
    const val RUN_LON_KEY = "run_lon_key"

    const val CURRENT_RUN_ID = "current_run_id"

    const val UNITS_KEY = "units_key"
    const val METRIC_UNIT = "metric_unit"

    const val VOICE_KEY = "voice_enabled_key"

    const val MOSCOW_LAT = 55.75f
    const val MOSCOW_LON = 37.61f

    // Service
    const val CHANNEL_ID = "runningServiceChannel"

    // Service actions
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    // Service notification
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1

    // Map Options
    const val POLYLINE_WIDTH = 12f

    const val MAP_TRACKING_ZOOM = 17f
    const val MAP_ZOOMED_OUT = 13f

    // Permissions
    val RUN_BASIC_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    val RUN_ADDITIONAL_PERMISSION_Q = arrayOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    // Firestore
    const val USER_TABLE = "user"
    const val RUNS_TABLE = "runs"

    const val NAME = "name"
    const val WEIGHT = "weight"

    // Convert
    const val UNIT_RATIO = 0.6214f

    const val ROWS_IN_RECYCLER = 2

    const val WEIGHT_INT_DEFAULT = 70

    const val PATTERN_DATE_DETAILED = "dd, MMM, yyyy HH:mm"
    const val PATTERN_DATE_HOME = "dd/MM/yyyy hh:mm"

    const val ROUNDING_CORNERS = 40
}