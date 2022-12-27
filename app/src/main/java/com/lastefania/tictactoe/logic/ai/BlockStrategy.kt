package com.lastefania.tictactoe.logic.ai

import com.lastefania.tictactoe.logic.Board

class BlockStrategy : IStrategy {
    override fun getMoveLocation(board: Board): Int {
        var location = blockPlayer(board)
        while (!board.isValidAddition(location)) {
            location = (0..8).random()
        }
        return location
    }

    private fun blockPlayer(board: Board): Int {
        val playerOneLocations = board.getFirstPlayerLocation()
        val playerTwoLocations = board.getSecondPlayerLocation()

        var maxAchievedCombinations = 0
        var indexToBlock = -1
        for (comb in Board.winningCombinations) { // this count signifies how likely the player is about to
            // complete this winning combination
            val count = getOccurrenceCount(
                comb, playerOneLocations
            ) // it doesn't matter blocking a winning-combination when the // combination is already full
            val isCombAlreadyFull =
                comb.minus(playerOneLocations.plus(playerTwoLocations)).isEmpty()
            if (count > maxAchievedCombinations && !isCombAlreadyFull) {
                maxAchievedCombinations = count
                indexToBlock = comb.minus(playerOneLocations).first()
            }
        }

        return indexToBlock
    }

    private fun getOccurrenceCount(items: List<Int>, container: List<Int>): Int {
        var count = 0
        for (item in items) {
            if (container.contains(item)) {
                count++
            }
        }
        return count
    }

    companion object {
        private const val tag = "BlockStrategy"
    }
}