package com.mobile.stashapp.data.model

data class Performer(
    val id: String,
    val name: String
): UiMode {
    override val mode: Modes
        get() = Modes.PERFORMERS
}