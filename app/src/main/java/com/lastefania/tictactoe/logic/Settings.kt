package com.lastefania.tictactoe.logic

import androidx.annotation.IntRange
import com.lastefania.tictactoe.logic.ai.StrategyType

data class Settings(
    var humanSymbol: Symbol,
    var aiSymbol: Symbol,
    var aiStrategyType: StrategyType,
    var shouldHumanStartFirst: Boolean,
    @IntRange(from = 0) var level: Int
)
