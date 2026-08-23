package com.curly.mailtail.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToNotebook: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notebooks by viewModel.notebooks.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "TailMail",
                        color = AccentPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    // Иконка уведомлений без точки и полая (Outlined)
                    IconButton(onClick = { /* TODO: Экран уведомлений */ }) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Уведомления",
                            tint = AccentPink
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppBackground
                ),
                // Делаем шапку чуть более компактной
                modifier = Modifier.height(56.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = BottomNavBackground,
                contentColor = AccentPink,
                modifier = Modifier.height(64.dp) // Уменьшаем высоту подвала (стандарт 80dp)
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Уже тут */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная", modifier = Modifier.size(28.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPink,
                        unselectedIconColor = IconUnselected,
                        indicatorColor = Color.Transparent // Убираем стандартный овал
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* TODO: Профиль */ },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Профиль", modifier = Modifier.size(28.dp)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentPink,
                        unselectedIconColor = IconUnselected,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            // Уменьшили отступы сверху и снизу для большего пространства
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(notebooks) { notebook ->
                NotebookCard(
                    notebook = notebook,
                    onClick = { onNavigateToNotebook(notebook.id) }
                )
            }

            item {
                CreateNotebookButton(onClick = { showCreateDialog = true })
            }
        }
    }

    if (showCreateDialog) {
        CreateNotebookDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title ->
                viewModel.createNotebook(title)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun NotebookCard(
    notebook: NotebookEntity,
    onClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PostSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp, 60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFC3E0F7))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notebook.title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                // Заменили 12 на 0 записей
                Text(
                    text = "0 записей",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val maxVisible = 3
                    val members = notebook.memberCount

                    for (i in 0 until minOf(members, maxVisible)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFD9D9D9))
                        )
                    }
                    if (members > maxVisible) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4EE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${members - maxVisible}",
                                color = AccentPink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Выпадающее меню
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Меню", tint = AccentPink)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(BottomNavBackground) // Розовый фон меню
                ) {
                    DropdownMenuItem(
                        text = { Text("Редактировать", color = AccentPink) },
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Пригласить участника", color = AccentPink) },
                        onClick = { showMenu = false }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Удалить",
                                color = AccentPink,
                                textDecoration = TextDecoration.Underline // Подчеркивание
                            )
                        },
                        onClick = { showMenu = false }
                    )
                }
            }
        }
    }
}

@Composable
fun CreateNotebookButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .dashedBorder(color = AccentPink, strokeWidth = 1.5.dp, cornerRadius = 16.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = "Создать", tint = AccentPink)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Создать дневник",
                color = AccentPink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun Modifier.dashedBorder(color: Color, strokeWidth: Dp, cornerRadius: Dp) = this.drawWithCache {
    val dash = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
    onDrawWithContent {
        drawContent()
        drawRoundRect(
            color = color,
            style = Stroke(width = strokeWidth.toPx(), pathEffect = dash),
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
}

@Composable
fun CreateNotebookDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var titleText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новый дневник", color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Название", color = TextSecondary) },
                singleLine = true,
                // Жестко задаем цвет текста
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentPink,
                    focusedBorderColor = AccentPink
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (titleText.isNotBlank()) onCreate(titleText) },
                colors = ButtonDefaults.textButtonColors(contentColor = AccentPink)
            ) { Text("Создать") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) { Text("Отмена") }
        },
        containerColor = AppBackground
    )
}