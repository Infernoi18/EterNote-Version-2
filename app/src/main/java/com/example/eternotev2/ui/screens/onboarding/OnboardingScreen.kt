package com.example.eternotev2.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.eternotev2.ui.theme.AuroraCyan
import com.example.eternotev2.ui.theme.AuroraCyanGlow
import com.example.eternotev2.ui.theme.CosmicViolet
import com.example.eternotev2.ui.theme.CosmicVioletGlow
import com.example.eternotev2.ui.theme.DeepVoid
import com.example.eternotev2.ui.theme.MoodHappyPrimary
import com.example.eternotev2.ui.theme.NebulaPink
import com.example.eternotev2.ui.theme.NebulaPinkGlow
import com.example.eternotev2.ui.theme.TextPrimary
import com.example.eternotev2.ui.theme.TextSecondary
import com.example.eternotev2.ui.theme.TextTertiary

// ── Onboarding page data ──────────────────────────────────────────────────────
data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val description: String,
    val emoji: String,
    val accentColor: Color,
    val glowColor: Color,
    val gradientColors: List<Color>
)

private val onboardingPages = listOf(
    OnboardingPage(
        title        = "Seal Your Memories",
        subtitle     = "Create a time capsule",
        description  = "Write messages, record your voice, and capture your emotions. Lock them away for your future self to discover.",
        emoji        = "🔮",
        accentColor  = CosmicViolet,
        glowColor    = CosmicVioletGlow,
        gradientColors = listOf(CosmicViolet, NebulaPink)
    ),
    OnboardingPage(
        title        = "Feel Every Mood",
        subtitle     = "Emotional time capsules",
        description  = "Tag each capsule with your mood — happy, hopeful, nostalgic, or grateful. Watch your emotional story unfold over time.",
        emoji        = "🌊",
        accentColor  = AuroraCyan,
        glowColor    = AuroraCyanGlow,
        gradientColors = listOf(AuroraCyan, CosmicViolet)
    ),
    OnboardingPage(
        title        = "Rediscover Yourself",
        subtitle     = "Open when the time is right",
        description  = "Set a date. Forget. Then one day, your past self will send you something you needed to hear.",
        emoji        = "✨",
        accentColor  = NebulaPink,
        glowColor    = NebulaPinkGlow,
        gradientColors = listOf(NebulaPink, MoodHappyPrimary)
    )
)

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentPage = onboardingPages[uiState.currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepVoid)
            .pointerInput(uiState.currentPage) {
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount < -50f -> viewModel.nextPage()
                        dragAmount > 50f  -> viewModel.previousPage()
                    }
                }
            }
    ) {

        // ── Ambient background gradient ───────────────────────────────────────
        AnimatedContent(
            targetState = uiState.currentPage,
            transitionSpec = {
                fadeIn(tween(600)) togetherWith fadeOut(tween(600))
            },
            label = "bgGradient"
        ) { page ->
            val pageData = onboardingPages[page]
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            pageData.glowColor.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height * 0.35f),
                        radius = size.width * 0.8f
                    ),
                    radius = size.width * 0.8f,
                    center = Offset(size.width / 2f, size.height * 0.35f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Skip ──────────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (!uiState.isLastPage) {
                    Text(
                        text     = "Skip",
                        color    = TextTertiary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.completeOnboarding(onOnboardingComplete)
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Animated illustration ─────────────────────────────────────────
            AnimatedContent(
                targetState = uiState.currentPage,
                transitionSpec = {
                    (slideInHorizontally(tween(400)) { it / 4 } + fadeIn(tween(400))) togetherWith
                    (slideOutHorizontally(tween(400)) { -it / 4 } + fadeOut(tween(400)))
                },
                label = "illustration"
            ) { page ->
                OnboardingIllustration(
                    page  = onboardingPages[page],
                    modifier = Modifier.size(240.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Page dot indicators ───────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(uiState.totalPages) { index ->
                    OnboardingDot(
                        isActive     = index == uiState.currentPage,
                        accentColor  = currentPage.accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Text content ──────────────────────────────────────────────────
            AnimatedContent(
                targetState = uiState.currentPage,
                transitionSpec = {
                    (slideInHorizontally(tween(450)) { it / 3 } + fadeIn(tween(450))) togetherWith
                    (slideOutHorizontally(tween(350)) { -it / 3 } + fadeOut(tween(350)))
                },
                label = "pageText"
            ) { page ->
                val pageData = onboardingPages[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text      = pageData.subtitle.uppercase(),
                        fontSize  = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 3.sp,
                        color     = pageData.accentColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text       = pageData.title,
                        fontSize   = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = TextPrimary,
                        textAlign  = TextAlign.Center,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text      = pageData.description,
                        fontSize  = 15.sp,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── CTA Button ────────────────────────────────────────────────────
            OnboardingButton(
                isLastPage  = uiState.isLastPage,
                accentColor = currentPage.accentColor,
                glowColor   = currentPage.glowColor,
                gradientColors = currentPage.gradientColors,
                isLoading   = uiState.isCompleting,
                onClick = {
                    if (uiState.isLastPage) {
                        viewModel.completeOnboarding(onOnboardingComplete)
                    } else {
                        viewModel.nextPage()
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Onboarding Illustration ───────────────────────────────────────────────────
@Composable
private fun OnboardingIllustration(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0.92f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val glowAlpha by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue  = 0.7f,
        animationSpec = infiniteRepeatable(
            animation  = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.scale(pulse)
    ) {
        // Outer glow ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        page.accentColor.copy(alpha = glowAlpha * 0.4f),
                        Color.Transparent
                    )
                ),
                radius = size.minDimension / 2f
            )
            // Glass circle border
            drawCircle(
                color  = page.accentColor.copy(alpha = 0.2f),
                radius = size.minDimension / 2f * 0.75f,
                style  = Stroke(width = 1.5.dp.toPx())
            )
            // Inner gradient fill
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        page.glowColor.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                ),
                radius = size.minDimension / 2f * 0.7f
            )
        }

        // Emoji in center
        Text(
            text     = page.emoji,
            fontSize = 72.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ── Page dot indicator ────────────────────────────────────────────────────────
@Composable
private fun OnboardingDot(
    isActive: Boolean,
    accentColor: Color
) {
    val width by animateDpAsState(
        targetValue = if (isActive) 24.dp else 6.dp,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "dotWidth"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.35f,
        animationSpec = tween(300),
        label = "dotAlpha"
    )
    Box(
        modifier = Modifier
            .width(width)
            .height(6.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(
                if (isActive) accentColor else TextTertiary
            )
    )
}

// ── CTA Button ────────────────────────────────────────────────────────────────
@Composable
private fun OnboardingButton(
    isLastPage: Boolean,
    accentColor: Color,
    glowColor: Color,
    gradientColors: List<Color>,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val scaleAnim = remember { Animatable(1f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scaleAnim.value)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(gradientColors)
            )
            .clickable(enabled = !isLoading) {
                onClick()
            }
    ) {
        // Glow layer underneath
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.4f),
                        Color.Transparent,
                        glowColor.copy(alpha = 0.4f)
                    )
                )
            )
        }

        Text(
            text       = if (isLastPage) "Begin Your Journey" else "Continue",
            color      = Color.White,
            fontSize   = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
