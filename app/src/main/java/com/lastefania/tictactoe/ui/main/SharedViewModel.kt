package com.lastefania.tictactoe.ui.main

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastefania.tictactoe.R
import com.lastefania.tictactoe.analytics.Analytics
import com.lastefania.tictactoe.analytics.Event
import com.lastefania.tictactoe.analytics.UserProperty
import com.lastefania.tictactoe.logic.*
import com.lastefania.tictactoe.logic.ai.StrategyType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SharedViewModel : ViewModel(), IBoardPainter {
    val chosenHumanSymbol = MutableLiveData(Symbol.X)
    val endStateReached = MutableLiveData<EndState>()
    val stateLiveData = MutableLiveData<String>()
    val boardDrawer = MutableLiveData<Board>()
    val hasSpell = MutableLiveData(false)

    private var humanSymbol = Symbol.X
    private var aiSymbol = Symbol.O
    private var aiType = StrategyType.NoAi
    var shouldAlwaysStartFirst = false

    private lateinit var gameState: GameState
    private var isInitialized: Boolean = false

    override fun drawBoard(board: Board) {
        boardDrawer.value = board
    }

    fun onTileChosen(symbol: Symbol) {
        chosenHumanSymbol.value = symbol
        humanSymbol = symbol
        aiSymbol = symbol.getOpponent()

        Analytics.setUserProperty(UserProperty.USER_SYMBOL.type, symbol.name)
    }

    fun setAiType(value: Float) {
        aiType = when (value.toInt()) {
            1 -> StrategyType.NoAi
            2 -> StrategyType.BlockingAi
            3 -> StrategyType.MiniMaxAi
            else -> {
                Log.e(tag, "Unexpected entry")
                throw UnsupportedOperationException()
            }
        }
    }

    fun play() {
        if (isInitialized) {
            val sym = gameState.haveWinner()
            if (sym == humanSymbol) {
                Log.d(tag, "proceed to next level")
                nextLevel()
                shouldTryToStart()

                // 2- tapping 'Next' after winning a round
                val properties = bundleOf(
                    "level" to gameState.settings.level + 1,
                    "type" to "next_level",
                    "strategy" to gameState.settings.aiStrategyType.type,
                )
                Analytics.sendEvent(Event.ROUND_STARTED.eventName, properties)

                return
            }

            // 3- tapping 'Try again' after loosing a round
            val properties = bundleOf(
                "level" to gameState.settings.level,
                "type" to "retry_level",
                "strategy" to gameState.settings.aiStrategyType.type,
            )
            Analytics.sendEvent(Event.ROUND_STARTED.eventName, properties)

            Log.d(tag, "retrying same level")
            startGame(gameState.settings.level)
            shouldTryToStart()
            return
        }

        startGame(level = 0)
        isInitialized = true
        Log.d(tag, "starting NEW game")
        shouldTryToStart()

        // TODO report game started, 'start' button
        val properties = bundleOf(
            "level" to 0,
            "type" to "new_game",
            "strategy" to gameState.settings.aiStrategyType.type,
        )
        Analytics.sendEvent(Event.ROUND_STARTED.eventName, properties)
    }

    fun playerFirstPlayerDrawable(): Int {
        return if (humanSymbol == Symbol.X) {
            R.drawable.x1
        } else {
            R.drawable.o1
        }
    }

    fun playerSecondPlayerDrawable(): Int {
        return if (aiSymbol == Symbol.X) {
            R.drawable.x1
        } else {
            R.drawable.o1
        }
    }

    private fun startGame(level: Int) {
        val settings = Settings(
            humanSymbol, aiSymbol, aiType, shouldAlwaysStartFirst, level)
        val board = Board(humanSymbol = humanSymbol, aiSymbol = aiSymbol)
        gameState = GameState(board, settings, this)
        gameState.initState()
    }

    private fun nextLevel() = gameState.nextLevel()

    private fun shouldTryToStart() {
        val shouldStart = Random().nextBoolean()
        if (shouldStart && !shouldAlwaysStartFirst) {
            Log.d(tag, "starting with Ai")
            playAi()
        } else {
            stateLiveData.value = "Your Turn!"
        }
    }

    fun resetGame() {
        endStateReached.value = EndState.NoFinalState
        gameState.reset()
        isInitialized = false
    }

    fun humanInsertsAt(blockNumber: Int) {
        if (checkWinnerStatus() || checkBoardStatus()) {
            return
        }

        // check if valid play
        if (!gameState.isValidAddition(blockNumber)) {
            Log.e(tag, "Invalid addition")
            return
        }

        // place the symbol
        gameState.placeHumanAt(blockNumber)
        Log.d(tag, "inserted successfully at $blockNumber")

        if (checkWinnerStatus() || checkBoardStatus()) {
            return
        }

        // play AI
        playAi()
    }

    private fun checkWinnerStatus(): Boolean {
        gameState.haveWinner()?.let { winner ->
            endStateReached.value = gameState.getEndStateFromWinnerSymbol(winner)
            when (endStateReached.value) {
                EndState.HumanWinner -> {
                    Log.d(tag, "you won !!!")
                }
                EndState.AiWinner -> {
                    Log.d(tag, "ai won !!!")
                }
                else -> {}
            }
            return true
        }

        stateLiveData.value = "Your Turn!"
        return false
    }

    /**
     * Return true if the game is blocked
     */
    private fun checkBoardStatus(): Boolean {
        if (gameState.gameBlocked()) {
            endStateReached.value = EndState.DrawReached
            Log.d(tag, "Game draw, please restart!")
            return true
        }

        return false
    }

    private fun playAi() {
        stateLiveData.value = "Ai Turn!"
        viewModelScope.launch(Dispatchers.Default) { // to simulate a process running
            delay(100)
            val location = gameState.getAiPosition()
            withContext(Dispatchers.Main) {
                gameState.placeAiAt(location)

                checkWinnerStatus()
                checkBoardStatus()
            }
        }
    }

    fun reportGameEnd(reason: String) {
        val properties = bundleOf(
            "level" to gameState.settings.level,
            "strategy" to gameState.settings.aiStrategyType.type,
            "reason" to reason)
        Analytics.sendEvent(Event.ROUND_ENDED.eventName, properties)
    }

    fun buySpell() {
        hasSpell.value = true

        val properties = bundleOf(
            "level" to gameState.settings.level,
            "strategy" to gameState.settings.aiStrategyType.type)
        Analytics.sendEvent(Event.BUY_SPELL.eventName, properties)
    }

    fun useSpell() {
        hasSpell.value = false
        val suggestedLocation = gameState.suggestLocation()
        humanInsertsAt(suggestedLocation)

        val properties = bundleOf(
            "level" to gameState.settings.level,
            "strategy" to gameState.settings.aiStrategyType.type)
        Analytics.sendEvent(Event.USE_SPELL.eventName, properties)
    }

    companion object {
        private const val tag = "SharedViewModel"
    }
}
