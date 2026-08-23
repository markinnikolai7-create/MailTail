package com.curly.mailtail.data.repository

import com.curly.mailtail.data.local.dao.NotebookDao
import com.curly.mailtail.data.local.dao.PostDao
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MailTailRepositoryImpl @Inject constructor(
    private val notebookDao: NotebookDao,
    private val postDao: PostDao
) : MailTailRepository {

    override fun getAllNotebooks(): Flow<List<NotebookEntity>> {
        // Пока просто отдаем локальные данные.
        // Позже добавим сюда логику проверки новых данных на сервере.
        return notebookDao.getAllNotebooks()
    }

    override suspend fun createNotebook(notebook: NotebookEntity) {
        notebookDao.insertNotebook(notebook)
    }

    override fun getPostsForNotebook(notebookId: String): Flow<List<PostEntity>> {
        return postDao.getPostsByNotebookId(notebookId)
    }

    override suspend fun createPost(post: PostEntity) {
        // Сохраняем пост локально.
        // Благодаря WorkManager, отправку на сервер мы настроим отдельно.
        postDao.insertPost(post)
    }
}