package com.curly.mailtail.domain.repository

import com.curly.mailtail.data.local.entity.CommentEntity
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.data.local.entity.PostWithCommentCount
import kotlinx.coroutines.flow.Flow

interface MailTailRepository {
    // --- Блокноты ---
    fun getAllNotebooks(): Flow<List<NotebookEntity>>
    suspend fun createNotebook(notebook: NotebookEntity)
    suspend fun updateNotebookTitle(notebookId: String, newTitle: String)
    suspend fun deleteNotebookById(notebookId: String)
    suspend fun updateNotebook(notebook: NotebookEntity)
    fun getNotebookById(notebookId: String): Flow<NotebookEntity?>

    // --- Посты ---
    fun getPostsByNotebookId(notebookId: String): Flow<List<PostWithCommentCount>> // <-- Наш новый метод
    suspend fun createPost(post: PostEntity)
    suspend fun updatePost(post: PostEntity)

    // --- Комментарии ---
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>
    suspend fun addComment(comment: CommentEntity)
}