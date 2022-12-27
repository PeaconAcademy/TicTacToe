package com.lastefania.tictactoe.logic

enum class Symbol(val weight: Int) {
    E(0), // empty cell
    X(-10), O(10);

    fun getOpponent(): Symbol = when (this) {
        X -> {
            O
        }
        O -> {
            X
        }
        else -> {
            E
        }
    }
}