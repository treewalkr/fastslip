package com.xdev.fastslip.screens.detail

import androidx.lifecycle.ViewModel
import com.xdev.fastslip.data.MuseumObject
import com.xdev.fastslip.data.MuseumRepository
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
