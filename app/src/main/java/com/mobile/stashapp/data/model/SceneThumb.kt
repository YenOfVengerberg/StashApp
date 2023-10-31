package com.mobile.stashapp.data.model

data class SceneThumb(
    val id: String,
    val title: String,
    val details: String,
    val performers: List<Performer>,
    val studio: Studio?,
    val date: String,
    val screenshot: String?
): UiMode {
    override val mode: Modes
        get() = Modes.SCENES
}