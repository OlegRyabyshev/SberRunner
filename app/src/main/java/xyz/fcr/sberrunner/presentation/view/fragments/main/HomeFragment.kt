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
import xyz.fcr.sberrunner.utils.Constants.START_SYNC
import xyz.fcr.sberrunner.utils.Constants.START_SYNC_KEY
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

        val action = arguments?.getString(START_SYNC_KEY)

        if (action == START_SYNC) {
            viewModel.initSync()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateListOfRuns()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.initSync()
            binding.swipeRefreshLayout.isRefreshing = false
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
            if (runs.any { !it.toDeleteFlag }) {
                recyclerAdapter.submitList(runs.filter { !it.toDeleteFlag })
                displayRecycler(true)
            } else {
                displayRecycler(false)
            }
        }
    }

    /**
     * Отображение RecyclerView в зависимости от полученного списка
     */
    private fun displayRecycler(isVisible: Boolean) {
        binding.recyclerViewRuns.isVisible = isVisible
        binding.lottieEmptyList.isVisible = !isVisible
        binding.textViewWelcome.isVisible = !isVisible
    }

    /**
     * Вывод ошибок из ViewModel
     */
    private fun showError(text: String) {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    /**
     * Визуализация загрузки
     */
    private fun showProgress(isActive: Boolean) {
        binding.homeLoadingLayout.isVisible = isActive
        displayRecycler(true)
    }

    /**
     * Инициализация RecyclerView
     */
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
            viewModel.setFlag(run.id!!, true)
            viewModel.updateListOfRuns()

            Snackbar.make(requireView(), getString(R.string.run_deleted), Snackbar.LENGTH_LONG).apply {
                setAction(getString(R.string.undo)) {
                    viewModel.setFlag(run.id!!, false)
                    viewModel.updateListOfRuns()
                }
                show()
            }
        }
    }

    /**
     * Отработка нажатия на элемент RecyclerView
     */
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
                .add(R.id.main_container, fragment)
                .addToBackStack(tag)
                .commit()
        }
    }
}