package com.curly.mailtail.presentation.notebook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val repository: MailTailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val notebookId: String = checkNotNull(savedStateHandle["notebookId"])

    private val _notebook = MutableStateFlow<NotebookEntity?>(null)
    val notebook: StateFlow<NotebookEntity?> = _notebook.asStateFlow()

    private val _posts = MutableStateFlow<List<PostEntity>>(emptyList())
    val posts: StateFlow<List<PostEntity>> = _posts.asStateFlow()

    init {
        loadNotebookData()
    }

    private fun loadNotebookData() {
        viewModelScope.launch {
            // Заглушка, пока не подключим реальные методы из репозитория
        }
    }
}