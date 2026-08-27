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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNotebookScreen(
    initialTitle: String = "",
    initialEnvelopeIndex: Int = 0,
    initialStampIndex: Int = -1,
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit,
    onSave: (String, Int, Int) -> Unit // Переименовали onNotebookCreated в onSave
) {
    var currentStep by remember { mutableIntStateOf(1) }
    // Используем начальные значения, если они переданы
    var selectedEnvelopeIndex by remember { mutableIntStateOf(initialEnvelopeIndex) }
    var selectedStampIndex by remember { mutableIntStateOf(initialStampIndex) }
    var notebookTitle by remember { mutableStateOf(initialTitle) }

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    if (currentStep == 3) {
                        // Меняем заголовок экрана
                        Text(if (isEditMode) "Редактирование" else "Новый дневник", color = AccentPink, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep -= 1 else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = AccentPink)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
                        modifier = Modifier
                            .size(56.dp)
                            .offset(y = 24.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "step_transition"
            ) { step ->
                when (step) {
                    1 -> {
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
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
                        Column(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = notebookTitle,
                                onValueChange = { notebookTitle = it },
                                placeholder = {
                                    Text("Название дневника", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = AccentPink,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    if (notebookTitle.isNotBlank()) {
                                        onSave(notebookTitle, selectedEnvelopeIndex, selectedStampIndex)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                enabled = notebookTitle.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                // Меняем текст на кнопке
                                Text(if (isEditMode) "Сохранить" else "Создать дневник", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            .background(if (isSelected) AccentPink.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
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