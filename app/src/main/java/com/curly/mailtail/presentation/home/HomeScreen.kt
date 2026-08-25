package com.curly.mailtail.presentation.home

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.curly.mailtail.R
import com.curly.mailtail.data.local.entity.NotebookEntity
import com.curly.mailtail.presentation.ui.theme.AccentPink

@Composable
fun HomeScreen(
    onNavigateToNotebook: (String) -> Unit,
    onNavigateToCreateNotebook: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val notebooks by viewModel.notebooks.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TailMail",
                    color = AccentPink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                IconButton(
                    onClick = { /* TODO: Уведомления */ },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Уведомления",
                        tint = AccentPink,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Уже тут */ }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Главная",
                        tint = AccentPink,
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { /* Профиль */ }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Профиль",
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(notebooks) { notebook ->
                NotebookCard(
                    notebook = notebook,
                    onClick = { onNavigateToNotebook(notebook.id) },
                    onDeleteClick = { viewModel.deleteNotebook(notebook.id) }
                )
            }

            item {
                CreateNotebookButton(onClick = onNavigateToCreateNotebook)
            }
        }
    }
}

@Composable
fun NotebookCard(
    notebook: NotebookEntity,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val envelopeDrawables = listOf(
        R.drawable.env_tirq, R.drawable.env_tirq_mesh, R.drawable.env_blue,
        R.drawable.env_blue_mesh, R.drawable.env_yellow_mesh, R.drawable.env_green,
        R.drawable.env_green_mesh, R.drawable.env_orange_mesh, R.drawable.env_peach,
        R.drawable.env_peach_mesh, R.drawable.env_pink, R.drawable.env_violet,
        R.drawable.env_violet_mesh
    )
    val stampDrawables = listOf(
        R.drawable.stamp_star, R.drawable.stamp_moon,
        R.drawable.stamp_heart, R.drawable.stamp_blue_lightning
    )

    val envelopeRes = envelopeDrawables.getOrElse(notebook.envelopeId) { R.drawable.env_blue }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = envelopeRes),
                    contentDescription = "Конверт",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (notebook.stampId >= 0) {
                    val stampRes = stampDrawables.getOrElse(notebook.stampId) { R.drawable.stamp_heart }
                    Image(
                        painter = painterResource(id = stampRes),
                        contentDescription = "Штамп",
                        modifier = Modifier
                            .size(22.dp)
                            .offset(y = 12.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notebook.title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "0 записей",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = "Меню", tint = AccentPink)
                }

                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                ) {
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
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
                                    textDecoration = TextDecoration.Underline
                                )
                            },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            }
                        )
                    }
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