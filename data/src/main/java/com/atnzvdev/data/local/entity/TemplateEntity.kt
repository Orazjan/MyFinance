package com.atnzvdev.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val typeOfOperation: String,
    val isIncome: Boolean,
    val amount: Double,
    val reminderTime: Long,
    val iconRes: Int,
    val order: Int
)
