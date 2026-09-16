package com.example.eternotev2.ui.screens.home

import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.local.entity.UserEntity
import com.example.eternotev2.data.model.Capsule
import com.example.eternotev2.data.model.CapsuleType
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.data.repository.UserRepository
import com.example.eternotev2.ui.theme.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val userRepository: UserRepository = mock()
    private val capsuleRepository: CapsuleRepository = mock()
    private val sessionManager: SessionManager = mock()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(sessionManager.userIdFlow).thenReturn(MutableStateFlow("test@example.com"))
        whenever(userRepository.getUserByEmailFlow("test@example.com")).thenReturn(flowOf(
            UserEntity(email = "test@example.com", username = "Traveler", password = "123")
        ))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testUpcomingBirthdayCapsuleIdentified() = runTest {
        val normalCapsule = Capsule(
            id = 1,
            userId = "test@example.com",
            title = "Normal Memory",
            message = "Hello",
            mood = Mood.HAPPY,
            createdAt = System.currentTimeMillis(),
            unlockAt = System.currentTimeMillis() + 100000,
            isUnlocked = false,
            isCoreMemory = false,
            isFavorite = false,
            hasVoiceNote = false,
            imageUri = null,
            unlockMessage = null,
            capsuleType = CapsuleType.NORMAL,
            tags = emptyList(),
            workRequestId = null
        )

        val birthdayCapsule = Capsule(
            id = 2,
            userId = "test@example.com",
            title = "Birthday Memory",
            message = "Happy Birthday!",
            mood = Mood.HAPPY,
            createdAt = System.currentTimeMillis(),
            unlockAt = System.currentTimeMillis() + 50000,
            isUnlocked = false,
            isCoreMemory = false,
            isFavorite = false,
            hasVoiceNote = false,
            imageUri = null,
            unlockMessage = null,
            capsuleType = CapsuleType.BIRTHDAY_SELF,
            tags = emptyList(),
            workRequestId = null
        )

        whenever(capsuleRepository.getAllCapsules()).thenReturn(flowOf(listOf(normalCapsule, birthdayCapsule)))

        viewModel = HomeViewModel(userRepository, capsuleRepository, sessionManager)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertNotNull(uiState.upcomingBirthdayCapsule)
        assertEquals(2L, uiState.upcomingBirthdayCapsule?.id)
    }

    @Test
    fun testNoUpcomingBirthdayCapsule() = runTest {
        val normalCapsule = createCapsule(1, CapsuleType.NORMAL, 100000)

        whenever(capsuleRepository.getAllCapsules()).thenReturn(flowOf(listOf(normalCapsule)))

        viewModel = HomeViewModel(userRepository, capsuleRepository, sessionManager)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertNull(uiState.upcomingBirthdayCapsule)
    }

    @Test
    fun testUnlockedBirthdayCapsuleNotIdentified() = runTest {
        val birthdayCapsule = createCapsule(1, CapsuleType.BIRTHDAY_SELF, -10000).copy(isUnlocked = true)

        whenever(capsuleRepository.getAllCapsules()).thenReturn(flowOf(listOf(birthdayCapsule)))

        viewModel = HomeViewModel(userRepository, capsuleRepository, sessionManager)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertNull(uiState.upcomingBirthdayCapsule)
    }

    @Test
    fun testMultipleBirthdayCapsulesEarliestSelected() = runTest {
        val farBirthday = createCapsule(1, CapsuleType.BIRTHDAY_OTHER, 200000)
        val soonBirthday = createCapsule(2, CapsuleType.BIRTHDAY_SELF, 50000)

        whenever(capsuleRepository.getAllCapsules()).thenReturn(flowOf(listOf(farBirthday, soonBirthday)))

        viewModel = HomeViewModel(userRepository, capsuleRepository, sessionManager)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(2L, uiState.upcomingBirthdayCapsule?.id)
    }

    private fun createCapsule(id: Long, type: CapsuleType, unlockInMs: Long) = Capsule(
        id = id,
        userId = "test@example.com",
        title = "Memory $id",
        message = "Hello",
        mood = Mood.HAPPY,
        createdAt = System.currentTimeMillis(),
        unlockAt = System.currentTimeMillis() + unlockInMs,
        isUnlocked = false,
        isCoreMemory = false,
        isFavorite = false,
        hasVoiceNote = false,
        imageUri = null,
        unlockMessage = null,
        capsuleType = type,
        tags = emptyList(),
        workRequestId = null
    )
}
