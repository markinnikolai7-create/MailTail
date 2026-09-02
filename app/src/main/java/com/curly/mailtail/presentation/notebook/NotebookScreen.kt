package com.curly.mailtail.presentation.notebook

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curly.mailtail.data.local.entity.PostEntity
import com.curly.mailtail.presentation.ui.theme.AccentPink
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed

@Composable
fun NotebookScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreatePost: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotebookViewModel = hiltViewModel()
) {
    val notebook by viewModel.notebook.collectAsState()
    val posts by viewModel.posts.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var fullScreenImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var initialImagePage by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Назад",
                        tint = AccentPink
                    )
                }

                Text(
                    text = notebook?.title ?: "Загрузка...",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Аватарки участников в шапке
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    val members = notebook?.memberCount ?: 1
                    val maxVisible = 3
                    for (i in 0 until minOf(members, maxVisible)) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD9D9D9))
                        )
                    }
                    if (members > maxVisible) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4EE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "+${members - maxVisible}", color = AccentPink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = { /* TODO: Настройки дневника */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "Меню",
                        tint = AccentPink
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreatePost(viewModel.notebookId) },
                containerColor = Color(0xFFFFE4EE), // Светло-розовый из макета
                contentColor = AccentPink,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить запись", modifier = Modifier.size(32.dp))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Табы: Лента, Медиа, Черновики
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Лента", "Медиа", "Черновики").forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (selectedTab == index) AccentPink else Color.Transparent)
                            .clickable { selectedTab = index }
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTab == index) Color.White else AccentPink,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Здесь пока пусто", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Группируем посты по отформатированному месяцу и году
                    val groupedPosts = posts.groupBy { post -> formatMonthYear(post.timestamp) }

                    groupedPosts.forEach { (monthYear, monthPosts) ->
                        item {
                            Text(
                                text = monthYear,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                            )
                        }

                        items(monthPosts) { post ->
                            PostCardMockup(
                                post = post,
                                onClick = { /* TODO: Открытие поста */ },
                                onImageClick = { uris, index ->
                                    fullScreenImages = uris
                                    initialImagePage = index
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    // Полноэкранный просмотрщик фото с листанием и свайпом
    // Полноэкранный просмотрщик фото с листанием и интерактивным свайпом
    if (fullScreenImages.isNotEmpty()) {
        Dialog(
            onDismissRequest = { fullScreenImages = emptyList() },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true
            )
        ) {
            val pagerState = rememberPagerState(
                initialPage = initialImagePage,
                pageCount = { fullScreenImages.size }
            )

            // Отслеживаем смещение пальца
            var offsetY by remember { mutableFloatStateOf(0f) }
            // Плавная анимация для возврата фото на место
            val animatedOffsetY by animateFloatAsState(targetValue = offsetY, label = "offsetY")
            // Прозрачность фона зависит от того, как далеко потянули картинку
            val backgroundAlpha = (1f - (abs(animatedOffsetY) / 1000f)).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = backgroundAlpha * 0.9f))
                    .pointerInput(Unit) {
                        detectVerticalDragGestures(
                            onDragEnd = {
                                // Если смахнули вверх или вниз достаточно сильно (больше 200 пикселей)
                                if (abs(offsetY) > 200f) {
                                    fullScreenImages = emptyList()
                                } else {
                                    // Возвращаем картинку в центр, если свайп был слабым
                                    offsetY = 0f
                                }
                            },
                            onDragCancel = { offsetY = 0f }
                        ) { change, dragAmount ->
                            change.consume()
                            offsetY += dragAmount
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = animatedOffsetY
                            // Легкое уменьшение картинки при свайпе для эффекта глубины
                            val scale = (1f - (abs(animatedOffsetY) / 1500f)).coerceIn(0.7f, 1f)
                            scaleX = scale
                            scaleY = scale
                        }
                ) { page ->
                    AsyncImage(
                        model = fullScreenImages[page],
                        contentDescription = "Полноэкранное фото",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                // Закрытие по простому тапу, если не было свайпа
                                if (abs(offsetY) < 10f) {
                                    fullScreenImages = emptyList()
                                }
                            },
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun PostCardMockup(
    post: PostEntity,
    onClick: () -> Unit,
    onImageClick: (List<String>, Int) -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Шапка поста: Аватар + Имя + Меню
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9D9D9))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = post.authorName,
                    color = AccentPink,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* TODO: Меню поста */ }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Меню", tint = AccentPink)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Заголовок (показываем, только если он не пустой)
            if (post.title.isNotBlank()) {
                Text(
                    text = post.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Текст поста
            Text(
                text = post.content,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            // Блок с фото (появляется ТОЛЬКО если есть прикрепленные картинки)
            val images = post.imageUris?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

            if (images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(images) { index, uriString ->
                        AsyncImage(
                            model = uriString,
                            contentDescription = "Фото записи",
                            modifier = Modifier
                                // 1 фото = на всю ширину, 2 и более = показываем ровно 2 (с учетом отступа)
                                .fillParentMaxWidth(if (images.size == 1) 1f else 0.48f)
                                .aspectRatio(1f) // Квадрат
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F0F0))
                                .clickable { onImageClick(images, index) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Подвал: Иконка комментариев и счетчик
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Комментарии",
                    tint = AccentPink,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "##", // Заглушку счетчика комментариев тоже потом заменим на реальную цифру
                    color = AccentPink,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// Вспомогательная функция вынесена на уровень файла (вне любых Composable)
fun formatMonthYear(timestamp: Long): String {
    val formatter = SimpleDateFormat("LLLL yyyy", Locale("ru"))
    return formatter.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
}