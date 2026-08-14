package com.ralphmarondev.velora.features.dashboard.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.Traffic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ralphmarondev.velora.R
import com.ralphmarondev.velora.core.domain.model.TrafficRecord
import com.ralphmarondev.velora.features.dashboard.presentation.component.GoogleMapComponent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreenRoot(
    navigateToProfile: () -> Unit,
    navigateToCalendar: () -> Unit
) {
    val viewModel: DashboardViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateToProfile) {
        if (state.navigateToProfile) {
            navigateToProfile()
            viewModel.onAction(DashboardAction.ClearNavigation)
        }
    }

    LaunchedEffect(state.navigateToCalendar) {
        if (state.navigateToCalendar) {
            navigateToCalendar()
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
private fun DashboardScreen(
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
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { action(DashboardAction.NavigateToCalendar) }) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = "Schedule trip"
                )
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { action(DashboardAction.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TrafficConditionCard(
                        modifier = Modifier.weight(1f),
                        trafficRecord = state.trafficRecord
                    )
                    RoadConstructionCard(
                        modifier = Modifier.weight(1f),
                        trafficRecord = state.trafficRecord
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                GoogleMapComponent(
                    isTraffic = state.trafficRecord.isCongested,
                    isUnderConstruction = state.trafficRecord.isUnderConstruction
                )
            }
        }
    }
}

@Composable
private fun TrafficConditionCard(
    trafficRecord: TrafficRecord,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Traffic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Traffic",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (trafficRecord.isCongested)
                    "Heavy"
                else
                    "Clear",
                style = MaterialTheme.typography.titleLarge,
                color = if (trafficRecord.isCongested)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RoadConstructionCard(
    trafficRecord: TrafficRecord,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Construction,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Construction",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (trafficRecord.isUnderConstruction)
                    "Active"
                else
                    "None",
                style = MaterialTheme.typography.titleLarge,
                color = if (trafficRecord.isUnderConstruction)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}