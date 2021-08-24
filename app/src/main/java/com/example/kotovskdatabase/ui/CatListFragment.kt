package com.example.kotovskdatabase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotovskdatabase.databinding.CatListFragmentBinding
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

class CatListFragment : Fragment() {

    private val viewModel: CatListViewModel by viewModels()

    private var _binding: CatListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = CatListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskAdapter = CatAdapter()


        binding.apply {
            recyclerViewCats.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
//                !!!
                setHasFixedSize(true)
            }

            viewModel.cats.observe(viewLifecycleOwner) {
                taskAdapter.submitList(it)
            }

            binding.fabAddCat.setOnClickListener {
                viewModel.onAddNewCatClick()
            }
        }


//            https://habr.com/ru/company/otus/blog/564050/
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.catEvent.collect { event ->
                when(event) {
                    is CatListViewModel.CatEvent.NavigateToCatFragment -> {
                        val action = CatListFragmentDirections.actionCatListFragmentToCatFragment(
                            null
                        )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
