package com.curly.mailtail.data.repository

import com.curly.mailtail.data.local.dao.NotebookDao
import com.curly.mailtail.data.local.dao.PostDao
import com.curly.mailtail.data.local.entity.CommentEntity
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.data.local.entity.ReactionEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MailTailRepositoryImpl @Inject constructor(
    private val notebookDao: NotebookDao,
    private val postDao: PostDao
) : MailTailRepository {

    // --- Блокноты ---
    override fun getAllNotebooks(): Flow<List<NotebookEntity>> {
        return notebookDao.getAllNotebooks()
    }

    override suspend fun createNotebook(notebook: NotebookEntity) {
        notebookDao.insertNotebook(notebook)
    }

    override suspend fun updateNotebookTitle(notebookId: String, newTitle: String) {
        notebookDao.updateNotebookTitle(notebookId, newTitle)
    }

    // --- Посты ---
    override fun getPostsForNotebook(notebookId: String): Flow<List<PostEntity>> {
        return postDao.getPostsByNotebookId(notebookId)
    }

    override suspend fun createPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    override suspend fun updatePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    // --- Комментарии ---
    override fun getCommentsForPost(postId: String): Flow<List<CommentEntity>> {
        return postDao.getCommentsForPost(postId)
    }

    override suspend fun addComment(comment: CommentEntity) {
        postDao.insertComment(comment)
    }

    // --- Реакции ---
    override fun getReactionsForPost(postId: String): Flow<List<ReactionEntity>> {
        return postDao.getReactionsForPost(postId)
    }

    override suspend fun addReaction(reaction: ReactionEntity) {
        postDao.insertReaction(reaction)
    }

    override suspend fun removeReaction(postId: String, authorName: String, emoji: String) {
        postDao.removeReaction(postId, authorName, emoji)
    }

    override suspend fun deleteNotebookById(notebookId: String) {
        notebookDao.deleteNotebookById(notebookId)
    }
}