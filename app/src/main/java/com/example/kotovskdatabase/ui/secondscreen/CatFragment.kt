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
import com.example.kotovskdatabase.R
import com.example.kotovskdatabase.databinding.CatFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect


class CatFragment : Fragment() {

    private val viewModel: CatViewModel by viewModels()

    private var binding: CatFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = CatFragmentBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
        pressButton()
        textChangedListener()

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditCatEvent.collect { event ->
                eventImplementation(event)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun pressButton() {
        views {
            fabAddCat.setOnClickListener {
                viewModel.onSaveClick()
            }

            ageEdit.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    viewModel.onSaveClick()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun updateUI() {
        views {
            nameEdit.setText(viewModel.catName)
            breedEdit.setText(viewModel.catBreed)
            ageEdit.setText(viewModel.catAge.toString())
        }
    }

    private fun textChangedListener() {
        views {
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
    }

    private fun eventImplementation(event: CatViewModel.AddEditCatEvent) {
        when (event) {
            is CatViewModel.AddEditCatEvent.NavigateBackWithResult -> {

                setFragmentResult(
                    "add_edit_request",
                    bundleOf("add_edit_result" to event.result)
                )
                findNavController().popBackStack()
            }
            is CatViewModel.AddEditCatEvent.ShowInvalidInputMessage -> {
                Snackbar.make(requireView(), R.string.not_all_fields, Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun <T> views(block: CatFragmentBinding.() -> T): T? = binding?.block()
}