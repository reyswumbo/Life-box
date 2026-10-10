package com.reyswumbo.lifeinbox.ui.petlab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyswumbo.lifeinbox.LifeInboxApplication
import com.reyswumbo.lifeinbox.domain.pet.Accessory
import com.reyswumbo.lifeinbox.domain.pet.BodyShape
import com.reyswumbo.lifeinbox.domain.pet.EyeStyle
import com.reyswumbo.lifeinbox.domain.pet.PetAnimationState
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.domain.pet.PetTexture
import com.reyswumbo.lifeinbox.ui.pet.PetCanvas
import com.reyswumbo.lifeinbox.ui.pet.PetStage
import com.reyswumbo.lifeinbox.ui.pet.rememberPetAnimationController
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@Composable
fun PetLabScreen() {
    val application = LocalContext.current.applicationContext as LifeInboxApplication
    val viewModel: PetLabViewModel = viewModel(
        factory = PetLabViewModelFactory(application.container.petConfigRepository)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val controller = rememberPetAnimationController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PetLabEvent.Message -> {
                    snackbarHostState.showSnackbar(event.text)
                    controller.trigger(PetAnimationState.CELEBRATING, 1000)
                }
            }
        }
    }

    fun reactToOptionChange(apply: () -> Unit) {
        apply()
        controller.trigger(PetAnimationState.CUSTOMIZING, 420)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(14.dp))
            Text(
                text = "PET LAB",
                style = MaterialTheme.typography.titleLarge,
                color = LifeInboxColors.DarkText,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Give your pal a personality.",
                style = MaterialTheme.typography.bodySmall,
                color = LifeInboxColors.SecondaryText,
                textAlign = TextAlign.Center
            )

            PetStage(
                config = uiState.draft,
                controller = controller,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.draft.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Character name") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(Modifier.height(16.dp))

            CategorySelector(
                selected = uiState.category,
                onSelect = viewModel::selectCategory
            )

            Spacer(Modifier.height(12.dp))

            when (uiState.category) {
                PetLabCategory.BODY -> OptionPreviewGrid(
                    options = listOf(
                        Triple("Round", "ROUND", uiState.draft.copy(bodyShape = BodyShape.ROUND)),
                        Triple("Boxy", "BOXY", uiState.draft.copy(bodyShape = BodyShape.BOXY)),
                        Triple("Fluffy", "FLUFFY", uiState.draft.copy(bodyShape = BodyShape.FLUFFY)),
                        Triple("Triangle", "TRIANGLE", uiState.draft.copy(bodyShape = BodyShape.TRIANGLE)),
                        Triple("Heart", "HEART", uiState.draft.copy(bodyShape = BodyShape.HEART))
                    ),
                    selectedKey = uiState.draft.bodyShape.name,
                    onSelect = { key ->
                        reactToOptionChange {
                            viewModel.onBodyShape(BodyShape.valueOf(key))
                        }
                    }
                )

                PetLabCategory.EYES -> OptionPreviewGrid(
                    options = listOf(
                        Triple("Normal", "NORMAL", uiState.draft.copy(eyeStyle = EyeStyle.NORMAL)),
                        Triple("Sleepy", "SLEEPY", uiState.draft.copy(eyeStyle = EyeStyle.SLEEPY)),
                        Triple("Happy", "HAPPY", uiState.draft.copy(eyeStyle = EyeStyle.HAPPY)),
                        Triple("Curious", "CURIOUS", uiState.draft.copy(eyeStyle = EyeStyle.CURIOUS))
                    ),
                    selectedKey = uiState.draft.eyeStyle.name,
                    onSelect = { key ->
                        reactToOptionChange {
                            viewModel.onEyeStyle(EyeStyle.valueOf(key))
                        }
                    }
                )

                PetLabCategory.ACCESSORIES -> OptionPreviewGrid(
                    options = listOf(
                        Triple("None", "NONE", uiState.draft.copy(accessory = Accessory.NONE)),
                        Triple("Crown", "CROWN", uiState.draft.copy(accessory = Accessory.CROWN)),
                        Triple("Horns", "HORNS", uiState.draft.copy(accessory = Accessory.HORNS)),
                        Triple("Hat", "HAT", uiState.draft.copy(accessory = Accessory.HAT))
                    ),
                    selectedKey = uiState.draft.accessory.name,
                    onSelect = { key ->
                        reactToOptionChange {
                            viewModel.onAccessory(Accessory.valueOf(key))
                        }
                    }
                )

                PetLabCategory.TEXTURES -> OptionPreviewGrid(
                    options = listOf(
                        Triple("Smooth", "SMOOTH", uiState.draft.copy(texture = PetTexture.SMOOTH)),
                        Triple("Fluffy", "FLUFFY", uiState.draft.copy(texture = PetTexture.FLUFFY)),
                        Triple("Fuzzy", "FUZZY", uiState.draft.copy(texture = PetTexture.FUZZY))
                    ),
                    selectedKey = uiState.draft.texture.name,
                    onSelect = { key ->
                        reactToOptionChange {
                            viewModel.onTexture(PetTexture.valueOf(key))
                        }
                    }
                )

                PetLabCategory.COLORS -> ColorOptions(
                    draft = uiState.draft,
                    onBodyColor = { hex ->
                        reactToOptionChange { viewModel.onBodyColor(hex) }
                    },
                    onEyeColor = { hex ->
                        reactToOptionChange { viewModel.onEyeColor(hex) }
                    },
                    onAccentColor = { hex ->
                        reactToOptionChange { viewModel.onAccentColor(hex) }
                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LifeInboxColors.Sage,
                    contentColor = LifeInboxColors.DarkText
                )
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Character")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CategorySelector(
    selected: PetLabCategory,
    onSelect: (PetLabCategory) -> Unit
) {
    val categories = remember {
        listOf(
            PetLabCategory.BODY to "Body",
            PetLabCategory.EYES to "Eyes",
            PetLabCategory.ACCESSORIES to "Accessories",
            PetLabCategory.COLORS to "Colors",
            PetLabCategory.TEXTURES to "Texture"
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { (category, label) ->
            FilterChip(
                selected = category == selected,
                onClick = { onSelect(category) },
                label = { Text(label) },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LifeInboxColors.Sage,
                    selectedLabelColor = LifeInboxColors.DarkText
                )
            )
        }
    }
}

@Composable
private fun OptionPreviewGrid(
    options: List<Triple<String, String, PetConfig>>,
    selectedKey: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { (label, key, config) ->
            val selected = key == selectedKey
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selected) LifeInboxColors.WarmCream else LifeInboxColors.SurfaceWhite
                    ),
                    border = BorderStroke(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) LifeInboxColors.Sage else LifeInboxColors.PaleSage
                    ),
                    modifier = Modifier
                        .size(86.dp)
                        .clickable { onSelect(key) }
                ) {
                    PetCanvas(
                        config = config,
                        state = PetAnimationState.IDLE,
                        showPlatform = false,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = LifeInboxColors.DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ColorOptions(
    draft: PetConfig,
    onBodyColor: (String) -> Unit,
    onEyeColor: (String) -> Unit,
    onAccentColor: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SwatchRow(
            title = "Body",
            palette = PetConfig.bodyPalette,
            selected = draft.bodyColorHex,
            onSelect = onBodyColor
        )
        SwatchRow(
            title = "Eyes",
            palette = PetConfig.eyePalette,
            selected = draft.eyeColorHex,
            onSelect = onEyeColor
        )
        SwatchRow(
            title = "Accent",
            palette = PetConfig.accentPalette,
            selected = draft.accentColorHex,
            onSelect = onAccentColor
        )
    }
}

@Composable
private fun SwatchRow(
    title: String,
    palette: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = LifeInboxColors.DarkText
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            palette.forEach { hex ->
                val isSelected = hex == selected
                val swatchColor = hex.toSwatchColor()
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 46.dp else 40.dp)
                        .clip(CircleShape)
                        .background(swatchColor)
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, LifeInboxColors.DarkText, CircleShape)
                            } else {
                                Modifier.border(1.dp, LifeInboxColors.PaleSage, CircleShape)
                            }
                        )
                        .clickable { onSelect(hex) }
                )
            }
        }
    }
}

private fun String.toSwatchColor(): Color = try {
    val cleaned = removePrefix("#")
    val rgb = cleaned.toLong(16)
    Color(0xFF000000L or rgb)
} catch (e: Exception) {
    LifeInboxColors.Sage
}