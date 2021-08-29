package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.run_item.view.*
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.utils.TrackingUtility
import java.text.SimpleDateFormat
import java.util.*

/**
 * @param listener [ItemClickListener]
 */
class RunRecyclerAdapter(private val listener: ItemClickListener) :
    RecyclerView.Adapter<RunRecyclerAdapter.RunViewHolder>() {

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

            distance_item_tv.text = (run.distanceInMeters / 1000f).toString()
            duration_item_tv.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
            avg_speed_item_tv.text = run.avgSpeedInKMH.toString()

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }

            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
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