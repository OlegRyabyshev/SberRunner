package xyz.fcr.sberrunner.presentation.view.fragments.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.model.RunEntity
import xyz.fcr.sberrunner.databinding.FragmentProgressBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.model.Progress
import xyz.fcr.sberrunner.presentation.view.fragments.main.adapters.ProgressRecyclerAdapter
import xyz.fcr.sberrunner.presentation.viewmodels.main.ProgressViewModel
import xyz.fcr.sberrunner.utils.*
import xyz.fcr.sberrunner.utils.Constants.ROWS_IN_RECYCLER
import xyz.fcr.sberrunner.utils.Constants.UNIT_RATIO
import javax.inject.Inject

/**
 * Фрагмент с выводом инфомации о всех забегах в виде карточек-статистик.
 */
class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerAdapter: ProgressRecyclerAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: ProgressViewModel by viewModels { factory }

    init {
        App.appComponent.inject(this)
    }

    private var isMetric = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setUnits()
        viewModel.updateListOfRuns()
        observeLiveData()
    }

    /**
     * Отслеживание изменений в livedata вьюмодели.
     */
    private fun observeLiveData() {
        viewModel.listOfRunsLiveData.observe(viewLifecycleOwner) { runs: List<RunEntity> ->
            if (runs.isNotEmpty()) {
                initRecycler(runs)
                displayRecycler(true)
            } else {
                displayRecycler(false)
            }
        }

        viewModel.unitsLiveData.observe(viewLifecycleOwner) { units: Boolean ->
            isMetric = units
        }
    }

    private fun initRecycler(runs: List<RunEntity>) {

        val listOfProgressInfo = listOf(
            progressTotalRuns(runs),
            progressAvgSpeed(runs),
            progressTotalDistance(runs),
            progressAvgDistance(runs),
            progressTotalDuration(runs),
            progressAvgDuration(runs),
            progressTotalCalories(runs),
            progressAvgCalories(runs)
        )

        recyclerAdapter = ProgressRecyclerAdapter(listOfProgressInfo)

        binding.recyclerViewProgress.apply {
            adapter = recyclerAdapter
            layoutManager = GridLayoutManager(requireContext(), ROWS_IN_RECYCLER)
        }

    }

    private fun displayRecycler(isVisible: Boolean) {
        binding.recyclerViewProgress.isVisible = isVisible
        binding.lottieEmptyListProgress.isVisible = !isVisible
        binding.textViewProgress.isVisible = !isVisible
    }

    private fun progressTotalRuns(runs: List<RunEntity>): Progress {
        val title: String = resources.getString(R.string.total_runs)
        val value: String = runs.size.toString()
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_runs)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgSpeed(runs: List<RunEntity>): Progress {
        val count = runs.size
        val title: String = resources.getString(R.string.avg_speed)

        val sum = if (isMetric) {
            runs.sumOf { it.avgSpeedInKMH.toInt() }
        } else {
            runs.sumOf { (it.avgSpeedInKMH.toFloat() * UNIT_RATIO).toInt() }
        }

        val value = (sum / count).toString().addSpeedUnits(isMetric)

        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_speed)!!

        return Progress(title, value, icon)
    }

    private fun progressTotalDistance(runs: List<RunEntity>): Progress {
        val title: String = resources.getString(R.string.total_distance)

        val distance: Double = runs.sumOf { it.distanceInMeters.getAverage(isMetric, 1) }
        val value = String.format("%.2f", distance).addDistanceUnits(isMetric)

        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_distance)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgDistance(runs: List<RunEntity>): Progress {
        val count = runs.size
        val title: String = resources.getString(R.string.avg_distance)

        val distance: Double = runs.sumOf { it.distanceInMeters.getAverage(isMetric, count) }

        val value = String.format("%.2f", distance).addDistanceUnits(isMetric)

        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_distance)!!
        return Progress(title, value, icon)
    }

    private fun progressTotalDuration(runs: List<RunEntity>): Progress {
        val title: String = resources.getString(R.string.total_duration)
        val value: String = TrackingUtility.getFormattedStopWatchTime(runs.sumOf { it.timeInMillis })
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_duration)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgDuration(runs: List<RunEntity>): Progress {
        val count = runs.size

        val title: String = resources.getString(R.string.avg_duration)
        val value: String = TrackingUtility.getFormattedStopWatchTime(runs.sumOf { it.timeInMillis } / count)
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_time)!!

        return Progress(title, value, icon)
    }

    private fun progressTotalCalories(runs: List<RunEntity>): Progress {
        val title: String = resources.getString(R.string.total_calories)
        val value: String = runs.sumOf { it.calories }.toString().addCalories()
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_calories)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgCalories(runs: List<RunEntity>): Progress {
        val count = runs.size

        val title: String = resources.getString(R.string.avg_calories)
        val value: String = (runs.sumOf { it.calories } / count).toString().addCalories()
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_calories)!!

        return Progress(title, value, icon)
    }
}