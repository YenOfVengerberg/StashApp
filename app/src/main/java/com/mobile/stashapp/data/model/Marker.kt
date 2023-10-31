package com.mobile.stashapp.data.model

data class Marker(
    val id: String,
    val title: String,
    val primaryTag: MarkerTag,
    val tags: List<MarkerTag>,
    val sceneName: String,
    val sceneId: String,
    val stream: String,
    val screenshot: String
)

data class MarkerTag(
    val id: String,
    val name: String
)