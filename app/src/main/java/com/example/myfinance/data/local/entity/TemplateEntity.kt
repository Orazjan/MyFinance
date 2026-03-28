package com.example.myfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val typeOfOperation: String,
    val amount: Double,
    val reminderTime: Long,
    val iconRes: String,
    val order: Int
)
