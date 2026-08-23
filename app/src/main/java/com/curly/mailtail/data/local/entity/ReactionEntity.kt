package com.curly.mailtail.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reactions",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["id"],
            childColumns = ["post_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReactionEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "post_id", index = true) val postId: String,
    val authorName: String,
    val emoji: String // Например: "❤️", "👍", "🔥", "😂"
)