package com.lastefania.tictactoe.logic

import android.util.Log
import com.lastefania.tictactoe.logic.ai.*

interface IBoardPainter {
    fun drawBoard(board: Board)
}

class GameState(
    private val board: Board, var settings: Settings, var painter: IBoardPainter) :
    IGameStateProvider by board {

    fun initState() {
        board.initBoard()
        Log.d(tag, "initState(): $settings")
    }

    fun reset() {
        board.initBoard()
    }

    fun nextLevel() {
        settings.level++
        board.initBoard()
        Log.d(tag, "nextLevel(): $settings")
    }

    fun placeHumanAt(position: Int) {
        board.insertSymbolAt(settings.humanSymbol, position)
        painter.drawBoard(board)
    }

    fun placeAiAt(position: Int) {
        board.insertSymbolAt(settings.aiSymbol, position)
        painter.drawBoard(board)
    }

    fun getAiPosition(): Int {
        val strategy: IStrategy = when (settings.aiStrategyType) {
            StrategyType.NoAi -> { // random player
                SimpleStrategy()
            }
            StrategyType.BlockingAi -> { // just block the player
                BlockStrategy()
            }
            StrategyType.MiniMaxAi -> { // do your best with minimax
                MiniMaxStrategy()
            }
        }
        return strategy.getMoveLocation(board)
    }

    fun getEndStateFromWinnerSymbol(sym: Symbol): EndState {
        return if (settings.humanSymbol == sym) {
            EndState.HumanWinner
        } else {
            EndState.AiWinner
        }
    }

    fun suggestLocation(): Int {
        return SimpleStrategy().getMoveLocation(board)
    }

    companion object {
        private const val tag = "GameState"
    }
}