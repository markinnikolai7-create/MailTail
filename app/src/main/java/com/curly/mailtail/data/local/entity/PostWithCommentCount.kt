package com.curly.mailtail.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class PostWithCommentCount(
    @Embedded val post: PostEntity,
    @ColumnInfo(name = "comment_count") val commentCount: Int
)