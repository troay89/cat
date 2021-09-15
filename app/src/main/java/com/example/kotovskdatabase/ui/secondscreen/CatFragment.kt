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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.kotovskdatabase.R
import com.example.kotovskdatabase.databinding.CatFragmentBinding
import com.example.kotovskdatabase.ui.model.UICat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel


class CatFragment : Fragment() {

    private val viewModel: CatViewModel by viewModel()


    private var binding: CatFragmentBinding? = null

    private val args by navArgs<CatFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = CatFragmentBinding.inflate(inflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uiCat: UICat? = args.cat
        val apiBd = args.apiBd

        viewModel.chooseRepository(apiBd)

        updateUI(uiCat)

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

    private fun updateUI(uiCat: UICat?) {

        var catName = uiCat?.name ?: ""
        var catBreed = uiCat?.breed ?: ""
        var catAge = uiCat?.age ?: ""

        views {
            nameEdit.setText(catName)
            breedEdit.setText(catBreed)
            ageEdit.setText(catAge.toString())

            nameEdit.addTextChangedListener {
                catName = it.toString()
            }


            breedEdit.addTextChangedListener {
                catBreed = it.toString()
            }

            ageEdit.addTextChangedListener {
                catAge = it.toString()
            }


            fabAddCat.setOnClickListener {
                addingCat(uiCat, catName, catBreed , catAge as String)
            }

            ageEdit.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addingCat(uiCat, catName, catBreed , catAge as String)
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun addingCat (uiCat: UICat?, catName: String, catBreed: String , catAge: String) {
        if (uiCat == null) viewModel.onSaveClick(catName, catBreed , catAge)
        else viewModel.onUpdateClick(uiCat, catName, catBreed , catAge)
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