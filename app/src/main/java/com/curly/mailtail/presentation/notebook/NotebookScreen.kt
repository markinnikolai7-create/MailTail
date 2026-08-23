package com.curly.mailtail.presentation.notebook

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.curly.mailtail.data.local.entity.PostEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotebookViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val notebooks by viewModel.notebooks.collectAsState()
    val context = LocalContext.current

    val currentNotebook = notebooks.find { it.id == viewModel.notebookId }
    val isCreator = currentNotebook?.creatorName == "Я"

    var expandedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var expandedImageIndex by remember { mutableIntStateOf(0) }
    var showEditNotebookDialog by remember { mutableStateOf(false) }
    var postToEdit by remember { mutableStateOf<PostEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(currentNotebook?.title ?: "Записи") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    if (isCreator) {
                        IconButton(onClick = { showEditNotebookDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Редактировать название")
                        }
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(posts.sortedByDescending { it.dateMillis }) { post ->
                    PostBubble(
                        post = post,
                        onImageClick = { uris, index ->
                            expandedImages = uris
                            expandedImageIndex = index
                        },
                        onEditClick = { postToEdit = post },
                        onReact = { emoji -> viewModel.addReaction(post.id, emoji) },
                        onCommentClick = {
                            Toast.makeText(context, "Комментарии скоро появятся!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (expandedImages.isNotEmpty()) {
        FullScreenImageDialog(
            images = expandedImages,
            initialPage = expandedImageIndex,
            onDismiss = { expandedImages = emptyList() },
            onDownload = { uri -> saveImageToGallery(context, uri) }
        )
    }

    if (showEditNotebookDialog) {
        var newTitle by remember { mutableStateOf(currentNotebook?.title ?: "") }
        AlertDialog(
            onDismissRequest = { showEditNotebookDialog = false },
            title = { Text("Название блокнота") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newTitle.isNotBlank()) viewModel.updateNotebookTitle(newTitle)
                    showEditNotebookDialog = false
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { showEditNotebookDialog = false }) { Text("Отмена") }
            }
        )
    }

    postToEdit?.let { post ->
        var newContent by remember { mutableStateOf(post.content) }
        AlertDialog(
            onDismissRequest = { postToEdit = null },
            title = { Text("Редактировать запись") },
            text = {
                OutlinedTextField(
                    value = newContent,
                    onValueChange = { newContent = it },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newContent.isNotBlank()) viewModel.updatePostContent(post, newContent)
                    postToEdit = null
                }) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton(onClick = { postToEdit = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun PostBubble(
    post: PostEntity,
    onImageClick: (List<Uri>, Int) -> Unit,
    onEditClick: () -> Unit,
    onReact: (String) -> Unit,
    onCommentClick: () -> Unit
) {
    val formatter = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = formatter.format(Date(post.dateMillis))

    val isMine = post.authorName == "Я"
    val align = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    val imageUris = remember(post.imageUris) {
        if (!post.imageUris.isNullOrBlank()) {
            post.imageUris.split(",").map { Uri.parse(it) }
        } else emptyList()
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = align) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(color = bubbleColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (!post.title.isNullOrBlank()) {
                    Text(text = post.title, color = textColor, style = MaterialTheme.typography.titleMedium)
                } else Spacer(modifier = Modifier.weight(1f))

                if (isMine) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Изменить",
                        tint = textColor.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp).clickable { onEditClick() }
                    )
                }
            }

            Text(text = post.content, color = textColor, style = MaterialTheme.typography.bodyLarge)

            if (imageUris.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(imageUris) { index, uri ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Фото",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { onImageClick(imageUris, index) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateString,
                    color = textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = { onReact("❤️") },
                        label = { Text("❤️") }
                    )
                    IconButton(onClick = onCommentClick, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Комментировать", tint = textColor)
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(
    images: List<Uri>,
    initialPage: Int,
    onDismiss: () -> Unit,
    onDownload: (Uri) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { images.size })
    var offsetY by remember { mutableFloatStateOf(0f) }
    val animatedOffsetY by animateFloatAsState(targetValue = offsetY, label = "offsetY")
    val alpha = (1f - (abs(offsetY) / 1000f)).coerceIn(0f, 1f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alpha))
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            if (abs(offsetY) > 300f) {
                                onDismiss()
                            } else {
                                offsetY = 0f
                            }
                        },
                        onVerticalDrag = { _, dragAmount ->
                            offsetY += dragAmount
                        }
                    )
                }
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = "Полноэкранное фото",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Закрыть", tint = Color.White)
                }
                IconButton(onClick = { onDownload(images[pagerState.currentPage]) }) {
                    Icon(Icons.Default.Download, contentDescription = "Скачать", tint = Color.White)
                }
            }
        }
    }
}

fun saveImageToGallery(context: Context, sourceUri: Uri) {
    try {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "MailTail_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
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