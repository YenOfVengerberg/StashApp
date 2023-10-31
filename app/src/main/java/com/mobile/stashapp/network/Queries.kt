package com.mobile.stashapp.network

import com.mobile.stashapp.data.model.SortBy
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object Queries {

    private val mimeType = "application/json".toMediaType()

    const val PER_PAGE = 20

    fun queryRequestBody(queryOperation: Queries.() -> String): RequestBody {
        val jsonObject = JSONObject().apply {
            put("query", queryOperation.invoke(this@Queries))
        }
        return jsonObject.toString().toRequestBody(mimeType)
    }

    fun systemStatus(): String {
        return "{ systemStatus { status }}"
    }

    fun getUIConfig(): String {
        return "{ configuration { ui }}"
    }

    fun getSavedFilter(filterId: String): String {
        return "{ findSavedFilter(id: $filterId) { filter, mode, name }}"
    }

    fun randomMarkers(page: Int, randomKey: Int): String {
        return "{ findSceneMarkers(filter: { page: $page, per_page: $PER_PAGE, sort: \"random_$randomKey\" }) { scene_markers { id, scene { title, id }, title, tags { name, id }, primary_tag { name, id } stream, screenshot }}}"
    }

    fun findScene(
        page: Int,
        sortBy: SortBy
    ): String {
        return "{ findScenes(filter:{ page: $page, per_page: $PER_PAGE, sort: \"${sortBy.type.type}\", direction: ${sortBy.direction.name} }) { scenes { title,id,details,date,performers {name,id},studio {name,id},paths { screenshot }}}}"
    }

    fun findSceneWithId(
        id: String
    ): String {
        return "{ findScene(id: \"${id}\") { id, title, details, date, created_at, updated_at, last_played_at, resume_time, play_count, urls, studio {id, name, url}, tags { id, name, image_path }, performers { id, name, gender, image_path } paths { screenshot, preview, sprite } sceneStreams { url, mime_type, label } scene_markers { id, title, seconds, screenshot }}}"
    }

}