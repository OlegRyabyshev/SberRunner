package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapters

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
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.data.repository.shared.ISharedPreferenceWrapper
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.utils.Constants.UNIT_RATIO
import xyz.fcr.sberrunner.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * @param listener [ItemClickListener]
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

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }

        holder.itemView.apply {
            Glide.with(this)
                .load(run.mapImage)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(15)))
                .centerCrop()
                .into(map_item_image_view)

            if (sharedPrefWrapper.isMetric()) {
                val distance = ((((run.distanceInMeters / 1000f) * 100f)).roundToInt() / 100f).toString()
                distance_item_tv.text = distance + holder.itemView.context.getString(R.string.km_addition)
            } else {
                val distance = (((run.distanceInMeters / 1000f * UNIT_RATIO) * 100f).roundToInt() / 100f).toString()
                distance_item_tv.text = distance + holder.itemView.context.getString(R.string.miles_addition)
            }

            duration_item_tv.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.getDefault())
            date_item_tv.text = dateFormat.format(calendar.time)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
}