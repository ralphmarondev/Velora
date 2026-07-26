package com.ralphmarondev.velora.features.auth.presentation.register

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ralphmarondev.velora.core.presentation.component.LumiButton
import com.ralphmarondev.velora.core.presentation.component.LumiOutlineButton
import com.ralphmarondev.velora.core.presentation.component.LumiPasswordField
import com.ralphmarondev.velora.core.presentation.component.LumiTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RegisterScreenRoot(
    onLogin: () -> Unit,
    onRegisterSuccessful: () -> Unit
) {
    val viewModel: RegisterViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.navigateToLogin) {
        if (state.navigateToLogin) {
            onLogin()
        }
    }

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onRegisterSuccessful()
        }
    }

    RegisterScreen(
        state = state,
        action = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(
    state: RegisterState,
    action: (RegisterAction) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(state.showErrorMessage) {
        if (state.showErrorMessage) {
            snackbarState.showSnackbar(
                message = state.errorMessage ?: "Registration Failed."
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Register")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Get Started with Velora",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Create your account and enjoy real-time traffic updates wherever you go.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(16.dp))

                LumiTextField(
                    value = state.displayName,
                    onValueChange = { action(RegisterAction.DisplayNameChange(it)) },
                    leadingIconImageVector = Icons.Outlined.AccountTree,
                    labelText = "Display Name",
                    placeHolderText = "Velora User",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    )
                )
                LumiTextField(
                    value = state.email,
                    onValueChange = { action(RegisterAction.EmailChange(it)) },
                    leadingIconImageVector = Icons.Outlined.Email,
                    labelText = "Email",
                    placeHolderText = "someone@example.com",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    )
                )
                LumiPasswordField(
                    value = state.password,
                    onValueChange = { action(RegisterAction.PasswordChange(it)) },
                    leadingIconImageVector = Icons.Outlined.Password,
                    labelText = "Password",
                    placeholderText = "Enter password",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.moveFocus(FocusDirection.Next) }
                    )
                )
                LumiPasswordField(
                    value = state.confirmPassword,
                    onValueChange = { action(RegisterAction.ConfirmPasswordChange(it)) },
                    leadingIconImageVector = Icons.Outlined.Password,
                    labelText = "Confirm Password",
                    placeholderText = "Re-enter password",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                LumiButton(
                    text = "Register",
                    onClick = { action(RegisterAction.Register) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isRegistering
                )

                Spacer(modifier = Modifier.height(16.dp))
                LumiOutlineButton(
                    text = "Already have an Account",
                    onClick = { action(RegisterAction.Login) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}