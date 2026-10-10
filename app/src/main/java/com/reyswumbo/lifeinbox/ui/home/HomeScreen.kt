package com.reyswumbo.lifeinbox.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
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
import com.reyswumbo.lifeinbox.ui.saved.CollectionCard
import com.reyswumbo.lifeinbox.ui.saved.CollectionVisuals
import com.reyswumbo.lifeinbox.ui.pet.PetStage
import com.reyswumbo.lifeinbox.ui.pet.rememberPetAnimationController
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.math.min
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
    val petConfig by application.container.petConfig.collectAsStateWithLifecycle()
    val controller = rememberPetAnimationController()

    var saveTick by remember { mutableStateOf(0) }
    var eatingProgress by remember { mutableStateOf(0f) }
    var collectionMenu by remember { mutableStateOf(false) }

    val idleMessages = remember {
        listOf(
            "Take your time.",
            "Ready for your next discovery?",
            "Your little ideas are safe here."
        )
    }
    var msgIndex by remember { mutableIntStateOf(0) }
    var showNotice by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(7000)
            msgIndex = (msgIndex + 1) % idleMessages.size
        }
    }
    LaunchedEffect(saveTick) {
        if (saveTick > 0) {
            showNotice = true
            delay(4500)
            showNotice = false
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            showNotice = true
            delay(6500)
            showNotice = false
        }
    }

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

    val shownMessage = if (showNotice) uiState.message else idleMessages[msgIndex]

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawAmbientRoom(size.width, size.height)
        }

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

            Spacer(Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(end = 4.dp)
            ) {
                item(key = "all") {
                    CollectionCard(
                        name = "All",
                        count = uiState.collectionsWithCount.sumOf { it.linkCount },
                        color = LifeInboxColors.PaleSage,
                        icon = Icons.Filled.Apps,
                        selected = uiState.selectedCollectionId == null,
                        onClick = { viewModel.onCollectionSelected(null) },
                        onLongClick = null
                    )
                }
                items(uiState.collectionsWithCount, key = { it.collection.id }) { item ->
                    CollectionCard(
                        name = item.collection.name,
                        count = item.linkCount,
                        color = CollectionVisuals.colorFromHex(item.collection.colorHex),
                        icon = CollectionVisuals.iconFor(item.collection.icon),
                        selected = uiState.selectedCollectionId == item.collection.id,
                        onClick = { viewModel.onCollectionSelected(item.collection.id) },
                        onLongClick = null
                    )
                }
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
                text = shownMessage,
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
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                            tint = LifeInboxColors.SecondaryText
                        )
                    },
                    singleLine = false,
                    shape = RoundedCornerShape(22.dp),
                    maxLines = 3
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

private fun DrawScope.drawAmbientRoom(width: Float, height: Float) {
    drawCircle(
        color = LifeInboxColors.SoftYellow.copy(alpha = 0.30f),
        radius = min(width, height) * 0.42f,
        center = Offset(width * 0.5f, height * 0.40f)
    )

    val green = LifeInboxColors.Sage.copy(alpha = 0.55f)
    val potColor = LifeInboxColors.DarkText.copy(alpha = 0.10f)
    val plantCx = width * 0.13f
    val plantCy = height * 0.68f

    val pot = Path().apply {
        moveTo(plantCx - 16f, plantCy)
        lineTo(plantCx + 16f, plantCy)
        lineTo(plantCx + 11f, plantCy + 20f)
        lineTo(plantCx - 11f, plantCy + 20f)
        close()
    }
    drawPath(pot, potColor)

    repeat(3) { i ->
        rotate(
            degrees = -28f + i * 28f,
            pivot = Offset(plantCx, plantCy - 4f)
        ) {
            drawOval(
                color = green,
                topLeft = Offset(plantCx - 5f, plantCy - 36f),
                size = Size(10f, 32f)
            )
        }
    }

    listOf(
        width * 0.79f to height * 0.30f,
        width * 0.87f to height * 0.38f,
        width * 0.83f to height * 0.46f
    ).forEach { (px, py) ->
        drawPaw(px, py)
    }
}

private fun DrawScope.drawPaw(cx: Float, cy: Float) {
    val alpha = 0.16f
    drawCircle(LifeInboxColors.DarkText.copy(alpha = alpha), radius = 4f, center = Offset(cx, cy))
    drawCircle(LifeInboxColors.DarkText.copy(alpha = alpha), radius = 2.4f, center = Offset(cx - 4f, cy - 3f))
    drawCircle(LifeInboxColors.DarkText.copy(alpha = alpha), radius = 2.4f, center = Offset(cx + 4f, cy - 3f))
    drawCircle(LifeInboxColors.DarkText.copy(alpha = alpha), radius = 2.4f, center = Offset(cx - 1.5f, cy - 4.5f))
    drawCircle(LifeInboxColors.DarkText.copy(alpha = alpha), radius = 2.4f, center = Offset(cx + 1.5f, cy - 4.5f))
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
            Text(
                text = "today",
                style = MaterialTheme.typography.labelSmall,
                color = LifeInboxColors.SecondaryText,
                fontSize = 9.sp
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