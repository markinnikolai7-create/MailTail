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

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download

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
fun PostBubble(post: PostEntity, onImageClick: (Uri) -> Unit) {
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

fun saveImageToGallery(context: Context, sourceUri: Uri) {
    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "MailTail_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Папка, в которую сохранится фото
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MailTail")
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri != null) {
            resolver.openInputStream(sourceUri)?.use { input ->
                resolver.openOutputStream(imageUri)?.use { output ->
                    input.copyTo(output)
                }
            }
            Toast.makeText(context, "Фото сохранено в галерею!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun FullScreenImageDialog(
    uri: Uri,
    onDismiss: () -> Unit,
    onDownload: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        // Заставляем диалог занять весь экран
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black) // Черный фон как в галереях
        ) {
            // Сама картинка
            AsyncImage(
                model = uri,
                contentDescription = "Полноэкранное фото",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit // Изображение помещается целиком
            )

            // Верхняя панель с кнопками
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
                }
                IconButton(onClick = onDownload) {
                    Icon(Icons.Default.Download, contentDescription = "Скачать", tint = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen( /* ... твои параметры ... */ ) {
    val posts by viewModel.posts.collectAsState()
    val context = LocalContext.current

    // Состояние: хранит URI картинки, которую мы открыли на весь экран. Если null — ничего не открыто.
    var expandedImageUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold( /* ... */ ) { innerPadding ->
        // ... твой LazyColumn ...
        items(posts.sortedByDescending { it.dateMillis }) { post ->
            // Передаем лямбду, которая срабатывает при клике на фото
            PostBubble(post = post, onImageClick = { clickedUri ->
                expandedImageUri = clickedUri
            })
        }
    }

    // Если переменная не null, показываем диалог
    expandedImageUri?.let { uri ->
        FullScreenImageDialog(
            uri = uri,
            onDismiss = { expandedImageUri = null }, // Закрываем
            onDownload = { saveImageToGallery(context, uri) } // Качаем
        )
    }
}