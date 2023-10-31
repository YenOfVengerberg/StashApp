package com.mobile.stashapp.di

import android.content.Context
import com.mobile.stashapp.ServerPreference
import com.mobile.stashapp.network.NetworkService
import com.mobile.stashapp.network.RequestAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Provides
    fun provideServerPref(@ApplicationContext context: Context) = ServerPreference(context)

    @Provides
    fun provideInterceptor(serverPreference: ServerPreference) = RequestAuthInterceptor(serverPreference)

    @Provides
    fun provideNetworkService(
        serverPreference: ServerPreference,
        requestAuthInterceptor: RequestAuthInterceptor
    ) = NetworkService(serverPreference, requestAuthInterceptor)

}