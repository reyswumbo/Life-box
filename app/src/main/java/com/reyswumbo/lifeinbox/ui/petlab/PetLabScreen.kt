package com.reyswumbo.lifeinbox.ui.petlab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reyswumbo.lifeinbox.domain.pet.PetAnimationState
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.ui.pet.PetCanvas
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@Composable
fun PetLabScreen() {
    val config = remember { PetConfig.Default }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Pet Lab",
            style = MaterialTheme.typography.titleLarge,
            color = LifeInboxColors.DarkText,
            textAlign = TextAlign.Center
        )
        PetCanvas(
            config = config,
            state = PetAnimationState.IDLE,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(vertical = 12.dp)
        )
        Text(
            text = "Customization tools are coming in a later step.",
            style = MaterialTheme.typography.bodyMedium,
            color = LifeInboxColors.SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}
