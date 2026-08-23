package com.curly.mailtail.presentation.notebook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val repository: MailTailRepository,
    savedStateHandle: SavedStateHandle // Hilt сам положит сюда аргументы навигации!
) : ViewModel() {

    // Достаем ID блокнота. Ключ должен точно совпадать с тем, что мы писали в графе MainActivity
    val notebookId: String = checkNotNull(savedStateHandle["notebookId"])

    // Подписываемся только на те посты, которые принадлежат этому блокноту
    val posts: StateFlow<List<PostEntity>> = repository.getPostsForNotebook(notebookId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}