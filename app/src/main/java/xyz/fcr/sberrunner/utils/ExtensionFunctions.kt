package xyz.fcr.sberrunner.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import pub.devrel.easypermissions.EasyPermissions
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.presentation.App
import kotlin.math.roundToInt

/**
 * Добавляет единицы дистанции к строке
 *
 * @param isMetric [Boolean] - флаг системы измерений
 * @return [Float] - полная строка со добавкой дистанции
 */
fun String.addDistanceUnits(isMetric: Boolean): String {
    return if (isMetric) {
        this.plus(App.appComponent.context().resources.getString(R.string.km_addition))
    } else {
        this.plus(App.appComponent.context().resources.getString(R.string.miles_addition))
    }
}

/**
 * Добавляет скорость к строке
 *
 * @param isMetric [Boolean] - флаг системы измерений
 * @return [Float] - полная строка со скоростью
 */
fun String.addSpeedUnits(isMetric: Boolean): String {
    return if (isMetric) {
        this.plus(App.appComponent.context().resources.getString(R.string.km_h_addition))
    } else {
        this.plus(App.appComponent.context().resources.getString(R.string.mph_addition))
    }
}

/**
 * Добавляет калории к строке
 *
 * @return [Float] - полная строка с калориями
 */
fun String.addCalories(): String {
    return this.plus(App.appComponent.context().resources.getString(R.string.kcal_addition))
}

/**
 * Получение среднего значения
 *
 * @param isMetric [Boolean] - флаг системы измерений
 * @param count [Int] - количество значений
 *
 * @return [Float] - среднее значение
 */
fun Long.getAverage(isMetric: Boolean, count: Int): Double {
    return if (isMetric) {
        ((((this / 1000f) / count) * 100f).roundToInt() / 100f).toDouble()
    } else {
        ((((this / 1000f / count * Constants.UNIT_RATIO) * 100f)).roundToInt() / 100f).toDouble()
    }
}

/**
 * Конвертация метров в мили
 *
 * @return [Float] - мили
 */
fun Long.convertMetersToMiles(): Float {
    val distanceInMiles = (this / 1000.0f) * Constants.UNIT_RATIO
    return (distanceInMiles * 100.0f).roundToInt() / 100.0f
}

/**
 * Конвертация метров в километры
 *
 * @return [Float] - километры
 */
fun Long.convertMetersToKilometres(): Float {
    val distanceInMiles = this / 1000.0f
    return (distanceInMiles * 100.0f).roundToInt() / 100.0f
}

/**
 * Конвертация Км/ч в Мили/ч
 *
 * @return [Float] - скорость в Мили/ч
 */
fun Float.convertKMHtoMPH(): Float {
    val distanceInMiles = this * Constants.UNIT_RATIO
    return (distanceInMiles * 100.0f).roundToInt() / 100.0f
}

/**
 * Проверка на наличие разрешений
 *
 * @return [Boolean] - значение доступа (да или нет)
 */
fun Context.hasBasicLocationPermissions(): Boolean {
    return EasyPermissions.hasPermissions(this, *Constants.RUN_BASIC_PERMISSIONS)
}

/**
 * Проверка на наличие background разрешения
 *
 * @return [Boolean] - значение доступа (да или нет)
 */
fun Context.hasBackgroundLocationPermission(): Boolean {
    return EasyPermissions.hasPermissions(this, *Constants.RUN_ADDITIONAL_PERMISSION_Q)
}

fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}