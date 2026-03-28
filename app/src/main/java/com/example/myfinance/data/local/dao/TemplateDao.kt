package com.example.myfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myfinance.data.local.entity.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity)

    @Query("DELETE from templates")
    suspend fun deleteAllTemplates()

    @Query("DELETE FROM templates WHERE id=:id")
    suspend fun deleteTemplateById(id: Long)

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Query("SELECT * FROM templates ORDER by `order` DESC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>


}