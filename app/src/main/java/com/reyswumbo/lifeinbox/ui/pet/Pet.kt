package com.reyswumbo.lifeinbox.ui.pet

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.reyswumbo.lifeinbox.domain.pet.Accessory
import com.reyswumbo.lifeinbox.domain.pet.BodyShape
import com.reyswumbo.lifeinbox.domain.pet.EyeStyle
import com.reyswumbo.lifeinbox.domain.pet.PetAnimationController
import com.reyswumbo.lifeinbox.domain.pet.PetAnimationState
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.domain.pet.PetTexture
import com.reyswumbo.lifeinbox.ui.theme.LifeInboxColors

@Composable
fun rememberPetAnimationController(): PetAnimationController {
    val scope = rememberCoroutineScope()
    return remember(scope) { PetAnimationController(scope) }
}

/**
 * Renders a pet driven by a shared [PetAnimationController]. Idle animation
 * work is paused automatically when the host screen stops, satisfying the
 * "reduce work when not visible" requirement.
 */
@Composable
fun PetStage(
    config: PetConfig,
    modifier: Modifier = Modifier,
    controller: PetAnimationController = rememberPetAnimationController(),
    showPlatform: Boolean = true
) {
    val state by controller.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, controller) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> controller.setVisible(true)
                Lifecycle.Event.ON_STOP -> controller.setVisible(false)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        controller.setVisible(
            lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        )
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            controller.setVisible(false)
        }
    }

    PetCanvas(
        config = config,
        state = state,
        showPlatform = showPlatform,
        modifier = modifier
    )
}

@Composable
fun PetCanvas(
    config: PetConfig,
    state: PetAnimationState,
    modifier: Modifier = Modifier,
    showPlatform: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "pet")
    val bobPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 420, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val eyeOpen by animateFloatAsState(
        targetValue = when (state) {
            PetAnimationState.BLINKING, PetAnimationState.SLEEPING -> 0.06f
            else -> 1f
        },
        animationSpec = tween(durationMillis = 120),
        label = "eyeOpen"
    )
    val eyeShift by animateFloatAsState(
        targetValue = when (state) {
            PetAnimationState.LOOKING -> 0.24f
            PetAnimationState.CURIOUS -> -0.15f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 350),
        label = "eyeShift"
    )
    val mouthOpen by animateFloatAsState(
        targetValue = when (state) {
            PetAnimationState.OPEN_MOUTH, PetAnimationState.RECEIVING -> 0.8f
            PetAnimationState.EATING_LINK -> 1f
            PetAnimationState.SWALLOWING -> 0.55f
            PetAnimationState.CELEBRATING -> 0.7f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 220),
        label = "mouthOpen"
    )
    val tilt by animateFloatAsState(
        targetValue = when (state) {
            PetAnimationState.CURIOUS -> -6f
            PetAnimationState.CONFUSED -> 8f
            PetAnimationState.CELEBRATING -> 4f
            else -> 0f
        },
        animationSpec = tween(durationMillis = 300),
        label = "tilt"
    )

    val actionActive = state == PetAnimationState.RECEIVING ||
        state == PetAnimationState.EATING_LINK ||
        state == PetAnimationState.SWALLOWING ||
        state == PetAnimationState.CELEBRATING

    val bob = (bobPhase - 0.5f) * 2f
    val verticalOffset = if (actionActive) -pulse * 0.03f else -bob * 0.025f
    val scaleY = if (actionActive) 1f + pulse * 0.08f else 1f + bob * 0.02f
    val scaleX = 1f / scaleY

    val bodyColor = config.bodyColorHex.toPetColor(LifeInboxColors.Sage)
    val eyeColor = config.eyeColorHex.toPetColor(LifeInboxColors.DarkText)
    val accentColor = config.accentColorHex.toPetColor(LifeInboxColors.SoftYellow)

    Canvas(modifier = modifier) {
        val unit = size.minDimension
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = unit * 0.28f

        drawGroundShadow(Offset(cx, cy + radius * 1.02f), radius)
        if (showPlatform) {
            drawPlatform(Offset(cx, cy + radius * 1.0f), radius)
        }

        withTransform({
            translate(0f, verticalOffset * unit)
            scale(scaleX, scaleY, pivot = Offset(cx, cy))
            rotate(tilt, pivot = Offset(cx, cy))
        }) {
            drawBody(config.bodyShape, Offset(cx, cy), radius, bodyColor)
            drawTexture(config.texture, Offset(cx, cy), radius, bodyColor)
            drawEyes(config.eyeStyle, Offset(cx, cy), radius, eyeColor, eyeOpen, eyeShift)
            drawMouth(Offset(cx, cy), radius, mouthOpen, eyeColor)
            drawAccessory(config.accessory, Offset(cx, cy), radius, accentColor)
        }
    }
}

private fun DrawScope.drawGroundShadow(center: Offset, radius: Float) {
    drawOval(
        color = Color(0x22000000),
        topLeft = Offset(center.x - radius * 1.05f, center.y - radius * 0.18f),
        size = Size(radius * 2.1f, radius * 0.36f)
    )
}

private fun DrawScope.drawPlatform(center: Offset, radius: Float) {
    val width = radius * 1.7f
    val height = radius * 0.5f
    drawOval(
        color = LifeInboxColors.DarkText.copy(alpha = 0.82f),
        topLeft = Offset(center.x - width / 2f, center.y - height / 2f),
        size = Size(width, height)
    )
}

private fun DrawScope.drawBody(shape: BodyShape, center: Offset, radius: Float, color: Color) {
    val path = when (shape) {
        BodyShape.ROUND -> Path().apply { addOval(bodyBounds(center, radius, 1f, 1f)) }

        BodyShape.BOXY -> Path().apply {
            addRoundRect(
                androidx.compose.ui.geometry.RoundRect(
                    Rect(
                        center.x - radius * 0.92f,
                        center.y - radius * 0.86f,
                        center.x + radius * 0.92f,
                        center.y + radius * 0.86f
                    ),
                    CornerRadius(radius * 0.34f)
                )
            )
        }

        BodyShape.FLUFFY -> Path().apply {
            addOval(bodyBounds(center, radius, 1f, 1f))
            val bumps = 9
            for (i in 0 until bumps) {
                val angle = (Math.PI * 2.0 * i / bumps).toFloat()
                val bx = center.x + kotlin.math.cos(angle) * radius * 0.92f
                val by = center.y + kotlin.math.sin(angle) * radius * 0.92f
                addOval(bodyBounds(Offset(bx, by), radius * 0.26f, 1f, 1f))
            }
        }

        BodyShape.TRIANGLE -> Path().apply {
            moveTo(center.x, center.y - radius)
            lineTo(center.x + radius, center.y + radius * 0.82f)
            lineTo(center.x - radius, center.y + radius * 0.82f)
            close()
        }

        BodyShape.HEART -> Path().apply {
            moveTo(center.x, center.y + radius)
            cubicTo(
                center.x - radius * 1.45f, center.y - radius * 0.15f,
                center.x - radius * 0.7f, center.y - radius * 1.15f,
                center.x, center.y - radius * 0.42f
            )
            cubicTo(
                center.x + radius * 0.7f, center.y - radius * 1.15f,
                center.x + radius * 1.45f, center.y - radius * 0.15f,
                center.x, center.y + radius
            )
            close()
        }
    }
    drawPath(path, color)
}

private fun DrawScope.drawTexture(
    texture: PetTexture,
    center: Offset,
    radius: Float,
    bodyColor: Color
) {
    val speck = bodyColor.darken(0.18f)
    when (texture) {
        PetTexture.SMOOTH -> Unit

        PetTexture.FLUFFY -> {
            val positions = listOf(
                Offset(-0.5f, 0.35f), Offset(-0.15f, 0.55f), Offset(0.25f, 0.5f),
                Offset(0.55f, 0.25f), Offset(-0.6f, 0.05f), Offset(0.15f, -0.6f)
            )
            positions.forEach { p ->
                drawCircle(
                    color = bodyColor,
                    radius = radius * 0.2f,
                    center = Offset(center.x + p.x * radius, center.y + p.y * radius)
                )
            }
            positions.forEach { p ->
                drawCircle(
                    color = speck.copy(alpha = 0.35f),
                    radius = radius * 0.05f,
                    center = Offset(center.x + p.x * radius, center.y + p.y * radius + radius * 0.08f)
                )
            }
        }

        PetTexture.FUZZY -> {
            val dots = listOf(
                Offset(-0.35f, -0.25f), Offset(0.3f, -0.3f), Offset(-0.1f, 0.1f),
                Offset(0.45f, 0.2f), Offset(-0.45f, 0.25f), Offset(0.05f, -0.5f),
                Offset(0.5f, -0.05f), Offset(-0.2f, 0.45f)
            )
            dots.forEach { p ->
                drawCircle(
                    color = speck,
                    radius = radius * 0.045f,
                    center = Offset(center.x + p.x * radius, center.y + p.y * radius)
                )
            }
        }
    }
}

private fun DrawScope.drawEyes(
    style: EyeStyle,
    center: Offset,
    radius: Float,
    eyeColor: Color,
    open: Float,
    shift: Float
) {
    val eyeY = center.y - radius * 0.16f
    val eyeX = radius * 0.34f
    val eyeRadius = radius * 0.17f
    val dx = shift * radius
    val left = Offset(center.x - eyeX + dx, eyeY)
    val right = Offset(center.x + eyeX + dx, eyeY)

    when (style) {
        EyeStyle.NORMAL -> {
            drawEye(left, eyeRadius, eyeColor, open)
            drawEye(right, eyeRadius, eyeColor, open)
        }

        EyeStyle.SLEEPY -> {
            drawLine(
                color = eyeColor,
                start = Offset(left.x - eyeRadius, left.y),
                end = Offset(left.x + eyeRadius, left.y),
                strokeWidth = eyeRadius * 0.32f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = eyeColor,
                start = Offset(right.x - eyeRadius, right.y),
                end = Offset(right.x + eyeRadius, right.y),
                strokeWidth = eyeRadius * 0.32f,
                cap = StrokeCap.Round
            )
        }

        EyeStyle.HAPPY -> {
            drawArc(
                color = eyeColor,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(left.x - eyeRadius, left.y - eyeRadius * 0.6f),
                size = Size(eyeRadius * 2f, eyeRadius * 1.6f),
                style = Stroke(width = eyeRadius * 0.34f, cap = StrokeCap.Round)
            )
            drawArc(
                color = eyeColor,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(right.x - eyeRadius, right.y - eyeRadius * 0.6f),
                size = Size(eyeRadius * 2f, eyeRadius * 1.6f),
                style = Stroke(width = eyeRadius * 0.34f, cap = StrokeCap.Round)
            )
        }

        EyeStyle.CURIOUS -> {
            drawEye(left, eyeRadius * 1.15f, eyeColor, open)
            drawEye(right, eyeRadius * 0.85f, eyeColor, open)
        }
    }
}

private fun DrawScope.drawEye(center: Offset, radius: Float, color: Color, open: Float) {
    val height = (radius * 2f * open).coerceAtLeast(radius * 0.12f)
    drawOval(
        color = color,
        topLeft = Offset(center.x - radius, center.y - height / 2f),
        size = Size(radius * 2f, height)
    )
    if (open > 0.35f) {
        drawCircle(
            color = Color.White.copy(alpha = 0.85f),
            radius = radius * 0.28f,
            center = Offset(center.x + radius * 0.35f, center.y - height * 0.22f)
        )
    }
}

private fun DrawScope.drawMouth(center: Offset, radius: Float, open: Float, color: Color) {
    val mouthY = center.y + radius * 0.34f
    if (open <= 0.02f) {
        drawArc(
            color = color,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(center.x - radius * 0.22f, mouthY - radius * 0.14f),
            size = Size(radius * 0.44f, radius * 0.32f),
            style = Stroke(width = radius * 0.055f, cap = StrokeCap.Round)
        )
    } else {
        val width = radius * 0.5f
        val height = radius * 0.34f * open + radius * 0.06f
        drawOval(
            color = color,
            topLeft = Offset(center.x - width / 2f, mouthY - height / 2f),
            size = Size(width, height)
        )
        drawOval(
            color = Color(0xFFE2848F),
            topLeft = Offset(center.x - width * 0.3f, mouthY + height * 0.05f),
            size = Size(width * 0.6f, height * 0.42f)
        )
    }
}

private fun DrawScope.drawAccessory(
    accessory: Accessory,
    center: Offset,
    radius: Float,
    color: Color
) {
    when (accessory) {
        Accessory.NONE -> Unit

        Accessory.CROWN -> {
            val top = center.y - radius * 1.02f
            val width = radius * 0.8f
            val path = Path().apply {
                moveTo(center.x - width / 2f, top + radius * 0.28f)
                lineTo(center.x - width / 2f, top)
                lineTo(center.x - width * 0.18f, top + radius * 0.16f)
                lineTo(center.x, top - radius * 0.05f)
                lineTo(center.x + width * 0.18f, top + radius * 0.16f)
                lineTo(center.x + width / 2f, top)
                lineTo(center.x + width / 2f, top + radius * 0.28f)
                close()
            }
            drawPath(path, color)
        }

        Accessory.HORNS -> {
            val top = center.y - radius * 0.9f
            listOf(-1f, 1f).forEach { dir ->
                val path = Path().apply {
                    moveTo(center.x + dir * radius * 0.18f, top + radius * 0.18f)
                    lineTo(center.x + dir * radius * 0.5f, top - radius * 0.35f)
                    lineTo(center.x + dir * radius * 0.6f, top + radius * 0.12f)
                    close()
                }
                drawPath(path, color)
            }
        }

        Accessory.HAT -> {
            val top = center.y - radius * 1.02f
            drawRoundRect(
                color = color,
                topLeft = Offset(center.x - radius * 0.34f, top - radius * 0.34f),
                size = Size(radius * 0.68f, radius * 0.5f),
                cornerRadius = CornerRadius(radius * 0.12f)
            )
            drawRoundRect(
                color = color.darken(0.12f),
                topLeft = Offset(center.x - radius * 0.5f, top + radius * 0.1f),
                size = Size(radius * 1f, radius * 0.14f),
                cornerRadius = CornerRadius(radius * 0.07f)
            )
        }
    }
}

private fun bodyBounds(center: Offset, radius: Float, sx: Float, sy: Float) = Rect(
    center.x - radius * sx,
    center.y - radius * sy,
    center.x + radius * sx,
    center.y + radius * sy
)

private fun Color.darken(factor: Float): Color = Color(
    red = (red * (1f - factor)).coerceIn(0f, 1f),
    green = (green * (1f - factor)).coerceIn(0f, 1f),
    blue = (blue * (1f - factor)).coerceIn(0f, 1f),
    alpha = alpha
)

internal fun String.toPetColor(fallback: Color): Color = try {
    val cleaned = removePrefix("#")
    val rgb = cleaned.toLong(16)
    Color(0xFF000000L or rgb)
} catch (e: Exception) {
    fallback
}
