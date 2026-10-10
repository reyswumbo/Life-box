package com.reyswumbo.lifeinbox.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyswumbo.lifeinbox.LifeInboxApplication
import com.reyswumbo.lifeinbox.domain.pet.PetAnimationState
import com.reyswumbo.lifeinbox.ui.pet.PetStage
import com.reyswumbo.lifeinbox.ui.pet.rememberPetAnimationController
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun HomeScreen() {
    val application = LocalContext.current.applicationContext as LifeInboxApplication
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            application.container.linkRepository,
            application.container.collectionRepository
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val application = LocalContext.current.applicationContext as LifeInboxApplication
    val petConfig by application.container.petConfig.collectAsStateWithLifecycle()
    val controller = rememberPetAnimationController()

    var saveTick by remember { mutableStateOf(0) }
    var eatingProgress by remember { mutableStateOf(0f) }
    var collectionMenu by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.Saved -> {
                    saveTick += 1
                    controller.playSequence(
                        listOf(
                            PetAnimationState.RECEIVING to 280L,
                            PetAnimationState.OPEN_MOUTH to 280L,
                            PetAnimationState.EATING_LINK to 420L,
                            PetAnimationState.SWALLOWING to 360L,
                            PetAnimationState.CELEBRATING to 900L
                        )
                    )
                }

                is HomeEvent.Failed -> Unit
            }
        }
    }

    LaunchedEffect(saveTick) {
        if (saveTick == 0) return@LaunchedEffect
        eatingProgress = 0f
        while (eatingProgress < 1f) {
            eatingProgress += 1f / (60f * 0.9f)
            delay(16L)
        }
        eatingProgress = 0f
    }

    val selectedCollectionName = uiState.collections
        .firstOrNull { it.id == uiState.selectedCollectionId }
        ?.name
        ?: "Inbox"

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIFE INBOX",
                    style = MaterialTheme.typography.titleLarge,
                    color = LifeInboxColors.DarkText,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                DailyCountChip(count = uiState.dailyCount)
            }

            PetStage(
                config = petConfig,
                controller = controller,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 4.dp)
            )

            Text(
                text = uiState.message,
                style = MaterialTheme.typography.bodyMedium,
                color = LifeInboxColors.SecondaryText,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.url,
                    onValueChange = viewModel::onUrlChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Paste a link to save...") },
                    singleLine = true,
                    shape = RoundedCornerShape(22.dp)
                )
                IconButton(
                    onClick = viewModel::submit,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(LifeInboxColors.Sage)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = LifeInboxColors.DarkText
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Save link",
                            tint = LifeInboxColors.DarkText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Save to",
                    style = MaterialTheme.typography.labelMedium,
                    color = LifeInboxColors.SecondaryText
                )
                Spacer(Modifier.width(8.dp))
                Box {
                    AssistChip(
                        onClick = { collectionMenu = true },
                        label = {
                            Text(
                                selectedCollectionName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = collectionMenu,
                        onDismissRequest = { collectionMenu = false }
                    ) {
                        uiState.collections.forEach { collection ->
                            DropdownMenuItem(
                                text = { Text(collection.name) },
                                onClick = {
                                    viewModel.onCollectionSelected(collection.id)
                                    collectionMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }

        LinkEatingOverlay(
            progress = eatingProgress,
            density = density,
            maxWidthPx = with(density) { maxWidth.roundToPx() },
            maxHeightPx = with(density) { maxHeight.roundToPx() }
        )
    }
}

@Composable
private fun DailyCountChip(count: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LifeInboxColors.WarmCream),
        border = BorderStroke(1.dp, LifeInboxColors.PaleSage)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "YOUR SAVES",
                style = MaterialTheme.typography.labelSmall,
                color = LifeInboxColors.SecondaryText,
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                color = LifeInboxColors.DarkText
            )
        }
    }
}

@Composable
private fun LinkEatingOverlay(
    progress: Float,
    density: androidx.compose.ui.unit.Density,
    maxWidthPx: Int,
    maxHeightPx: Int
) {
    if (progress <= 0f || progress >= 1f) return

    val cardWidthPx = with(density) { 160.dp.toPx() }
    val cardHeightPx = with(density) { 88.dp.toPx() }

    val startY = maxHeightPx * 0.86f
    val endY = maxHeightPx * 0.36f
    val y = startY + (endY - startY) * progress
    val x = maxWidthPx / 2f
    val scale = 1.2f + (0.32f - 1.2f) * progress
    val alpha = if (progress < 0.75f) 1f else 1f - (progress - 0.75f) / 0.25f

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (x - cardWidthPx / 2f).roundToInt(),
                    (y - cardHeightPx / 2f).roundToInt()
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .width(160.dp)
            .height(88.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LifeInboxColors.SurfaceWhite),
            border = BorderStroke(1.dp, LifeInboxColors.PaleSage)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LifeInboxColors.Sage)
                )
                Text(
                    text = "Saved link",
                    style = MaterialTheme.typography.bodySmall,
                    color = LifeInboxColors.DarkText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "example.com",
                    style = MaterialTheme.typography.labelSmall,
                    color = LifeInboxColors.SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}