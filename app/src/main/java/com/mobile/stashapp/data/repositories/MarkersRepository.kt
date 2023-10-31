package com.mobile.stashapp.data.repositories

import com.mobile.stashapp.data.model.Marker
import com.mobile.stashapp.data.model.MarkerTag
import com.mobile.stashapp.network.NetworkService
import com.mobile.stashapp.network.Queries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.util.Random
import javax.inject.Inject
import kotlin.random.nextInt

class MarkersRepository @Inject constructor(
    private val networkService: NetworkService
) {

    suspend fun getRandomMarkers(page: Int, randomKey: Int): List<Marker> {
        return withContext(Dispatchers.IO) {
            try {
                val response = networkService.getApiService()
                    .getData(Queries.queryRequestBody { randomMarkers(page, randomKey) })
                    .execute()

                if (response.isSuccessful) {
                    val sceneMarkersJson = response.body()?.string()?.let {
                        JSONObject(it).getJSONObject("data").getJSONObject("findSceneMarkers")
                            .getJSONArray("scene_markers")
                    } ?: return@withContext listOf()

                    val markers = ArrayList<Marker>()
                    for (j in 0 until sceneMarkersJson.length()) {
                        val markerJson = sceneMarkersJson.getJSONObject(j)


                        val tags = ArrayList<MarkerTag>()
                        val markerTags = markerJson.getJSONArray("tags")
                        for (i in 0 until markerTags.length()) {
                            val tagJson = markerTags.getJSONObject(i)
                            val tag = MarkerTag(
                                name = tagJson.getString("name"),
                                id = tagJson.getString("id")
                            )
                            tags.add(tag)
                        }
                        val marker = Marker(
                            id = markerJson.getString("id"),
                            title = markerJson.getString("title"),
                            primaryTag = MarkerTag(
                                id = markerJson.getJSONObject("primary_tag").getString("id"),
                                name = markerJson.getJSONObject("primary_tag").getString("name")
                            ),
                            tags = tags,
                            sceneName = markerJson.getJSONObject("scene").getString("title"),
                            sceneId = markerJson.getJSONObject("scene").getString("id"),
                            stream = markerJson.getString("stream"),
                            screenshot = markerJson.getString("screenshot")
                        )
                        markers.add(marker)
                    }

                    return@withContext markers

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return@withContext listOf()
        }
    }

}