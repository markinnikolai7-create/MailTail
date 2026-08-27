package com.curly.mailtail.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.curly.mailtail.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import com.curly.mailtail.data.local.entity.CommentEntity

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Update
    suspend fun updatePost(post: PostEntity) // Редактирование поста

    @Query("SELECT * FROM posts WHERE notebook_id = :notebookId ORDER BY dateMillis DESC")
    fun getPostsByNotebookId(notebookId: String): Flow<List<PostEntity>>

    // Комментарии
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("SELECT * FROM comments WHERE post_id = :postId ORDER BY dateMillis ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>
}