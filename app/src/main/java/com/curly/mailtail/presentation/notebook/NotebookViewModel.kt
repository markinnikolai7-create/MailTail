package com.curly.mailtail.presentation.notebook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.data.local.entity.ReactionEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val repository: MailTailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val notebookId: String = checkNotNull(savedStateHandle["notebookId"])

    // Подтягиваем все блокноты, чтобы в UI найти текущий и проверить его создателя
    val notebooks: StateFlow<List<NotebookEntity>> = repository.getAllNotebooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val posts: StateFlow<List<PostEntity>> = repository.getPostsForNotebook(notebookId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateNotebookTitle(newTitle: String) {
        viewModelScope.launch {
            repository.updateNotebookTitle(notebookId, newTitle)
        }
    }

    fun updatePostContent(post: PostEntity, newContent: String) {
        viewModelScope.launch {
            // Сохраняем пост с новым текстом
            repository.updatePost(post.copy(content = newContent))
        }
    }

    fun addReaction(postId: String, emoji: String) {
        viewModelScope.launch {
            val reaction = ReactionEntity(
                id = UUID.randomUUID().toString(),
                postId = postId,
                authorName = "Я",
                emoji = emoji
            )
            repository.addReaction(reaction)
        }
    }
}