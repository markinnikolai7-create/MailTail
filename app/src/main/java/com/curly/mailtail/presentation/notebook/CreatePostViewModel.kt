package com.curly.mailtail.presentation.notebook

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.domain.repository.MailTailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: MailTailRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val notebookId: String = checkNotNull(savedStateHandle["notebookId"])

    fun savePost(
        title: String,
        content: String,
        dateMillis: Long,
        imageUris: List<Uri>,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            // Соединяем URI картинок в одну строку через запятую
            val imagesString = if (imageUris.isNotEmpty()) {
                imageUris.joinToString(separator = ",") { it.toString() }
            } else {
                null
            }

            val newPost = PostEntity(
                id = UUID.randomUUID().toString(),
                notebookId = notebookId,
                authorName = "Я",
                title = title,
                content = content,
                dateMillis = dateMillis,
                imageUris = imagesString,
                isDraft = false
            )

            withContext(NonCancellable) {
                repository.createPost(newPost)
            }

            onSaved()
        }
    }
}