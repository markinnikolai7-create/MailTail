package com.curly.mailtail.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MailTailRepository
) : ViewModel() {

    // Превращаем обычный Flow из БД в StateFlow, который понимает Compose.
    // Если БД пустая, изначально отдаем пустой список (emptyList)
    val notebooks: StateFlow<List<NotebookEntity>> = repository.getAllNotebooks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Функция для создания блокнота. Запускается в корутине (viewModelScope)
    fun createNotebook(title: String) {
        viewModelScope.launch {
            val newNotebook = NotebookEntity(
                id = UUID.randomUUID().toString(), // Генерируем уникальный случайный ID
                title = title,
                memberCount = 1 // Создатель по умолчанию один
            )
            repository.createNotebook(newNotebook)
        }
    }
}