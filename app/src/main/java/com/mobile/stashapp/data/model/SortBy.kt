package com.mobile.stashapp.data.model

data class SortBy(
    val type: SortType,
    val direction: SortDirection
)

enum class SortDirection {
    ASC,
    DESC
}

enum class SortType(val type: String) {
    DATE("date"),
    ADDED_TIME("created_at"),
    LAST_PLAYED("last_played_at")
}