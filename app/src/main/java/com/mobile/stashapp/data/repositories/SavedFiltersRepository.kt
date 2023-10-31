package com.mobile.stashapp.data.repositories

import com.mobile.stashapp.network.NetworkService
import com.mobile.stashapp.network.Queries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

class SavedFiltersRepository @Inject constructor(
    private val networkService: NetworkService
) {

    suspend fun getFilterWithId(id: String) {
        return withContext(Dispatchers.IO) {
            try {
                val response = networkService.getApiService()
                    .getData(Queries.queryRequestBody { getSavedFilter(id) })
                    .execute()

                if (response.isSuccessful) {
                    val filterJson = response.body()?.string()?.let {
                        JSONObject(it).getJSONObject("data")
                            .getJSONObject("findSavedFilter")
                            .getString("filter")
                    } ?: return@withContext


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}