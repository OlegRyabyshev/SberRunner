package xyz.fcr.sberrunner.utils

object Constants {
    const val VALID = "valid"
    const val NON_VALID = "non-valid"

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    // Tracking Options
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

    const val DB_NAME = "sber_runner_table"

    const val TAG_HOME = "TAG_HOME"
    const val TAG_MAP = "TAG_MAP"
    const val TAG_RUN = "TAG_RUN"
    const val TAG_YOU = "TAG_YOU"
    const val TAG_SETTINGS = "TAG_SETTINGS"

    const val CHANNEL_ID = "runningServiceChannel"

    const val DEFAULT_ZOOM = 19f
    const val LOCATION_REQUEST_CODE = 10001
}