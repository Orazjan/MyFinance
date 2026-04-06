package com.atnzvdev.data.repository.templates

import com.atnzvdev.data.local.dao.TemplateDao
import com.atnzvdev.data.local.entity.TemplateEntity
import com.atnzvdev.data.local.entity.toDomain
import com.atnzvdev.data.local.entity.toEntity
import com.atnzvdev.domain.model.Template
import com.atnzvdev.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TemplateRepositoryImpl @Inject constructor(
    private val dao: TemplateDao
) : TemplateRepository {
    override suspend fun deleteAllTemplates() {
        return dao.deleteAllTemplates()
    }

    override suspend fun deleteTemplateById(id: Long) {
        return dao.deleteTemplateById(id)
    }

    override suspend fun insertTemplate(template: Template) {
        return dao.insertTemplate(template.toEntity())
    }

    override suspend fun updateTemplate(template: Template) {
        return dao.updateTemplate((template.toEntity()))
    }

    override fun getAllTemplates(): Flow<List<Template>> {
        return dao.getAllTemplates().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}