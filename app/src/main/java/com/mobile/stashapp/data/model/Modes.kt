package com.mobile.stashapp.data.model

interface UiMode {
    val mode: Modes
}

enum class Modes {
    SCENES,
    PERFORMERS,
    GALLERIES,
    IMAGES,
    MOVIES,
    STUDIOS
}