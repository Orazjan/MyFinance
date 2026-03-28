package com.example.myfinance.data.repository.templates

import com.example.myfinance.data.local.dao.TemplateDao
import com.example.myfinance.data.local.entity.TemplateEntity
import com.example.myfinance.domain.repository.TemplateRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun insertTemplate(template: TemplateEntity) {
        return dao.insertTemplate(template)
    }

    override suspend fun updateTemplate(template: TemplateEntity) {
        return dao.updateTemplate((template))
    }

    override fun getAllTemplates(): Flow<List<TemplateEntity>> {
        return dao.getAllTemplates()
    }


}