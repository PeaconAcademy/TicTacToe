package com.lastefania.tictactoe.logic.ai

import com.lastefania.tictactoe.logic.Board
import com.lastefania.tictactoe.logic.Symbol

// TODO known issue the algorithm will be absolutely opposite when
//  changing the symbols (when human takes o)!
class MiniMaxStrategy : IStrategy {
    override fun getMoveLocation(board: Board): Int {
        var location = move(board)
        while (!board.isValidAddition(location)) {
            location = (0..8).random()
        }
        return location
    }

    private fun move(board: Board): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = Int.MIN_VALUE
        for (i in (0..8)) {
            if (board[i] == Symbol.E) {
                board[i] = Symbol.O
                val score = minimax(board, 0, false)
                board[i] = Symbol.E
                if (score > bestScore) {
                    bestScore = score
                    bestMove = i
                }
            }
        }
        return bestMove
    }

    private fun minimax(board: Board, depth: Int, isMaximizingPlayer: Boolean): Int {
        board.haveWinner()?.let {
            return it.weight
        }

        if (isMaximizingPlayer) {
            var bestScore = 0
            for (i in (0..8)) {
                if (board[i] == Symbol.E) {
                    board[i] = Symbol.O
                    val score = minimax(board, depth + 1, false)
                    board[i] = Symbol.E
                    if (score > 0) {
                        bestScore += score
                    }
                }
            }
            return bestScore
        } else {
            var bestScore = 0
            for (i in (0..8)) {
                if (board[i] == Symbol.E) {
                    board[i] = Symbol.X
                    val score = minimax(board, depth + 1, true)
                    board[i] = Symbol.E
                    if (score < 0) {
                        bestScore += score
                    }
                }
            }
            return bestScore
        }
    }
}