package com.reyswumbo.lifeinbox.domain.pet

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Centralized pet animation controller.
 *
 * The controller owns the current [PetAnimationState] and guarantees that
 * only one animation runs at a time. Idle behavior (blinking, looking,
 * occasional curious reactions) is generated internally while visible;
 * externally triggered actions (saving, eating, celebrating) pre-empt the
 * idle loop and return the pet to [PetAnimationState.IDLE] afterwards.
 */
class PetAnimationController(
    private val scope: CoroutineScope,
    private val random: Random = Random.Default,
    private val timing: Timing = Timing()
) {

    data class Timing(
        val idleMinMs: Long = 2_200,
        val idleJitterMs: Long = 3_200,
        val blinkMs: Long = 150,
        val lookMs: Long = 900,
        val curiousMs: Long = 1_300,
        val defaultActionMs: Long = 900
    )

    private val _state = MutableStateFlow(PetAnimationState.IDLE)
    val state: StateFlow<PetAnimationState> = _state.asStateFlow()

    private var idleJob: Job? = null
    private var actionJob: Job? = null

    var isVisible: Boolean = false
        private set

    fun setVisible(visible: Boolean) {
        if (visible == isVisible) return
        isVisible = visible
        if (visible) startIdle() else stopAll()
    }

    /** Plays a one-shot animation, replacing any action already in flight. */
    fun trigger(state: PetAnimationState, durationMs: Long = timing.defaultActionMs) {
        if (!isVisible) return
        playSequence(listOf(state to durationMs))
    }

    /** Plays a coordinated sequence of animation states, cancelling any previous action. */
    fun playSequence(steps: List<Pair<PetAnimationState, Long>>) {
        if (!isVisible || steps.isEmpty()) return
        actionJob?.cancel()
        actionJob = scope.launch {
            for ((animationState, duration) in steps) {
                _state.value = animationState
                delay(duration)
            }
            returnToIdle()
        }
    }

    fun stopAll() {
        idleJob?.cancel()
        idleJob = null
        actionJob?.cancel()
        actionJob = null
        _state.value = PetAnimationState.IDLE
    }

    private fun startIdle() {
        if (idleJob?.isActive == true) return
        idleJob = scope.launch { idleLoop() }
    }

    private suspend fun idleLoop() {
        while (currentCoroutineContext().isActive) {
            delay(timing.idleMinMs + random.nextLong(timing.idleJitterMs + 1))
            if (actionJob?.isActive == true) continue
            if (_state.value != PetAnimationState.IDLE) continue
            when (random.nextInt(3)) {
                0 -> expression(PetAnimationState.BLINKING, timing.blinkMs)
                1 -> expression(PetAnimationState.LOOKING, timing.lookMs)
                else -> expression(PetAnimationState.CURIOUS, timing.curiousMs)
            }
        }
    }

    private suspend fun expression(state: PetAnimationState, durationMs: Long) {
        _state.value = state
        delay(durationMs)
        returnToIdle(state)
    }

    private fun returnToIdle(expected: PetAnimationState? = null) {
        if (expected == null || _state.value == expected) {
            _state.value = PetAnimationState.IDLE
        }
    }
}
