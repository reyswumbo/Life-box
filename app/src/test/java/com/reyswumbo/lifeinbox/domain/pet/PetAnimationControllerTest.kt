package com.reyswumbo.lifeinbox.domain.pet

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class PetAnimationControllerTest {

    private val fastTiming = PetAnimationController.Timing(
        idleMinMs = 100,
        idleJitterMs = 0,
        blinkMs = 150,
        lookMs = 150,
        curiousMs = 150,
        defaultActionMs = 300
    )

    @Test
    fun startsIdleAndInvisible() = runTest {
        val controller = PetAnimationController(this, Random(1), fastTiming)
        assertEquals(PetAnimationState.IDLE, controller.state.value)
    }

    @Test
    fun idleLoopProducesExpressionsWhenVisible() = runTest {
        val controller = PetAnimationController(this, Random(7), fastTiming)
        try {
            controller.setVisible(true)

            advanceTimeBy(120)
            runCurrent()

            assertTrue(
                controller.state.value in setOf(
                    PetAnimationState.BLINKING,
                    PetAnimationState.LOOKING,
                    PetAnimationState.CURIOUS
                )
            )
        } finally {
            controller.stopAll()
        }
    }

    @Test
    fun triggerShowsStateThenReturnsToIdle() = runTest {
        val controller = PetAnimationController(this, Random(3), fastTiming)
        try {
            controller.setVisible(true)

            controller.trigger(PetAnimationState.RECEIVING, durationMs = 300)
            runCurrent()
            assertEquals(PetAnimationState.RECEIVING, controller.state.value)

            advanceTimeBy(320)
            runCurrent()
            assertEquals(PetAnimationState.IDLE, controller.state.value)
        } finally {
            controller.stopAll()
        }
    }

    @Test
    fun newTriggerCancelsPreviousAction() = runTest {
        val controller = PetAnimationController(this, Random(5), fastTiming)
        try {
            controller.setVisible(true)

            controller.trigger(PetAnimationState.RECEIVING, durationMs = 1000)
            runCurrent()
            controller.trigger(PetAnimationState.CELEBRATING, durationMs = 1000)
            runCurrent()

            assertEquals(PetAnimationState.CELEBRATING, controller.state.value)

            advanceTimeBy(1010)
            runCurrent()
            assertEquals(PetAnimationState.IDLE, controller.state.value)
        } finally {
            controller.stopAll()
        }
    }

    @Test
    fun playSequenceRunsStepsInOrder() = runTest {
        val controller = PetAnimationController(this, Random(9), fastTiming)
        try {
            controller.setVisible(true)

            controller.playSequence(
                listOf(
                    PetAnimationState.RECEIVING to 100,
                    PetAnimationState.EATING_LINK to 100,
                    PetAnimationState.SWALLOWING to 100,
                    PetAnimationState.CELEBRATING to 100
                )
            )

            runCurrent()
            assertEquals(PetAnimationState.RECEIVING, controller.state.value)

            advanceTimeBy(110)
            runCurrent()
            assertEquals(PetAnimationState.EATING_LINK, controller.state.value)

            advanceTimeBy(100)
            runCurrent()
            assertEquals(PetAnimationState.SWALLOWING, controller.state.value)

            advanceTimeBy(100)
            runCurrent()
            assertEquals(PetAnimationState.CELEBRATING, controller.state.value)

            advanceTimeBy(100)
            runCurrent()
            assertEquals(PetAnimationState.IDLE, controller.state.value)
        } finally {
            controller.stopAll()
        }
    }

    @Test
    fun triggerIsIgnoredWhenNotVisible() = runTest {
        val controller = PetAnimationController(this, Random(2), fastTiming)
        controller.trigger(PetAnimationState.CELEBRATING, durationMs = 100)
        runCurrent()
        assertEquals(PetAnimationState.IDLE, controller.state.value)
    }

    @Test
    fun stopAllResetsStateAndHaltsIdle() = runTest {
        val controller = PetAnimationController(this, Random(11), fastTiming)
        controller.setVisible(true)

        controller.trigger(PetAnimationState.EATING_LINK, durationMs = 1000)
        runCurrent()
        controller.stopAll()
        assertEquals(PetAnimationState.IDLE, controller.state.value)

        advanceTimeBy(1000)
        runCurrent()
        assertEquals(PetAnimationState.IDLE, controller.state.value)
    }

    @Test
    fun hidingStopsIdleWork() = runTest {
        val controller = PetAnimationController(this, Random(13), fastTiming)
        controller.setVisible(true)
        controller.setVisible(false)

        advanceTimeBy(5000)
        runCurrent()
        assertEquals(PetAnimationState.IDLE, controller.state.value)
        assertTrue(!controller.isVisible)
    }
}
