package com.curly.mailtail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.curly.mailtail.data.local.entity.NotebookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: NotebookEntity)

    @Query("SELECT * FROM notebooks")
    fun getAllNotebooks(): Flow<List<NotebookEntity>>

    // Обновление названия блокнота
    @Query("UPDATE notebooks SET title = :newTitle WHERE id = :notebookId")
    suspend fun updateNotebookTitle(notebookId: String, newTitle: String)

    @Query("DELETE FROM notebooks WHERE id = :notebookId")
    suspend fun deleteNotebookById(notebookId: String)
}