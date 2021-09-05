package xyz.fcr.sberrunner.presentation.view.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.R
import xyz.fcr.sberrunner.databinding.FragmentHomeBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.view.fragments.main.adapters.ItemClickListener
import xyz.fcr.sberrunner.presentation.view.fragments.main.adapters.RunRecyclerAdapter
import xyz.fcr.sberrunner.presentation.viewmodels.main.HomeViewModel
import xyz.fcr.sberrunner.utils.Constants.CURRENT_RUN_ID
import javax.inject.Inject

/**
 * Фрагмент вывода информации о всех забегах пользователя.
 */
class HomeFragment : Fragment(), ItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerAdapter: RunRecyclerAdapter

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    val viewModel: HomeViewModel by viewModels { factory }

    init {
        App.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel.syncWithCloud()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateListOfRuns()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncWithCloud()
        }

        setupRecyclerView()
        observeLiveData()
    }

    /**
     * Отслеживание изменений в livedata вьюмодели.
     */
    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner) { isVisible: Boolean ->
            showProgress(isVisible)
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) { error: String ->
            showError(error)
        }

        viewModel.listOfRunsLiveData.observe(viewLifecycleOwner) { runs ->
            if (runs.isNotEmpty()) {
                recyclerAdapter.submitList(runs)
                displayRecycler(true)
            } else {
                displayRecycler(false)
            }
        }
    }

    private fun displayRecycler(isVisible: Boolean) {
        binding.recyclerViewRuns.isVisible = isVisible
        binding.lottieEmptyList.isVisible = !isVisible
        binding.textViewWelcome.isVisible = !isVisible
    }

    private fun showError(text: String) {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun showProgress(isVisible: Boolean) {
        binding.swipeRefreshLayout.post {
            binding.swipeRefreshLayout.isRefreshing = isVisible
        }
    }

    private fun setupRecyclerView() {
        recyclerAdapter = RunRecyclerAdapter(this)
        binding.recyclerViewRuns.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(activity)
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
        }
    }

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition

            val run = recyclerAdapter.differ.currentList[position]
            viewModel.deleteRun(run)

            Snackbar.make(requireView(), getString(R.string.run_deleted), Snackbar.LENGTH_LONG).apply {
                setAction(getString(R.string.undo)) {
                    viewModel.addRun(run)
                }
                show()
            }
        }
    }

    override fun onItemClick(position: Int) {
        val run = recyclerAdapter.differ.currentList[position]

        if (run.id != null) {
            val bundle = Bundle().apply {
                putInt(CURRENT_RUN_ID, run.id!!)
            }

            val fragment = DetailedRunFragment()
            fragment.arguments = bundle

            parentFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.main_container, fragment)
                .addToBackStack(tag)
                .commit()
        }
    }
}