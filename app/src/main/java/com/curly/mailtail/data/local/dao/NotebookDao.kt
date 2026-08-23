package com.curly.mailtail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.curly.mailtail.data.local.entity.NotebookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    // Сохранить или обновить блокнот (если ID уже существует, заменим его)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: NotebookEntity)

    // Получить все блокноты в виде реактивного потока
    @Query("SELECT * FROM notebooks")
    fun getAllNotebooks(): Flow<List<NotebookEntity>>

    // Удалить блокнот по ID (посты удалятся автоматически благодаря CASCADE)
    @Query("DELETE FROM notebooks WHERE id = :notebookId")
    suspend fun deleteNotebookById(notebookId: String)
}