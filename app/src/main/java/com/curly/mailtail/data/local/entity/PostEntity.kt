package com.curly.mailtail.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val notebookId: String,
    val authorName: String,
    val title: String = "",
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateMillis: Long = System.currentTimeMillis(),
    val imageUris: String? = null,
    val isDraft: Boolean = false
)