package com.mobile.stashapp.data.model

data class Scene(
    val id: String,
    val title: String,
    val details: String,
    val performers: List<Performer>,
    val studio: Studio?,
    val date: String,
    val createdAt: String,
    val updatedAt: String,
    val lastPlayedAt: String,
    val resumeTime: String,
    val url: String,
    val tag: List<MarkerTag>,
    val preview: String?,
    val sprite: String?,
    val screenshot: String?,
    val streams: List<SceneStream>,
    val markers: List<SceneMarker>
)

data class SceneStream(
    val url: String,
    val type: String,
    val label: String
)

data class SceneMarker(
    val id: String,
    val title: String,
    val seconds: Int,
    val screenshot: String
)