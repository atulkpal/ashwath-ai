package com.ashwathai.ashwathai.features.chat.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.theme.CyanPrimary
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
                val application = extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!!
                return ChatViewModel(
                    ServiceLocator.provideInferenceEngine()
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
            TopAppBar(
                title = {
                    Column {
                        Text("Ashwath AI", style = MaterialTheme.typography.titleMedium)
                        Text(state.activeModelName, style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ChatEvent.ClearChat) }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                    items(state.messages) { message ->
                        ChatBubble(message)
                    }
                    if (state.isTyping) {
                        item { TypingIndicator() }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
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
fun EngineStatusOverlay(status: EngineStatus, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            when (status) {
                is EngineStatus.Initializing -> {
                    CircularProgressIndicator(color = CyanPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Starting engine...", color = Color.White)
                }
                is EngineStatus.Error -> {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Engine Error", color = Color.Red, style = MaterialTheme.typography.titleLarge)
                    Text(status.message, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)) {
                        Text("Retry", color = Color.Black)
                    }
                }
                is EngineStatus.Connected -> {}
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isAi = message.sender == Sender.AI
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isAi) 0.dp else 12.dp,
                        bottomEnd = if (isAi) 12.dp else 0.dp
                    )
                )
                .background(if (isAi) SurfaceTier1 else CyanPrimary)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isAi) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Box(
        modifier = Modifier
            .background(SurfaceTier1, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text("Thinking...", style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
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

            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                enabled = enabled,
                placeholder = { Text("Ask anything...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SurfaceTier2,
                    unfocusedContainerColor = SurfaceTier2,
                    disabledContainerColor = SurfaceTier1,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    disabledTextColor = Color.Gray
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = enabled && text.isNotBlank(),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (enabled && text.isNotBlank()) CyanPrimary else Color.DarkGray)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (enabled && text.isNotBlank()) Color.Black else Color.Gray
                )
            }
        }
    }
}
