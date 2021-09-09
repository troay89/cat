package com.example.kotovskdatabase.ui.firstscreen

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotovskdatabase.R
import com.example.kotovskdatabase.databinding.CatListFragmentBinding
import com.example.kotovskdatabase.ui.factory
import com.example.kotovskdatabase.ui.firstscreen.adapter.CatAdapter
import com.example.kotovskdatabase.ui.firstscreen.adapter.SwipeHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect

class CatListFragment : Fragment() {

    private val viewModel: CatListViewModel by viewModels { factory() }

    private var binding: CatListFragmentBinding? = null

    private val catAdapter = CatAdapter { cat ->
        viewModel.onCatSelected(cat)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = CatListFragmentBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateRecyclerViewAndListener()
        eventImplementation()

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

            R.id.action_room -> {
                viewModel.choosingApiBD(ChooseBD.FROM_ROOM)
                Snackbar.make(requireView(), R.string.use_room, Snackbar.LENGTH_SHORT).show()
                true
            }

            R.id.action_cursor -> {
                viewModel.choosingApiBD(ChooseBD.FROM_CURSOR)
                Snackbar.make(requireView(), R.string.use_cursor, Snackbar.LENGTH_SHORT).show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun populateRecyclerViewAndListener() {
        views {
            recyclerViewCats.apply {
                adapter = catAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                SwipeHelper(viewModel::onTaskSwiped).attachToRecyclerView(recyclerViewCats)
            }

            viewModel.cats.observe(viewLifecycleOwner, {
                catAdapter.submitList(it)
            })

            fabAddCat.setOnClickListener {
                viewModel.onAddNewCatClick()
            }
        }
    }

    private fun eventImplementation() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.catEvent.collect { event ->
                setEvent(event)
            }
        }
    }

    private fun setEvent(event: CatListViewModel.CatEvent) {
        when (event) {
            is CatListViewModel.CatEvent.NavigateToAddCatFragment -> {
                val action = CatListFragmentDirections.actionCatListFragmentToCatFragment(
                    null,
                    getString(R.string.cat_new),
                    event.keyBd
                )
                findNavController().navigate(action)
            }

            is CatListViewModel.CatEvent.ShowUndoDeleteTaskMessage -> {
                Snackbar.make(requireView(), R.string.cat_delete, Snackbar.LENGTH_LONG)
                    .setAction(R.string.cancel) {
                        viewModel.onUndoDeletedClick(event.cat)
                    }.show()
            }

            is CatListViewModel.CatEvent.NavigateToEditTaskScreen -> {
                val action =
                    CatListFragmentDirections.actionCatListFragmentToCatFragment(
                        event.cat,
                        getString(R.string.editing),
                        event.keyBd
                    )
                findNavController().navigate(action)
            }

            is CatListViewModel.CatEvent.ShowTaskSavedConfirmationMessage -> {
                Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun <T> views(block: CatListFragmentBinding.() -> T): T? = binding?.block()
}
