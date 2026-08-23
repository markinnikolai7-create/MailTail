package com.curly.mailtail.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.curly.mailtail.R
import com.curly.mailtail.presentation.ui.theme.AccentPink
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit
) {
    // Экран будет висеть 2 секунды, а потом автоматически перейдет дальше
    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateNext()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Убедись, что назвал свой SVG файл ic_logo
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Логотип TailMail",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "TailMail",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPink
            )
        )
    }
}