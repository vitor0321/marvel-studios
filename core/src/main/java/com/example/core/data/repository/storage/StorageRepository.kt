package com.example.core.data.repository.storage

import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    val sorting: Flow<String>

    suspend fun saveSorting(sorting: String)
}