package com.lastefania.tictactoe.logic

import android.util.Log

interface IBoard {
    operator fun get(index: Int): Symbol
    operator fun set(index: Int, symbol: Symbol)
    fun initBoard()
}

/**
 * Provides information about the state of the game.
 */
interface IGameStateProvider {
    fun haveWinner(): Symbol?
    fun gameBlocked(): Boolean
    fun isValidAddition(newBlock: Int): Boolean
}

class Board(
    private val currentBoard: MutableList<Symbol> = mutableListOf(),
    private val humanSymbol: Symbol,
    private val aiSymbol: Symbol
) : IBoard, IGameStateProvider {

    override operator fun get(index: Int): Symbol {
        return currentBoard[index]
    }

    override fun set(index: Int, symbol: Symbol) {
        currentBoard[index] = symbol
    }

    override fun initBoard() {
        currentBoard.clear()
        for (i in (0..8)) {
            currentBoard.add(i, Symbol.E)
        }
    }

    override fun haveWinner(): Symbol? {
        val humanLocations = getPlayerLocation(humanSymbol)
        val aiLocations = getPlayerLocation(aiSymbol)

        for (comb in winningCombinations) {
            if (humanLocations.containsAll(comb)) {
                return humanSymbol
            }
        }

        for (comb in winningCombinations) {
            if (aiLocations.containsAll(comb)) {
                return aiSymbol
            }
        }

        return null
    }

    override fun gameBlocked(): Boolean = currentBoard.count { s -> s == Symbol.E } == 0

    override fun isValidAddition(newBlock: Int): Boolean {
        val playerOneLocations = getPlayerLocation(humanSymbol)
        val playerTwoLocations = getPlayerLocation(aiSymbol)

        val isValid =
            (newBlock in (0..8)) && (playerOneLocations.contains(newBlock) || playerTwoLocations.contains(
                newBlock
            )).not()
        Log.d(tag, "Valid move: $isValid")
        return isValid
    }

    fun insertSymbolAt(symbol: Symbol, index: Int) {
        currentBoard[index] = symbol
        Log.w(tag, "After insert $symbol, board: $currentBoard")
    }

    fun getFirstPlayerLocation(): List<Int> = getPlayerLocation(humanSymbol)

    fun getSecondPlayerLocation(): List<Int> = getPlayerLocation(aiSymbol)

    private fun getPlayerLocation(player: Symbol): List<Int> {
        val indices = mutableListOf<Int>()
        for ((index, sym) in currentBoard.withIndex()) {
            if (sym == player) {
                indices.add(index)
            }
        }
        return indices
    }

    companion object {
        private const val tag = "Board"

        val winningCombinations = listOf(
            // horizontal
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8), // vertical
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8), // diagonal
            listOf(0, 4, 8),
            listOf(2, 4, 6),
        )
    }
}