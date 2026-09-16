package com.example.eternotev2.ui.screens.capsule

import android.content.Context
import com.example.eternotev2.data.auth.SessionManager
import com.example.eternotev2.data.local.entity.UserEntity
import com.example.eternotev2.data.model.CapsuleType
import com.example.eternotev2.data.repository.CapsuleRepository
import com.example.eternotev2.data.repository.UserRepository
import com.example.eternotev2.util.VoiceRecorder
import com.example.eternotev2.worker.CapsuleWorkerScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class CreateCapsuleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val userRepository: UserRepository = mock()
    private val capsuleRepository: CapsuleRepository = mock()
    private val voiceRecorder: VoiceRecorder = mock()
    private val workerScheduler: CapsuleWorkerScheduler = mock()
    private val sessionManager: SessionManager = mock()
    private val context: Context = mock()
    private lateinit var viewModel: CreateCapsuleViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(sessionManager.userIdFlow).thenReturn(MutableStateFlow("test@example.com"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCalculateNextBirthdayUnlock_FutureDate() = runTest {
        val email = "test@example.com"
        whenever(sessionManager.getCurrentUserId()).thenReturn(email)

        val birthCalendar = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.OCTOBER)
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.YEAR, 1995)
        }
        val user = UserEntity(email = email, username = "Test", password = "123", birthDate = birthCalendar.timeInMillis)
        whenever(userRepository.getUserByEmail(email)).thenReturn(user)

        viewModel = CreateCapsuleViewModel(
            userRepository,
            capsuleRepository,
            voiceRecorder,
            workerScheduler,
            sessionManager,
            context
        )
        advanceUntilIdle()

        viewModel.onCapsuleTypeChanged(CapsuleType.BIRTHDAY_SELF)

        val uiState = viewModel.uiState.value
        assertEquals(CapsuleType.BIRTHDAY_SELF, uiState.capsuleType)

        val targetCalendar = Calendar.getInstance()
        targetCalendar.set(Calendar.MONTH, Calendar.OCTOBER)
        targetCalendar.set(Calendar.DAY_OF_MONTH, 15)
        if (targetCalendar.timeInMillis < System.currentTimeMillis()) {
            targetCalendar.add(Calendar.YEAR, 1)
        }

        val diff = Math.abs(uiState.unlockAt - targetCalendar.timeInMillis)
        assertTrue("Unlock time calculation mismatch", diff < 5000)
    }
}
