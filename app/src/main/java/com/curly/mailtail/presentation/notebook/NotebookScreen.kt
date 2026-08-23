package com.curly.mailtail.presentation.notebook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curly.mailtail.data.local.entity.PostEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.net.Uri
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotebookViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Записи") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreatePost(viewModel.notebookId) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Написать")
            }
        }
    ) { innerPadding ->
        if (posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Здесь пока пусто. Напишите первую запись!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Сортируем по ПРАВИЛЬНОМУ полю dateMillis
                items(posts.sortedByDescending { it.dateMillis }) { post ->
                    PostBubble(post = post)
                }
            }
        }
    }
}

@Composable
fun PostBubble(post: PostEntity) {
    val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = formatter.format(Date(post.dateMillis))

    val isMine = post.authorName == "Я"
    val align = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    // Достаем картинки обратно из строки
    val imageUris = remember(post.imageUris) {
        if (!post.imageUris.isNullOrBlank()) {
            post.imageUris.split(",").map { Uri.parse(it) }
        } else {
            emptyList()
        }
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(color = bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!post.title.isNullOrBlank()) {
                Text(text = post.title, color = textColor, style = MaterialTheme.typography.titleMedium)
            }

            Text(text = post.content, color = textColor, style = MaterialTheme.typography.bodyLarge)

            // Если есть фото, выводим их каруселью
            if (imageUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(imageUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Фото",
                            modifier = Modifier
                                .size(90.dp)
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Text(
                text = dateString,
                color = textColor.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}