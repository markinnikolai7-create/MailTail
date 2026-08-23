package com.curly.mailtail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.curly.mailtail.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // Сохранить новый пост
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    // Получить все посты конкретного блокнота, отсортированные по дате (по убыванию)
    @Query("SELECT * FROM posts WHERE notebook_id = :notebookId ORDER BY dateMillis DESC")
    fun getPostsByNotebookId(notebookId: String): Flow<List<PostEntity>>

    // Удалить конкретный пост
    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)
}