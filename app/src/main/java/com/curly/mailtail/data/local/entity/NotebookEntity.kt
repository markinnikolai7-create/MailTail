package com.curly.mailtail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebooks")
data class NotebookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val memberCount: Int,
    val creatorName: String = "Я" // Создатель блокнота
)