package com.curly.mailtail.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = NotebookEntity::class,
            parentColumns = ["id"],
            childColumns = ["notebook_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostEntity(
    @PrimaryKey val id: String,

    @ColumnInfo(name = "notebook_id", index = true) val notebookId: String,

    val authorName: String,
    val title: String?, // Заголовок поста
    val content: String,
    val dateMillis: Long,

    val imageUris: String?, // Строка с путями к картинкам через запятую

    val isDraft: Boolean = false,
    val isSyncing: Boolean = false
)