package com.example.kotovskdatabase.ui.firstscreen

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotovskdatabase.R
import com.example.kotovskdatabase.databinding.CatListFragmentBinding
import com.example.kotovskdatabase.repositiry.entity.Cat
import com.example.kotovskdatabase.ui.factory
import com.example.kotovskdatabase.ui.firstscreen.adapter.CatAdapter
import com.example.kotovskdatabase.ui.firstscreen.adapter.SwipeHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class CatListFragment : Fragment(), CatAdapter.OnItemClickListener {

    private val viewModel: CatListViewModel by viewModels { factory() }

    private var _binding: CatListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CatListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val catAdapter = CatAdapter(this)


        binding.apply {
            recyclerViewCats.apply {
                adapter = catAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                SwipeHelper(viewModel::onTaskSwiped).attachToRecyclerView(recyclerViewCats)
            }

            viewModel.cats.observe(viewLifecycleOwner) {
                catAdapter.submitList(it)
            }

            binding.fabAddCat.setOnClickListener {
                viewModel.onAddNewCatClick()
            }
        }


//            https://habr.com/ru/company/otus/blog/564050/
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.catEvent.collect { event ->
                when (event) {
                    is CatListViewModel.CatEvent.NavigateToAddCatFragment -> {
                        val action = CatListFragmentDirections.actionCatListFragmentToCatFragment(
                            null
                        )
                        findNavController().navigate(action)
                    }

                    is CatListViewModel.CatEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Кот удалён", Snackbar.LENGTH_LONG)
                            .setAction("ОТМЕНИТЬ") {
                                viewModel.onUndoDeletedClick(event.cat)
                            }.show()
                    }

                    is CatListViewModel.CatEvent.NavigateToEditTaskScreen -> {
                        val action =
                            CatListFragmentDirections.actionCatListFragmentToCatFragment(event.cat)
                        findNavController().navigate(action)
                    }

                    is CatListViewModel.CatEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_list_cat, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_age -> {
                viewModel.onSortOrderSelected(SortOrder.BY_AGE)
                true
            }
            R.id.action_sort_by_date -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(cat: Cat) {
        Log.d("onItemClick", cat.toString())
        viewModel.onCatSelected(cat)
    }
}
