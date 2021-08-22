package xyz.fcr.sberrunner.presentation.view.fragments.main_fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import es.dmoral.toasty.Toasty
import xyz.fcr.sberrunner.data.room.RunEntity
import xyz.fcr.sberrunner.databinding.FragmentHomeBinding
import xyz.fcr.sberrunner.presentation.App
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapter.ItemClickListener
import xyz.fcr.sberrunner.presentation.view.fragments.main_fragments.adapter.RunRecyclerAdapter
import xyz.fcr.sberrunner.presentation.viewmodels.main_viewmodels.HomeViewModel
import javax.inject.Inject

class HomeFragment : Fragment(), ItemClickListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RunRecyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private var listOfRuns : List<RunEntity>? = null

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewRuns

        viewModel.loadListOfRuns()
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.progressLiveData.observe(viewLifecycleOwner, { isVisible: Boolean -> showProgress(isVisible) })
        viewModel.successLiveData.observe(viewLifecycleOwner, { listOfRuns: List<RunEntity>? -> fillAdapter(listOfRuns) })
        viewModel.errorLiveData.observe(viewLifecycleOwner, { error: String -> showError(error) })
    }

    private fun fillAdapter(list: List<RunEntity>?) {
        listOfRuns = list

        if (listOfRuns != null) {
            recyclerView.adapter = RunRecyclerAdapter(listOfRuns!!, this)
            adapterIsVisible(true)
        } else {
            adapterIsVisible(false)
        }
    }

    private fun adapterIsVisible(isVisible: Boolean) {
        binding.recyclerViewRuns.isVisible = isVisible
        binding.imageViewWelcome.isVisible = !isVisible
        binding.textViewWelcome.isVisible = !isVisible
    }

    private fun showError(text: String) {
        Toasty.error(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun showProgress(isVisible: Boolean) {
        binding.progressCircularHome.isVisible = isVisible
    }

    override fun onItemClick(position: Int) {

    }
}