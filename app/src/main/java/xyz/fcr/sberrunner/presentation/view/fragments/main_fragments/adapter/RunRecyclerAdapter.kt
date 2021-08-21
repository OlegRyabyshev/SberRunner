package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapter.RunRecyclerAdapter.RunnerViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.room.RunEntity
import xyz.fcr.sberrunner.databinding.RunItemBinding

/**
 * Адаптер для отображения списка погоды.
 *
 * @param weatherModelList [RunEntity] список из погоды на неделю
 * @param listener [ItemClickListener] лисенер для перехвата нажатия по холдерам
 *
 * @author Рябышев Олег on 05.08.2021
 */
class RunRecyclerAdapter(
    private val weatherModelList: List<RunEntity>,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RunnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunnerViewHolder {
        return RunnerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.run_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RunnerViewHolder, position: Int) {
        holder.bindView(weatherModelList[position])
        holder.itemView.setOnClickListener { listener.onItemClick(position) }
    }

    override fun getItemCount() = weatherModelList.size

    class RunnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: RunItemBinding = RunItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindView(weatherModel: RunEntity) {
//            binding.dayTextView.text = "Day ${weatherModel.day}:"
//            binding.tempTextView.text = weatherModel.max
//            binding.weatherImage.setImageResource(weatherModel.icon)
        }
    }
}