package xyz.fcr.sberrunner.presentation.model

import android.graphics.drawable.Drawable

/**
 * Модель прогресса для вывода детальной инфомации во вкладке Прогресс
 *
 * @param title [String] - наименование прогресса
 * @param value [String] - строка инфомации о прогрессе
 * @param icon [Drawable] - изображение прогресса
 */
data class Progress(
    var title: String?,
    var value: String?,
    var icon: Drawable?
)