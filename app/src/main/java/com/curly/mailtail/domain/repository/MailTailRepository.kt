package com.curly.mailtail.domain.repository

import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import com.curly.mailtail.data.local.entity.CommentEntity

interface MailTailRepository {
    fun getAllNotebooks(): Flow<List<NotebookEntity>>
    suspend fun createNotebook(notebook: NotebookEntity)
    suspend fun updateNotebookTitle(notebookId: String, newTitle: String) // Редактирование блокнота

    fun getPostsForNotebook(notebookId: String): Flow<List<PostEntity>>
    suspend fun createPost(post: PostEntity)
    suspend fun updatePost(post: PostEntity) // Редактирование поста

    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>
    suspend fun addComment(comment: CommentEntity)

    suspend fun deleteNotebookById(notebookId: String)

    suspend fun updateNotebook(notebook: NotebookEntity)

    fun getNotebookById(notebookId: String): kotlinx.coroutines.flow.Flow<NotebookEntity?>
    fun getPostsByNotebookId(notebookId: String): kotlinx.coroutines.flow.Flow<List<PostEntity>>
}