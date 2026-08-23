package com.curly.mailtail.presentation.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curly.mailtail.R
import com.curly.mailtail.presentation.ui.theme.AccentPink
import com.curly.mailtail.presentation.ui.theme.AppBackground
import com.curly.mailtail.presentation.ui.theme.PostSurface
import com.curly.mailtail.presentation.ui.theme.TextPrimary
import com.curly.mailtail.presentation.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotebookScreen(
    onNavigateBack: () -> Unit,
    onNotebookCreated: (String, Int, Int) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) } // 1 - конверт, 2 - штамп, 3 - название
    var selectedEnvelopeIndex by remember { mutableIntStateOf(0) }
    var selectedStampIndex by remember { mutableIntStateOf(-1) } // -1 означает без штампа
    var notebookTitle by remember { mutableStateOf("") }

    // Список твоих конвертов из drawable (без back_price)
    val envelopeDrawables = listOf(
        R.drawable.env_tirq,
        R.drawable.env_tirq_mesh,
        R.drawable.env_blue,
        R.drawable.env_blue_mesh,
        R.drawable.env_yellow_mesh,
        R.drawable.env_green,
        R.drawable.env_green_mesh,
        R.drawable.env_orange_mesh,
        R.drawable.env_peach,
        R.drawable.env_peach_mesh,
        R.drawable.env_pink,
        R.drawable.env_violet,
        R.drawable.env_violet_mesh
    )

    // Список штампов из drawable (индекс -1 — заглушка "без штампа")
    val stampDrawables = listOf(
        R.drawable.stamp_star,
        R.drawable.stamp_moon,
        R.drawable.stamp_heart,
        R.drawable.stamp_blue_lightning
    )

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = {
                    if (currentStep == 3) {
                        Text("Новый дневник", color = AccentPink, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep -= 1 else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = AccentPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        floatingActionButton = {
            if (currentStep < 3) {
                FloatingActionButton(
                    onClick = { currentStep += 1 },
                    containerColor = Color(0xFFFFE4EE),
                    contentColor = AccentPink,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Далее", modifier = Modifier.size(32.dp))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ПРЕДПРОСМОТР СВЕРХУ (в CreateNotebookScreen)
            Box(
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 32.dp)
                    .size(260.dp, 170.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = envelopeDrawables[selectedEnvelopeIndex]),
                    contentDescription = "Конверт",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                if (selectedStampIndex >= 0) {
                    Image(
                        painter = painterResource(id = stampDrawables[selectedStampIndex]),
                        contentDescription = "Штамп",
                        // Смещаем штамп чуть выше центра, на самый кончик конверта
                        modifier = Modifier
                            .size(56.dp)
                            .offset(y = (-14).dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // НИЖНЯЯ ЧАСТЬ С ПЛАВНОЙ СМЕНОЙ ШАГОВ
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "step_transition"
            ) { step ->
                when (step) {
                    1 -> {
                        // ШАГ 1: Сетка выбора конвертов
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(envelopeDrawables.size) { index ->
                                val isSelected = selectedEnvelopeIndex == index
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(1.4f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) AccentPink.copy(alpha = 0.15f) else Color.Transparent)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) AccentPink else Color.Transparent,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(4.dp)
                                        .clickable { selectedEnvelopeIndex = index },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = envelopeDrawables[index]),
                                        contentDescription = "Вариант конверта",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // ШАГ 2: Сетка выбора штампов
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Вариант 1: Без штампа (перечеркнутый круг)
                            item {
                                StampItem(
                                    isSelected = selectedStampIndex == -1,
                                    onClick = { selectedStampIndex = -1 }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        tint = Color.Gray,
                                        contentDescription = "Без штампа",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            // Варианты штампов из drawable
                            items(stampDrawables.size) { index ->
                                val isSelected = selectedStampIndex == index
                                StampItem(
                                    isSelected = isSelected,
                                    onClick = { selectedStampIndex = index }
                                ) {
                                    Image(
                                        painter = painterResource(id = stampDrawables[index]),
                                        contentDescription = "Штамп",
                                        modifier = Modifier.size(48.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                    3 -> {
                        // ШАГ 3: Ввод названия дневника
                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = notebookTitle,
                                onValueChange = { notebookTitle = it },
                                placeholder = { Text("Название дневника", color = TextSecondary) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = PostSurface,
                                    unfocusedContainerColor = PostSurface,
                                    focusedBorderColor = AccentPink,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    if (notebookTitle.isNotBlank()) {
                                        onNotebookCreated(notebookTitle, selectedEnvelopeIndex, selectedStampIndex)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                enabled = notebookTitle.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Создать дневник", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StampItem(isSelected: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(if (isSelected) AccentPink.copy(alpha = 0.15f) else PostSurface)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) AccentPink else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}