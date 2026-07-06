package com.ashwathai.ashwathai.features.download.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ashwathai.ashwathai.app.theme.CyanPrimary
import com.ashwathai.ashwathai.app.theme.JetBrainsMonoFontFamily
import com.ashwathai.ashwathai.app.theme.OnSurfaceVariant
import com.ashwathai.ashwathai.app.theme.SurfaceTier1
import com.ashwathai.ashwathai.app.theme.SurfaceTier2
import com.ashwathai.ashwathai.domain.models.ModelInfo
import com.ashwathai.ashwathai.features.download.LocalFileImporter
import com.ashwathai.ashwathai.features.download.viewmodel.DownloadViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = viewModel(),
    onNavigateToChat: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val modelsDir = remember { java.io.File(context.filesDir, "models") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val file = LocalFileImporter.importModel(context, uri, modelsDir)
                if (file != null) {
                    snackbarHostState.showSnackbar("Imported: ${file.name}")
                    viewModel.loadModels()
                } else {
                    snackbarHostState.showSnackbar("Failed to import model")
                }
            }
        }
    }

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onNavigateToChat()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isComplete) "Ready" else "Download a Model",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                ),
            )
        },
        containerColor = Color.Black,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyanPrimary, strokeWidth = 2.dp)
                }
            } else if (state.error != null && state.availableModels.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(56.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error ?: "Error loading models",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.loadModels() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    ) {
                        Text("Retry", color = Color.Black)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Text(
                            text = "Choose a model to download:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }

                    items(state.availableModels, key = { it.id }) { model ->
                        DownloadModelCard(
                            model = model,
                            isDownloading = state.downloadingModelId == model.id,
                            progress = if (state.downloadingModelId == model.id) state.downloadProgress else 0f,
                            isComplete = state.isComplete,
                            onDownload = { viewModel.downloadModel(model.id) },
                        )
                    }

                    item {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = SurfaceTier2,
                        )
                    }
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { filePickerLauncher.launch(arrayOf("application/octet-stream", "*/*")) },
                            color = SurfaceTier1,
                            shape = RoundedCornerShape(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(CyanPrimary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Import from device",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                        color = Color.White,
                                    )
                                    Text(
                                        "Select a .gguf file from storage",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = OnSurfaceVariant,
                                    )
                                }
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Import",
                                    tint = CyanPrimary,
                                )
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }

            if (state.downloadingModelId != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black)
                        .padding(16.dp),
                ) {
                    Column {
                        Text(
                            text = "Downloading... ${(state.downloadProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                            ),
                            color = CyanPrimary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { state.downloadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(CircleShape),
                            color = CyanPrimary,
                            trackColor = SurfaceTier2,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadModelCard(
    model: ModelInfo,
    isDownloading: Boolean,
    progress: Float,
    isComplete: Boolean,
    onDownload: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceTier1),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = model.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(model.size, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(model.parameters, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text(model.provider, style = MaterialTheme.typography.labelSmall, color = CyanPrimary)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            when {
                isComplete -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = CyanPrimary,
                        modifier = Modifier.size(32.dp),
                    )
                }
                isDownloading -> {
                    CircularProgressIndicator(
                        progress = { progress },
                        color = CyanPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(32.dp),
                    )
                }
                else -> {
                    FilledIconButton(
                        onClick = onDownload,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = CyanPrimary,
                            contentColor = Color.Black,
                        ),
                        modifier = Modifier.size(40.dp),
                    ) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = "Download",
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}
