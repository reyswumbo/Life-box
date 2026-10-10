package com.reyswumbo.lifeinbox.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.ui.pet.PetStage
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@Composable
fun HomeScreen() {
    val config = remember { PetConfig.Default }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "LIFE INBOX",
            style = MaterialTheme.typography.titleLarge,
            color = LifeInboxColors.DarkText,
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 20.dp)
        )

        PetStage(
            config = config,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp)
        )

        Text(
            text = "Take your time.",
            style = MaterialTheme.typography.bodyMedium,
            color = LifeInboxColors.SecondaryText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    }
}
