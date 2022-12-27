package com.lastefania.tictactoe.logic.ai

import com.lastefania.tictactoe.logic.Board

class SimpleStrategy : IStrategy {
    override fun getMoveLocation(board: Board): Int {
        var location = (0..8).random()
        while (!board.isValidAddition(location)) {
            location = (0..8).random()
        }
        return location
    }

    companion object {
        private const val tag = "SimpleStrategy"
    }
}