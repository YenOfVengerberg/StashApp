package com.mobile.stashapp.data.model

sealed interface UiConfig {
    data class CustomFilter(
        val mode: Modes,
        val sortBy: SortBy
    ): UiConfig

    data class SavedFilter(
        val id: String
    ): UiConfig
}


