package com.atnzvdev.domain.repository

import com.atnzvdev.domain.model.Template
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    suspend fun deleteAllTemplates()
    suspend fun deleteTemplateById(id: Long)
    suspend fun insertTemplate(template: Template)
    suspend fun updateTemplate(template: Template)
    fun getAllTemplates(): Flow<List<Template>>
}