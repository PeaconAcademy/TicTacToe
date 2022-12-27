package com.lastefania.tictactoe.analytics

enum class Event(val eventName: String) {

    ROUND_STARTED("round_started"),

    ROUND_ENDED("round_ended"),

    BUY_SPELL("buy_spell"),

    USE_SPELL("use_spell")
}