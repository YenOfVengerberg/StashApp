package com.mobile.stashapp.data.repositories

import com.mobile.stashapp.data.model.MarkerTag
import com.mobile.stashapp.data.model.Performer
import com.mobile.stashapp.data.model.Scene
import com.mobile.stashapp.data.model.SceneMarker
import com.mobile.stashapp.data.model.SceneStream
import com.mobile.stashapp.data.model.SceneThumb
import com.mobile.stashapp.data.model.SortBy
import com.mobile.stashapp.data.model.Studio
import com.mobile.stashapp.network.NetworkService
import com.mobile.stashapp.network.Queries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class ScenesRepository @Inject constructor(
    private val networkService: NetworkService
) {


    suspend fun findSceneWithId(id: String): Scene? {
        return withContext(Dispatchers.IO) {
            try {
                val response = networkService.getApiService()
                    .getData(Queries.queryRequestBody { findSceneWithId(id) }).execute()
                if (response.isSuccessful) {
                    val sceneJson = response.body()?.string()?.let {
                        JSONObject(it).getJSONObject("data").getJSONObject("findScene")
                    } ?: return@withContext null

                    val performers = ArrayList<Performer>()
                    val performersList = sceneJson.getJSONArray("performers")
                    for (j in 0 until performersList.length()) {
                        val performer = Performer(
                            id = performersList.getJSONObject(j).getString("id"),
                            name = performersList.getJSONObject(j).getString("name")
                        )
                        performers.add(performer)
                    }
                    val studio = sceneJson.optJSONObject("studio")?.let {
                        Studio(id = it.getString("id"), name = it.getString("name"))
                    }

                    val tags = ArrayList<MarkerTag>()
                    val tagsList = sceneJson.getJSONArray("tags")
                    for (t in 0 until  tagsList.length()) {
                        val tag = MarkerTag(
                            id = tagsList.getJSONObject(t).getString("id"),
                            name = tagsList.getJSONObject(t).getString("name")
                        )
                        tags.add(tag)
                    }

                    val sceneStreams = ArrayList<SceneStream>()
                    val streamsList = sceneJson.getJSONArray("sceneStreams")
                    for (s in 0 until streamsList.length()) {
                        SceneStream(
                            url = streamsList.getJSONObject(s).getString("url"),
                            label = streamsList.getJSONObject(s).getString("label"),
                            type = streamsList.getJSONObject(s).getString("mime_type")
                        ).also {
                            sceneStreams.add(it)
                        }
                    }

                    val sceneMarkers = ArrayList<SceneMarker>()
                    val markersList = sceneJson.getJSONArray("scene_markers")
                    for (m in 0 until markersList.length()) {
                        SceneMarker(
                            id = markersList.getJSONObject(m).getString("id"),
                            title = markersList.getJSONObject(m).getString("title"),
                            seconds = markersList.getJSONObject(m).getInt("seconds"),
                            screenshot = markersList.getJSONObject(m).getString("screenshot")
                        ).also {
                            sceneMarkers.add(it)
                        }
                    }

                    return@withContext Scene(
                        id = sceneJson.getString("id"),
                        title = sceneJson.getString("title"),
                        details = sceneJson.getString("details"),
                        date = sceneJson.getString("date"),
                        createdAt = sceneJson.getString("created_at"),
                        updatedAt = sceneJson.getString("updated_at"),
                        resumeTime = sceneJson.getString("resume_time"),
                        url = sceneJson.getJSONArray("urls").getString(0),
                        performers = performers,
                        studio = studio,
                        tag = tags,
                        preview = sceneJson.getJSONObject("paths").getString("preview"),
                        screenshot = sceneJson.getJSONObject("paths").getString("screenshot"),
                        sprite = sceneJson.getJSONObject("paths").getString("sprite"),
                        streams = sceneStreams,
                        lastPlayedAt = sceneJson.getString("last_played_at"),
                        markers = sceneMarkers
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    suspend fun findScene(page: Int, sortBy: SortBy): List<SceneThumb> {
        return withContext(Dispatchers.IO) {
            try {
                val response = networkService.getApiService()
                    .getData(Queries.queryRequestBody { findScene(page, sortBy) })
                    .execute()
                if (response.isSuccessful) {
                    val scenesJSON = response.body()?.string()?.let {
                        JSONObject(it).getJSONObject("data").getJSONObject("findScenes")
                            .getJSONArray("scenes")
                    } ?: return@withContext listOf()

                    val scenes = ArrayList<SceneThumb>()
                    for (i in 0 until scenesJSON.length()) {
                        val sceneJson = scenesJSON.getJSONObject(i)
                        val performers = ArrayList<Performer>()
                        val performersList = sceneJson.getJSONArray("performers")
                        for (j in 0 until performersList.length()) {
                            val performer = Performer(
                                id = performersList.getJSONObject(j).getString("id"),
                                name = performersList.getJSONObject(j).getString("name")
                            )
                            performers.add(performer)
                        }
                        val studio = sceneJson.optJSONObject("studio")?.let {
                            Studio(id = it.getString("id"), name = it.getString("name"))
                        }
                        val scene = SceneThumb(
                            id = sceneJson.getString("id"),
                            title = sceneJson.optString("title", ""),
                            details = sceneJson.optString("details", ""),
                            performers = performers,
                            screenshot = sceneJson.optJSONObject("paths")?.optString("screenshot"),
                            studio = studio,
                            date = sceneJson.optString("date")
                        )
                        scenes.add(scene)
                    }
                    return@withContext scenes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext listOf()
        }
    }
}