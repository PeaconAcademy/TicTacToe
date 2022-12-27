package com.lastefania.tictactoe.ui.entry

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lastefania.tictactoe.R
import com.lastefania.tictactoe.analytics.Analytics
import com.lastefania.tictactoe.logic.Symbol
import com.lastefania.tictactoe.ui.main.SharedViewModel

class EntryFragment : Fragment(R.layout.fragment_entry) {

    private val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // TODO report start/entry screen view
        Analytics.recordScreenView(EntryFragment.tag)

        val xType = view.findViewById<ImageView>(R.id.typeX)
        val yType = view.findViewById<ImageView>(R.id.typeO)
        val difficultySlider = view.findViewById<Slider>(R.id.slider)
        val shouldAlwaysStartFirst = view.findViewById<SwitchMaterial>(R.id.start_first)
        val startGameButton = view.findViewById<MaterialButton>(R.id.start_game)

        xType.setOnClickListener { viewModel.onTileChosen(Symbol.X) }
        yType.setOnClickListener { viewModel.onTileChosen(Symbol.O) }

        difficultySlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            viewModel.setAiType(value)
        })

        shouldAlwaysStartFirst.setOnCheckedChangeListener { _, isChecked ->
            viewModel.shouldAlwaysStartFirst = isChecked
        }

        viewModel.chosenHumanSymbol.observe(viewLifecycleOwner) {
            if (it == Symbol.X) {
                xType.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.highlight)
                yType.background = null
            } else {
                yType.background =
                    ContextCompat.getDrawable(requireActivity(), R.drawable.highlight)
                xType.background = null
            }
        }

        startGameButton.setOnClickListener {
            findNavController().navigate(R.id.action_entryFragment_to_gameFragment)
            viewModel.play()
        }
    }

    companion object {
        private const val tag = "EntryFragment"
    }
}
