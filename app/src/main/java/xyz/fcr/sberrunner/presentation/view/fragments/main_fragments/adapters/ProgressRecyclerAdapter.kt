package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.ProgressItemBinding
import xyz.fcr.sberrunner.presentation.model.Progress

class ProgressRecyclerAdapter(
    private val listOfProgress: List<Progress>
) : RecyclerView.Adapter<ProgressRecyclerAdapter.ProgressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        return ProgressViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.progress_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.bindView(listOfProgress[position])
    }

    override fun getItemCount(): Int {
        return listOfProgress.size
    }

    class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ProgressItemBinding = ProgressItemBinding.bind(itemView)

        fun bindView(progress: Progress) {
            binding.detailedDistanceImage.setImageDrawable(progress.icon)
            binding.detailedTitle.text = progress.title
            binding.detailedValue.text = progress.value
        }
    }
}