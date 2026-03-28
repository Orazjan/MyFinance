package com.example.myfinance.domain.repository

import com.example.myfinance.data.local.entity.TemplateEntity
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    suspend fun deleteAllTemplates()
    suspend fun deleteTemplateById(id: Long)
    suspend fun insertTemplate(template: TemplateEntity)
    suspend fun updateTemplate(template: TemplateEntity)
    fun getAllTemplates(): Flow<List<TemplateEntity>>
}