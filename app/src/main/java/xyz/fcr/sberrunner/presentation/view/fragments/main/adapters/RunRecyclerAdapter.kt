package xyz.fcr.sberrunner.presentation.view.fragments.main.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.run_item.view.*
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.model.Run
import xyz.fcr.sberrunner.utils.Constants.PATTERN_DATE_HOME
import xyz.fcr.sberrunner.utils.Constants.ROUNDING_CORNERS
import xyz.fcr.sberrunner.utils.TrackingUtility
import xyz.fcr.sberrunner.utils.addDistanceUnits
import xyz.fcr.sberrunner.utils.convertMetersToKilometres
import xyz.fcr.sberrunner.utils.convertMetersToMiles
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Адаптер recyclerview для фрагмента Run
 *
 * @param listener [ItemClickListener] - интерфейс, передающий информацию во врагмент о позиции нажатия.
 */
class RunRecyclerAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<RunRecyclerAdapter.RunViewHolder>() {

    @Inject
    lateinit var sharedPrefWrapper: ISharedPreferenceWrapper

    init {
        App.appComponent.inject(this)
    }

    inner class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.run_item, parent, false)
        )
    }

    override fun getItemCount() = differ.currentList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }

        holder.itemView.apply {
            Glide.with(this)
                .load(run.mapImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(ROUNDING_CORNERS)))
                .centerCrop()
                .into(map_item_image_view)

            val isMetric = sharedPrefWrapper.isMetric()

            if (isMetric) {
                distance_item_tv.text = run.distanceInMeters
                    .convertMetersToKilometres()
                    .toString()
                    .addDistanceUnits(isMetric)
            } else {
                distance_item_tv.text = run.distanceInMeters
                    .convertMetersToMiles()
                    .toString()
                    .addDistanceUnits(isMetric)
            }

            duration_item_tv.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat(PATTERN_DATE_HOME, Locale.getDefault())
            date_item_tv.text = dateFormat.format(calendar.time)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}