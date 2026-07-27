package com.ralphmarondev.velora.features.dashboard.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ralphmarondev.velora.R
import com.ralphmarondev.velora.core.domain.model.TrafficRecord
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreenRoot(
    navigateToProfile: () -> Unit
) {
    val viewModel: DashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateToProfile) {
        if (state.navigateToProfile) {
            navigateToProfile()
            viewModel.onAction(DashboardAction.ClearNavigation)
        }
    }

    DashboardScreen(
        state = state,
        action = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    action: (DashboardAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard"
                    )
                },
                actions = {
                    state.imagePath?.let { imagePath ->
                        val profileImagePath = when (imagePath) {
                            1 -> R.drawable.profile_2
                            2 -> R.drawable.profile_3
                            3 -> R.drawable.profile_4
                            4 -> R.drawable.profile_4
                            5 -> R.drawable.profile_5
                            else -> R.drawable.profile_2
                        }
                        IconButton(onClick = { action(DashboardAction.NavigateToProfile) }) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                                        shape = CircleShape
                                    )
                                    .padding(2.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(profileImagePath),
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { action(DashboardAction.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TrafficStatusCard(trafficRecord = state.trafficRecord)
                }
                item {
                    ConstructionStatusCard(trafficRecord = state.trafficRecord)
                }
                item {
                    LastUpdatedCard(
                        timestamp = state.trafficRecord.timestamp
                    )
                }
            }
        }
    }
}

@Composable
fun TrafficStatusCard(
    trafficRecord: TrafficRecord
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Traffic Status",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (trafficRecord.isCongested)
                    "🚦 Heavy Traffic Detected"
                else
                    "✅ Road is Clear",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun ConstructionStatusCard(
    trafficRecord: TrafficRecord
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Road Construction",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (trafficRecord.isUnderConstruction)
                    "🚧 Ongoing Road Construction"
                else
                    "✅ No Ongoing Construction",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun LastUpdatedCard(
    timestamp: Long
) {
    val formatter = remember {
        SimpleDateFormat(
            "MMM dd, yyyy • hh:mm:ss a",
            Locale.getDefault()
        )
    }

    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Last Updated",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                formatter.format(Date(timestamp)),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}