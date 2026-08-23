package com.curly.mailtail.presentation.notebook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable // Добавляем импорт
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // Добавляем импорт
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: MailTailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val notebookId: String = checkNotNull(savedStateHandle["notebookId"])

    fun savePost(content: String, onSaved: () -> Unit) {
        viewModelScope.launch {
            val newPost = PostEntity(
                id = UUID.randomUUID().toString(),
                notebookId = notebookId,
                content = content,
                dateMillis = System.currentTimeMillis(),
                authorName = "Я"
            )

            // Защищаем процесс записи от убийства при закрытии экрана
            withContext(NonCancellable) {
                repository.createPost(newPost)
            }

            onSaved()
        }
    }
}