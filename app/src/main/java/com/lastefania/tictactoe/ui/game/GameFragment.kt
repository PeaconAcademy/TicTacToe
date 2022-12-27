package com.lastefania.tictactoe.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.lastefania.tictactoe.R
import com.lastefania.tictactoe.analytics.Analytics
import com.lastefania.tictactoe.logic.EndState
import com.lastefania.tictactoe.ui.main.SharedViewModel

class GameFragment : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels()
    private lateinit var gameView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // TODO report game screen view
        Analytics.recordScreenView(GameFragment.tag)

        gameView = inflater.inflate(R.layout.fragment_game, container, false)

        for (i in (0..8)) {
            gameView.findViewWithTag<ImageView>(i.toString())
                .setOnClickListener { playerInsertsAt(i) }
        }

        gameView.findViewById<MaterialButton>(R.id.play_again).also {
            it.setOnClickListener { view ->
                for (i in (0..8)) {
                    gameView.findViewWithTag<ImageView>(i.toString()).setImageDrawable(null)
                }
                viewModel.play()
                view.visibility = View.GONE
            }
        }

        gameView.findViewById<MaterialButton>(R.id.quit_game).apply {
            setOnClickListener {
                viewModel.reportGameEnd("explicit_quit")
            }
        }

        gameView.findViewById<MaterialButton>(R.id.buy_spell).apply {
            setOnClickListener {
                viewModel.buySpell()
            }
        }

        gameView.findViewById<MaterialButton>(R.id.use_spell).apply {
            setOnClickListener {
                viewModel.useSpell()
            }
        }

        viewModel.endStateReached.observe(viewLifecycleOwner) {
            if (it == EndState.NoFinalState) return@observe
            if (it == EndState.DrawReached) announceDraw() else announceWinner(it)
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            gameView.findViewById<TextView>(R.id.status).text = it
        }

        viewModel.boardDrawer.observe(viewLifecycleOwner) { board ->
            board.getFirstPlayerLocation().forEach { loc ->
                gameView.findViewWithTag<ImageView>(loc.toString()).setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), viewModel.playerFirstPlayerDrawable()))
            }
            board.getSecondPlayerLocation().forEach { loc ->
                gameView.findViewWithTag<ImageView>(loc.toString()).setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), viewModel.playerSecondPlayerDrawable()))
            }
        }

        viewModel.hasSpell.observe(viewLifecycleOwner) { hasSpell ->
            if (hasSpell) {
                gameView.findViewById<MaterialButton>(R.id.buy_spell).visibility = View.GONE
                gameView.findViewById<MaterialButton>(R.id.use_spell).visibility = View.VISIBLE
            } else {
                gameView.findViewById<MaterialButton>(R.id.buy_spell).visibility = View.VISIBLE
                gameView.findViewById<MaterialButton>(R.id.use_spell).visibility = View.GONE
            }
        }
        return gameView
    }

    override fun onDestroyView() {
        viewModel.reportGameEnd("user_left")
        viewModel.resetGame()
        gameView.findViewById<MaterialButton>(R.id.play_again).visibility = View.GONE
        super.onDestroyView()
    }

    private fun playerInsertsAt(blockNumber: Int) {
        viewModel.humanInsertsAt(blockNumber)
    }

    private fun announceWinner(endState: EndState) {
        val gameStatus = view?.findViewById<TextView>(R.id.status)
        gameStatus?.let {
            val button = view?.findViewById<MaterialButton>(R.id.play_again)?.apply {
                visibility = View.VISIBLE
            }

            when (endState) {
                EndState.HumanWinner -> {
                    it.text = getString(R.string.human_won)
                    button?.text = getString(R.string.next_level)
                    viewModel.reportGameEnd("human_won")
                }
                EndState.AiWinner -> {
                    it.text = getString(R.string.ai_won)
                    button?.text = getString(R.string.try_again)
                    viewModel.reportGameEnd("ai_won")
                }
                else -> {}
            }
        }
    }

    private fun announceDraw() {
        gameView.findViewById<TextView>(R.id.status).text = getString(R.string.announce_draw)
        view?.findViewById<MaterialButton>(R.id.play_again)?.apply {
            visibility = View.VISIBLE
            text = getString(R.string.try_again)
        }
        viewModel.reportGameEnd("draw")
    }

    companion object {
        private const val tag = "GameFragment"
    }
}