package com.mobile.stashapp.data.domain

import com.mobile.stashapp.data.repositories.ScenesRepository
import com.mobile.stashapp.data.repositories.UiConfigRepository
import javax.inject.Inject

class HomeViewUseCase @Inject constructor(
    private val uiConfigRepository: UiConfigRepository,
    private val scenesRepository: ScenesRepository
) {



}