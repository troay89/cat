package com.example.kotovskdatabase.ui.secondscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.kotovskdatabase.databinding.CatFragmentBinding
import com.example.kotovskdatabase.ui.factory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class CatFragment : Fragment() {

    private val viewModel: CatViewModel by viewModels ()

    private var _binding: CatFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.fabAddCat.setOnClickListener {
            viewModel.onSaveClick()
        }

        binding.ageEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                viewModel.onSaveClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.apply {
            nameEdit.setText(viewModel.catName)
            breedEdit.setText(viewModel.catBreed)
            ageEdit.setText(viewModel.catAge.toString())

            nameEdit.addTextChangedListener {
                viewModel.catName = it.toString()
            }


            breedEdit.addTextChangedListener {
                viewModel.catBreed = it.toString()
            }

            ageEdit.addTextChangedListener {
                viewModel.catAge = it.toString()
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditCatEvent.collect { event ->
                when (event) {
                    is CatViewModel.AddEditCatEvent.NavigateBackWithResult -> {

                        setFragmentResult(
                            "add_edit_request",
                            bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is CatViewModel.AddEditCatEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
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