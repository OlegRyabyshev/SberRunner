package xyz.fcr.sberrunner.utils

import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App
import kotlin.math.roundToInt

fun String.addDistanceUnits(isMetric: Boolean): String {
    return if (isMetric) {
        this.plus(App.appComponent.context().resources.getString(R.string.km_addition))
    } else {
        this.plus(App.appComponent.context().resources.getString(R.string.miles_addition))
    }
}

fun String.addSpeedUnits(isMetric: Boolean): String {
    return if (isMetric) {
        this.plus(App.appComponent.context().resources.getString(R.string.km_h_addition))
    } else {
        this.plus(App.appComponent.context().resources.getString(R.string.mph_addition))
    }
}

fun String.addCalories(): String {
    return this.plus(App.appComponent.context().resources.getString(R.string.kcal_addition))
}

fun Int.getAverage(isMetric: Boolean, count: Int): Double {
    return if (isMetric) {
        ((((this / 1000f) / count) * 100f).roundToInt() / 100f).toDouble()
    } else {
        ((((this / 1000f / count * Constants.UNIT_RATIO) * 100f)).roundToInt() / 100f).toDouble()
    }
}

fun Int.convertMetersToMiles(): Float {
    val distanceInMiles = (this / 1000.0f) * Constants.UNIT_RATIO
    return (distanceInMiles * 100.0f).roundToInt() / 100.0f
}

fun Float.convertKMHtoMPH(): Float {
    val distanceInMiles = this * Constants.UNIT_RATIO
    return (distanceInMiles * 100.0f).roundToInt() / 100.0f
}