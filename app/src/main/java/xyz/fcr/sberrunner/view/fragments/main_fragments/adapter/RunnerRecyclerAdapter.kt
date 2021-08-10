package xyz.fcr.sberrunner.view.fragments.main_fragments.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import xyz.fcr.sberrunner.view.fragments.main_fragments.adapter.RunnerRecyclerAdapter.RunnerViewHolder
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import xyz.fcr.sberrunner.data.model.RunModel
import xyz.fcr.lesson19_mvvm.databinding.WeatherItemBinding
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.RunItemBinding

/**
 * Адаптер для отображения списка погоды.
 *
 * @param weatherModelList [RunModel] список из погоды на неделю
 * @param listener [ItemClickListener] лисенер для перехвата нажатия по холдерам
 *
 * @author Рябышев Олег on 05.08.2021
 */
class RunnerRecyclerAdapter(
    private val weatherModelList: List<RunModel>,
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

    override fun getItemCount(): Int {
        return weatherModelList.size
    }

    class RunnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: RunItemBinding = RunItemBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindView(weatherModel: RunModel) {
            binding.dayTextView.text = "Day ${weatherModel.day}:"
            binding.tempTextView.text = weatherModel.max
            binding.weatherImage.setImageResource(weatherModel.icon)
        }
    }
}