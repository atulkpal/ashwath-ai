package com.ashwathai.ashwathai.features.chat.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.components.*
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.PureBlack
import com.ashwathai.ashwathai.app.theme.SurfaceTier1
import com.ashwathai.ashwathai.app.theme.SurfaceTier2
import com.ashwathai.ashwathai.app.theme.BorderSubtle
import com.ashwathai.ashwathai.app.theme.OnSurfaceVariant
import com.ashwathai.ashwathai.app.theme.JetBrainsMonoFontFamily
import com.ashwathai.ashwathai.di.ServiceLocator
import com.ashwathai.ashwathai.domain.models.Sender
import com.ashwathai.ashwathai.features.chat.events.ChatEvent
import com.ashwathai.ashwathai.features.chat.state.EngineStatus
import com.ashwathai.ashwathai.features.chat.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateToDownload: () -> Unit = {},
    viewModel: ChatViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return ChatViewModel(
                    ServiceLocator.provideInferenceEngine(),
                ) as T
            }
        }
    )
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberLazyListState()

    // Auto-scroll to bottom on new messages or streaming updates
    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.text) {
        if (state.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(state.messages.size)
        }
    }

    Scaffold(
        topBar = {
            AshwathTopBar(
                title = "Ashwath AI",
                subtitle = when (state.engineStatus) {
                    is EngineStatus.Connected -> state.activeModelName
                    is EngineStatus.Initializing -> "Connecting to Engine..."
                    is EngineStatus.Error -> "Engine Offline"
                    is EngineStatus.NoModelInstalled -> "No Models Found"
                },
                subtitleColor = when (state.engineStatus) {
                    is EngineStatus.Connected, is EngineStatus.Initializing -> CyanPrimary
                    else -> OnSurfaceVariant
                },
                modifier = Modifier.semantics {
                    liveRegion = LiveRegionMode.Polite
                },
                actions = {
                    if (state.messages.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onEvent(ChatEvent.ClearChat) },
                            modifier = Modifier.semantics { contentDescription = "Clear Chat" }
                        ) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = null)
                        }
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    if (state.messages.isEmpty() && state.engineStatus is EngineStatus.Connected) {
                        EmptyChatContent(
                            onSuggestionClick = { viewModel.onEvent(ChatEvent.InputChanged(it)) }
                        )
                    } else {
                        LazyColumn(
                            state = scrollState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.messages,
                                key = { it.id }
                            ) { message ->
                                if (message.text.isNotBlank() || message.sender == Sender.AI) {
                                    AshwathChatBubble(
                                        text = message.text,
                                        isAi = message.sender == Sender.AI
                                    )
                                }
                            }

                            if (state.isTyping) {
                                item(key = "typing_indicator") {
                                    TypingIndicator()
                                }
                            }

                            // Extra space at bottom to ensure input bar doesn't cover last message
                            item { Spacer(modifier = Modifier.height(8.dp)) }
                        }
                    }
                }

                ChatInputBar(
                    text = state.inputText,
                    onTextChange = { viewModel.onEvent(ChatEvent.InputChanged(it)) },
                    onSend = { viewModel.onEvent(ChatEvent.SendMessage) },
                    enabled = state.engineStatus is EngineStatus.Connected,
                    isTyping = state.isTyping
                )
            }

            AnimatedVisibility(
                visible = state.engineStatus !is EngineStatus.Connected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EngineStatusOverlay(
                    status = state.engineStatus,
                    onRetry = { viewModel.onEvent(ChatEvent.RetryEngine) },
                    onExploreModels = onNavigateToDownload,
                )
            }
        }
    }
}

@Composable
fun EmptyChatContent(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanPrimary.copy(alpha = 0.2f), Color.Transparent)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome to Ashwath AI",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            ),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Private, local, and powerful inference directly on your device.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "TRY ASKING",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp
            ),
            color = CyanPrimary.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val suggestions = remember {
            listOf(
                "Write a Python script for data analysis",
                "Explain quantum computing simply",
                "What are the benefits of local AI?"
            )
        }

        suggestions.forEach { suggestion ->
            AshwathCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                onClick = { onSuggestionClick(suggestion) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = CyanPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = suggestion,
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun EngineStatusOverlay(status: EngineStatus, onRetry: () -> Unit, onExploreModels: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceTier1)
                .padding(32.dp)
        ) {
            when (status) {
                is EngineStatus.Initializing -> {
                    CircularProgressIndicator(
                        color = CyanPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "Initializing Engine",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Bootstrapping local intelligence...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                is EngineStatus.NoModelInstalled -> {
                    Icon(
                        Icons.Default.DownloadForOffline,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        "No Models Found",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "You need to download a model to use Ashwath AI offline.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    AshwathPrimaryButton(
                        text = "DOWNLOAD A MODEL",
                        onClick = onExploreModels,
                    )
                }
                is EngineStatus.Error -> {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Engine Error",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFFF5252)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        status.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AshwathPrimaryButton(
                        text = "RETRY",
                        onClick = onRetry
                    )
                }
                is EngineStatus.Connected -> {}
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                .background(SurfaceTier1)
                .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val delay = index * 200
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = keyframes {
                                durationMillis = 1000
                                0.2f at delay
                                1.0f at delay + 300
                                0.2f at delay + 600
                            },
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .alpha(alpha)
                            .background(CyanPrimary, CircleShape)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "AI is thinking",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = JetBrainsMonoFontFamily // Assuming it's available or mapped
            ),
            color = OnSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean = true,
    isTyping: Boolean = false
) {
    Surface(
        color = Color.Black,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)

            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* TODO: Attachments */ },
                    enabled = enabled && !isTyping
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = if (enabled) OnSurfaceVariant else Color.DarkGray
                    )
                }

                AshwathTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    placeholder = "Message Ashwath AI..."
                )

                Spacer(modifier = Modifier.width(8.dp))

                val sendEnabled = enabled && text.isNotBlank() && !isTyping

                IconButton(
                    onClick = onSend,
                    enabled = sendEnabled,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (sendEnabled) CyanPrimary else SurfaceTier2)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (sendEnabled) PureBlack else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
