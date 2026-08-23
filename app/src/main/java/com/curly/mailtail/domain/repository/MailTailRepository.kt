package com.curly.mailtail.domain.repository

import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import com.curly.mailtail.data.local.entity.CommentEntity
import com.curly.mailtail.data.local.entity.ReactionEntity

interface MailTailRepository {
    fun getAllNotebooks(): Flow<List<NotebookEntity>>
    suspend fun createNotebook(notebook: NotebookEntity)
    suspend fun updateNotebookTitle(notebookId: String, newTitle: String) // Редактирование блокнота

    fun getPostsForNotebook(notebookId: String): Flow<List<PostEntity>>
    suspend fun createPost(post: PostEntity)
    suspend fun updatePost(post: PostEntity) // Редактирование поста

    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>
    suspend fun addComment(comment: CommentEntity)

    fun getReactionsForPost(postId: String): Flow<List<ReactionEntity>>
    suspend fun addReaction(reaction: ReactionEntity)
    suspend fun removeReaction(postId: String, authorName: String, emoji: String)
}