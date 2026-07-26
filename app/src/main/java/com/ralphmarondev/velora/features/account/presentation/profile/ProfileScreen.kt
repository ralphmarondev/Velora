package com.ralphmarondev.velora.features.account.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ralphmarondev.velora.R
import com.ralphmarondev.velora.core.presentation.component.LumiTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreenRoot(
    logout: () -> Unit,
    selectImage: () -> Unit
) {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.selectImage) {
        if (state.selectImage) {
            selectImage()
        }
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            logout()
        }
    }

    ProfileScreen(
        state = state,
        action = viewModel::onAction
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    state: ProfileState,
    action: (ProfileAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Profile")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { action(ProfileAction.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    UserImage(
                        imagePath = state.imagePath,
                        onImageSelected = { action(ProfileAction.SelectImage) },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column {
                            AccountField(
                                label = "Display Name",
                                value = state.displayName ?: "Not set",
                                onClick = {
                                    action(ProfileAction.ShowDisplayNameDialog(true))
                                }
                            )
                            HorizontalDivider(thickness = 0.3.dp)
                            AccountField(
                                label = "Email",
                                value = state.email ?: "Not set",
                                onClick = {
                                    action(ProfileAction.ShowEmailDialog(true))
                                }
                            )
                            HorizontalDivider(thickness = 0.3.dp)
                            AccountField(
                                label = "Password",
                                value = state.password ?: "Not set",
                                onClick = {
                                    action(ProfileAction.ShowPasswordDialog(true))
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { action(ProfileAction.Logout) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Logout",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }

        if (state.showDisplayNameDialog) {
            DisplayNameDialog(
                displayName = state.displayName ?: "",
                onCancel = { action(ProfileAction.ShowDisplayNameDialog(false)) },
                onUpdate = { updatedDisplayName ->
                    action(ProfileAction.UpdateDisplayName(updatedDisplayName))
                }
            )
        }
        if (state.showEmailDialog) {
            EmailDialog(
                email = state.email ?: "",
                close = { action(ProfileAction.ShowEmailDialog(false)) }
            )
        }
        if (state.showPasswordDialog) {
            PasswordDialog(
                onCancel = { action(ProfileAction.ShowPasswordDialog(false)) },
                onUpdate = { updatedPassword ->
                    action(ProfileAction.UpdatePassword(updatedPassword))
                }
            )
        }
    }
}


@Composable
private fun UserImage(
    imagePath: Int,
    onImageSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileImagePath = when (imagePath) {
        1 -> R.drawable.profile_2
        2 -> R.drawable.profile_3
        3 -> R.drawable.profile_4
        4 -> R.drawable.profile_4
        5 -> R.drawable.profile_5
        else -> R.drawable.profile_2
    }
    Image(
        painter = rememberAsyncImagePainter(profileImagePath),
        contentDescription = "User Image",
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .clickable { onImageSelected() },
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun AccountField(label: String, value: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.width(2.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .rotate(180f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun DisplayNameDialog(
    displayName: String,
    onCancel: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var newDisplayName by rememberSaveable { mutableStateOf(displayName) }

    AlertDialog(
        onDismissRequest = { onCancel() },
        confirmButton = {
            TextButton(onClick = { onUpdate(newDisplayName) }) {
                Text(text = "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = "Update Display Name",
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column {
                Text(
                    text = "This name is only visible by you.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                LumiTextField(
                    value = newDisplayName,
                    onValueChange = { newDisplayName = it },
                    placeHolderText = "Velora User",
                    labelText = "Display Name",
                    leadingIconImageVector = Icons.Outlined.ManageAccounts,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
private fun EmailDialog(
    email: String,
    close: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { close() },
        confirmButton = {
            TextButton(onClick = close) {
                Text(text = "Close")
            }
        },
        title = {
            Text(
                text = "View Email",
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column {
                Text(
                    text = "This email is used to sign in to your account. It can't be changed right now.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(20),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary
                    ),
                    placeholder = {
                        Text(
                            text = "someone@example.com",
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    label = {
                        Text(
                            text = "Email",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    singleLine = true,
                    maxLines = 1,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            }
        }
    )
}

// onUpdate: (newPassword) -> Unit
@Composable
private fun PasswordDialog(
    onCancel: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmNewPassword by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onCancel() },
        confirmButton = {
            TextButton(
                onClick = {
                    if (newPassword == confirmNewPassword) {
                        onUpdate(newPassword)
                    }
                }
            ) {
                Text(text = "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(
                text = "Update Password",
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column {
                Text(
                    text = "This password is used to sign in to Velora.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                LumiTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeHolderText = "New Password",
                    labelText = "New Password",
                    leadingIconImageVector = Icons.Outlined.Password,
                    modifier = Modifier.fillMaxWidth()
                )
                LumiTextField(
                    value = confirmNewPassword,
                    onValueChange = { confirmNewPassword = it },
                    placeHolderText = "Confirm Password",
                    labelText = "Confirm Password",
                    leadingIconImageVector = Icons.Outlined.Password,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}