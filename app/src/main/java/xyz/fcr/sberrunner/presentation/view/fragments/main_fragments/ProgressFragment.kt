package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.data.model.Run
import xyz.fcr.sberrunner.databinding.FragmentProgressBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.model.Progress
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapter.ProgressRecyclerAdapter
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.ProgressViewModel
import xyz.fcr.sberrunner.utils.Constants.ROWS_IN_RECYCLER
import xyz.fcr.sberrunner.utils.Constants.UNIT_RATIO
import xyz.fcr.sberrunner.utils.TrackingUtility
import javax.inject.Inject
import kotlin.math.roundToInt

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
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.listOfRuns.observe(viewLifecycleOwner) { runs: List<Run> ->
            if (runs.isNotEmpty()) {
                initRecycler(runs)
            }
        }

        viewModel.unitsLiveData.observe(viewLifecycleOwner) { units: Boolean ->
            isMetric = units
        }
    }

    private fun initRecycler(runs: List<Run>) {

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

    private fun progressTotalRuns(runs: List<Run>): Progress {
        val title: String = resources.getString(R.string.total_runs)
        val value: String = runs.size.toString()
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_runs)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgSpeed(runs: List<Run>): Progress {
        val count = runs.size
        val title: String = resources.getString(R.string.avg_speed)

        val sum = if (isMetric) {
            runs.sumOf { it.avgSpeedInKMH.toInt() }
        } else {
            runs.sumOf { (it.avgSpeedInKMH * UNIT_RATIO).toInt() }
        }

        val value = (sum / count).toString().addSpeedUnits(isMetric)

        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_speed)!!

        return Progress(title, value, icon)
    }

    private fun progressTotalDistance(runs: List<Run>): Progress {
        val title: String = resources.getString(R.string.total_distance)

        val totalDistance = if (isMetric) {
            runs.sumOf { it.distanceInMeters } / 1000
        } else {
            (runs.sumOf { it.distanceInMeters } * UNIT_RATIO).roundToInt() / 1000
        }

        val value: String = totalDistance.toString().addDistanceUnits(isMetric)
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_distance)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgDistance(runs: List<Run>): Progress {
        val count = runs.size
        val title: String = resources.getString(R.string.avg_distance)

        val distance: Double = runs.sumOf { it.distanceInMeters.getAverage(isMetric, count) }

        val value = String.format("%.2f", distance).addDistanceUnits(isMetric)

        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_distance)!!
        return Progress(title, value, icon)
    }

    private fun progressTotalDuration(runs: List<Run>): Progress {
        val title: String = resources.getString(R.string.total_duraion)
        val value: String = TrackingUtility.getFormattedStopWatchTime(runs.sumOf { it.timeInMillis })
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_duration)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgDuration(runs: List<Run>): Progress {
        val count = runs.size

        val title: String = resources.getString(R.string.avg_duration)
        val value: String = TrackingUtility.getFormattedStopWatchTime(runs.sumOf { it.timeInMillis } / count)
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_time)!!

        return Progress(title, value, icon)
    }

    private fun progressTotalCalories(runs: List<Run>): Progress {
        val title: String = resources.getString(R.string.total_calories)
        val value: String = runs.sumOf { it.calories }.toString().addCalories()
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_total_calories)!!

        return Progress(title, value, icon)
    }

    private fun progressAvgCalories(runs: List<Run>): Progress {
        val count = runs.size

        val title: String = resources.getString(R.string.avg_calories)
        val value: String = (runs.sumOf { it.calories } / count).toString().addCalories()
        val icon: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_detailed_calories)!!

        return Progress(title, value, icon)
    }
}

private fun Int.getAverage(isMetric: Boolean, count: Int): Double {
    return if (isMetric) {
        ((((this / 1000f) / count) * 100f).roundToInt() / 100f).toDouble()
    } else {
        ((((this / 1000f / count * UNIT_RATIO) * 100f)).roundToInt() / 100f).toDouble()
    }
}

private fun String.addDistanceUnits(isMetric: Boolean): String {
    return if (isMetric) {
        this.plus(App.appComponent.context().resources.getString(R.string.km_addition))
    } else {
        this.plus(App.appComponent.context().resources.getString(R.string.miles_addition))
    }
}

private fun String.addSpeedUnits(isMetric: Boolean): String {
    return if (isMetric) {
        this.plus(App.appComponent.context().resources.getString(R.string.km_h_addition))
    } else {
        this.plus(App.appComponent.context().resources.getString(R.string.mph_addition))
    }
}

private fun String.addCalories(): String {
    return this.plus(App.appComponent.context().resources.getString(R.string.kcal_addition))
}