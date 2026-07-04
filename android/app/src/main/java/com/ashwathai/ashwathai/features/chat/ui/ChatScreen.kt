package com.ashwathai.ashwathai.features.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.ashwathai.ashwathai.di.ServiceLocator
import com.ashwathai.ashwathai.domain.models.ChatMessage
import com.ashwathai.ashwathai.domain.models.Sender
import com.ashwathai.ashwathai.features.chat.events.ChatEvent
import com.ashwathai.ashwathai.features.chat.state.EngineStatus
import com.ashwathai.ashwathai.features.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.messages.size, state.messages.lastOrNull()?.text) {
        if (state.messages.isNotEmpty()) {
            scrollState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            AshwathTopBar(
                title = "Ashwath AI",
                subtitle = if (state.engineStatus is EngineStatus.Connected) state.activeModelName else "Engine Offline",
                actions = {
                    if (state.messages.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onEvent(ChatEvent.ClearChat) }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear")
                        }
                    }
                }
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (state.messages.isEmpty() && state.engineStatus is EngineStatus.Connected) {
                    Box(modifier = Modifier.weight(1f)) {
                        EmptyChatContent(
                            onSuggestionClick = { viewModel.onEvent(ChatEvent.InputChanged(it)) }
                        )
                    }
                } else {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                        items(state.messages) { message ->
                            if (message.text.isNotBlank() || message.sender == Sender.AI) {
                                AshwathChatBubble(
                                    text = message.text,
                                    isAi = message.sender == Sender.AI
                                )
                            }
                        }
                        if (state.isTyping) {
                            item { TypingIndicator() }
                        }
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }

                ChatInputBar(
                    text = state.inputText,
                    onTextChange = { viewModel.onEvent(ChatEvent.InputChanged(it)) },
                    onSend = { viewModel.onEvent(ChatEvent.SendMessage) },
                    enabled = state.engineStatus is EngineStatus.Connected
                )
            }

            if (state.engineStatus !is EngineStatus.Connected) {
                EngineStatusOverlay(state.engineStatus, onRetry = { viewModel.onEvent(ChatEvent.RetryEngine) })
            }
        }
    }
}

@Composable
fun EmptyChatContent(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = CyanPrimary.copy(alpha = 0.5f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Ready to assist",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )
        Text(
            "Your conversations are local and private.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            "TRY ASKING",
            style = MaterialTheme.typography.labelSmall,
            color = CyanPrimary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val suggestions = listOf(
            "Write a Python script for data analysis",
            "Explain quantum computing like I'm five",
            "Help me plan a 3-day trip to Tokyo"
        )

        suggestions.forEach { suggestion ->
            AshwathCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = { onSuggestionClick(suggestion) }
            ) {
                Text(
                    text = suggestion,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EngineStatusOverlay(status: EngineStatus, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            when (status) {
                is EngineStatus.Initializing -> {
                    CircularProgressIndicator(color = CyanPrimary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Initializing Ashwath AI",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        "Starting local inference engine...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                is EngineStatus.NoModelInstalled -> {
                    Icon(
                        Icons.Default.DownloadForOffline,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "No Models Found",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        "Download a model to start chatting offline.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AshwathPrimaryButton(
                        text = "EXPLORE MODELS",
                        onClick = { /* Navigation handled via BottomBar */ }
                    )
                }
                is EngineStatus.Error -> {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Engine Failure",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Red
                    )
                    Text(
                        status.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AshwathPrimaryButton(
                        text = "RETRY INITIALIZATION",
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(32.dp)
                .background(
                    color = SurfaceTier2,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(CyanPrimary, CircleShape)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "AI is thinking...",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        color = SurfaceTier1,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Mock */ }, enabled = enabled) {
                Icon(Icons.Default.Add, contentDescription = "Attach", tint = if (enabled) Color.Gray else Color.DarkGray)
            }

            AshwathTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                placeholder = "Ask anything..."
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = enabled && text.isNotBlank(),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (enabled && text.isNotBlank()) CyanPrimary else Color.DarkGray)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (enabled && text.isNotBlank()) PureBlack else Color.Gray
                )
            }
        }
    }
}
