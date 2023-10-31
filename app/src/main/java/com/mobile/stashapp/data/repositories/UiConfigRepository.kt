package com.mobile.stashapp.data.repositories

import com.mobile.stashapp.data.model.SortBy
import com.mobile.stashapp.data.model.SortDirection
import com.mobile.stashapp.data.model.SortType
import com.mobile.stashapp.data.model.UiConfig
import com.mobile.stashapp.data.model.Modes
import com.mobile.stashapp.network.NetworkService
import com.mobile.stashapp.network.Queries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class UiConfigRepository @Inject constructor(
    private val networkService: NetworkService
) {

    suspend fun getUiConfiguration(): List<UiConfig> {
        return withContext(Dispatchers.IO) {
            try {
                val response = networkService.getApiService()
                    .getData(Queries.queryRequestBody { getUIConfig() })
                    .execute()

                if (response.isSuccessful) {
                    val frontPageContent = response.body()?.string()?.let {
                        JSONObject(it).getJSONObject("data")
                            .getJSONObject("configuration")
                            .getJSONObject("ui")
                            .getJSONArray("frontPageContent")
                    } ?: return@withContext listOf()

                    val uiConfigs = ArrayList<UiConfig>()
                    for (i in 0 until frontPageContent.length()) {
                        val configJson = frontPageContent.getJSONObject(i)
                        when (configJson.getString("__typename")) {
                            "CustomFilter" -> {
                                val uiMode = when (configJson.getString("mode")) {
                                    "SCENES" -> Modes.SCENES
                                    "PERFORMERS" -> Modes.PERFORMERS
                                    else -> Modes.PERFORMERS
                                }
                                val sortBy = when(configJson.getString("sortBy")) {
                                    "created_at" -> SortType.ADDED_TIME
                                    "date" -> SortType.DATE
                                    else -> SortType.DATE
                                }
                                val sortDirection = when(configJson.getString("direction")) {
                                    "DESC" -> SortDirection.DESC
                                    else -> SortDirection.ASC
                                }
                                UiConfig.CustomFilter(
                                    uiMode,
                                    SortBy(sortBy, sortDirection)
                                ).also {
                                    uiConfigs.add(it)
                                }
                            }
                            else -> {
                                UiConfig.SavedFilter(configJson.getString("savedFilterId")).also {
                                    uiConfigs.add(it)
                                }
                            }
                        }

                    }
                    return@withContext uiConfigs
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext listOf()
        }
    }

}