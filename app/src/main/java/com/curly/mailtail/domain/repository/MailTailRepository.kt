package com.curly.mailtail.domain.repository

import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

interface MailTailRepository {
    // Работа с блокнотами
    fun getAllNotebooks(): Flow<List<NotebookEntity>>
    suspend fun createNotebook(notebook: NotebookEntity)

    // Работа с постами
    fun getPostsForNotebook(notebookId: String): Flow<List<PostEntity>>
    suspend fun createPost(post: PostEntity)
}