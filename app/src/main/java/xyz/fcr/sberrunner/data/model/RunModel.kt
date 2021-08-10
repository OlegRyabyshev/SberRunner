package xyz.fcr.sberrunner.data.model

import android.os.Parcelable
import com.google.type.LatLng
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RunModel(
    private var date: String = "00.00.2000",
    private var kcal: String = "0 kcal",
    private var pace: String = "0 m/km",
    private var duration: String = "00:00:00",
    private var distance: String = "0 km",
    private var listOfPositions: List<Pair<LatLng, Date>>? = null,
) : Parcelable