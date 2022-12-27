package com.lastefania.tictactoe.logic.ai

import com.lastefania.tictactoe.logic.Board

enum class StrategyType(val type: String) {
    NoAi("no_ai"), BlockingAi("blocking_ai"), MiniMaxAi("min_max")
}

interface IStrategy {
    fun getMoveLocation(board: Board): Int
}