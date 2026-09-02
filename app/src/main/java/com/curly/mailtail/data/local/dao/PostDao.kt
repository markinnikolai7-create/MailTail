package com.curly.mailtail.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.curly.mailtail.data.local.entity.CommentEntity // <-- Добавили импорт комментариев
import com.curly.mailtail.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import com.curly.mailtail.data.local.entity.PostWithCommentCount

@Dao
interface PostDao {

    // --- Посты ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Query("""
        SELECT posts.*, 
        (SELECT COUNT(*) FROM comments WHERE comments.post_id = posts.id) AS comment_count 
        FROM posts 
        WHERE notebookId = :notebookId 
        ORDER BY timestamp DESC
    """)
    fun getPostsByNotebookId(notebookId: String): Flow<List<PostWithCommentCount>>

    // --- Комментарии ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity) // <-- ВЕРНУЛИ

    // Примечание: если в CommentEntity таблица называется не "comments", или поле привязки не "postId",
    // поправь запрос под свои названия в сущности комментариев.
    @Query("SELECT * FROM comments WHERE post_id = :postId")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>
}