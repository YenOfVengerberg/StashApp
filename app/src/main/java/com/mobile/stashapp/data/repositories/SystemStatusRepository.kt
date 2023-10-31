package com.mobile.stashapp.data.repositories

import com.mobile.stashapp.data.model.SystemStatus
import com.mobile.stashapp.network.NetworkService
import com.mobile.stashapp.network.Queries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import javax.inject.Inject

class SystemStatusRepository @Inject constructor(
    private val networkService: NetworkService
) {

    suspend fun getSystemStatus(): SystemStatus {
        return withContext(Dispatchers.IO) {
            try {
                val response = networkService.getApiService()
                    .getData(Queries.queryRequestBody { systemStatus() })
                    .execute()
                if (response.isSuccessful) {
                    val systemStatus = response.body()?.string()?.let {
                        JSONObject(it)
                            .getJSONObject("data")
                            .getJSONObject("systemStatus")
                            .getString("status")
                    }

                    return@withContext when (systemStatus) {
                        "OK" -> {
                            SystemStatus.OK
                        }
                        "NEEDS_MIGRATION" -> {
                            SystemStatus.NEED_MIGRATION
                        }
                        "SETUP" -> {
                            SystemStatus.SETUP
                        }
                        else -> {
                            SystemStatus.ERROR
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext SystemStatus.ERROR
        }
    }

}