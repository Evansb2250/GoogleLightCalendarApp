package com.example.googlelightcalendar.ui.screens.loginScreen

import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.googlelightcalendar.R
import com.example.googlelightcalendar.common.imageHolder
import com.example.googlelightcalendar.core.viewmodels.login.LoginScreenStates
import com.example.googlelightcalendar.core.viewmodels.login.LoginViewModel
import com.example.googlelightcalendar.screens.loginScreen.preview.LoginScreenPreviewProvider
import com.example.googlelightcalendar.ui.screens.register.RegistrationScreen
import com.example.googlelightcalendar.ui_components.buttons.GoogleButton
import com.example.googlelightcalendar.ui_components.buttons.StandardButton
import com.example.googlelightcalendar.ui_components.custom_column.AppColumnContainer
import com.example.googlelightcalendar.ui_components.dialog.ErrorAlertDialog
import com.example.googlelightcalendar.ui_components.divider.CustomDividerText
import com.example.googlelightcalendar.ui_components.header.LoginOrSignUpTabAndHeader
import com.example.googlelightcalendar.ui_components.text_fields.CustomOutlineTextField
import com.example.googlelightcalendar.ui_components.text_fields.CustomPasswordTextField
import kotlinx.coroutines.Dispatchers


enum class OnAppStartUpScreen(
    type: String,
) {
    LOGIN("Login"),
    REGISTER("Register")
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun LoginOrSignUpScreen(
    displayScreen: String = OnAppStartUpScreen.LOGIN.name,
) {
    var screenToShow by remember {
        mutableStateOf(displayScreen)
    }
    Scaffold(
        topBar = {
            LoginOrSignUpTabAndHeader(
                onShowLoginScreen = {screenToShow = OnAppStartUpScreen.LOGIN.toString() },
                onShowRegistrationScreen = {screenToShow = OnAppStartUpScreen.REGISTER.toString() }
            )
        }
    ) { it ->
        when(screenToShow){
            OnAppStartUpScreen.LOGIN.toString() -> LoginScreen(
                modifier = Modifier.padding(it),
            )
            OnAppStartUpScreen.REGISTER.toString() -> RegistrationScreen()
        }
    }
}
@Preview(
    showBackground = true,
)
@Composable
private fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val googleSignInIntent = result.data as Intent
            loginViewModel.handleAuthorizationResponse(googleSignInIntent)
        }
    )
    loginViewModel.registerAuthLauncher(
        googleSignInLauncher
    )

    LoginContent(
        modifier = modifier,
        loginState = loginViewModel.state.collectAsState(Dispatchers.Main.immediate).value,
        signInManually = loginViewModel::signInManually,
        initiateGoogleSignIn = loginViewModel::signInWithGoogle,
        retryLogin = loginViewModel::resetLoginScreenState,
        navigateToHomeScreen = loginViewModel::navigateToHomeScreen,
        navigateToRegisterScreen = loginViewModel::navigateToRegisterScreen,
    )
}

@Preview(
    showBackground = true,
)
@Composable
fun LoginContent(
    @PreviewParameter(LoginScreenPreviewProvider::class)
    loginState: LoginScreenStates,
    retryLogin: () -> Unit = {},
    signInManually: (userName: String, password: String) -> Unit = { _, _ -> },
    initiateGoogleSignIn: () -> Unit = {},
    navigateToHomeScreen: (String) -> Unit = {},
    navigateToRegisterScreen: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier.background(Color.Black)
    ) {

        if (loginState is LoginScreenStates.LoginError) {
            ErrorAlertDialog(
                title = "Login Failed",
                error = loginState.message,
                onDismiss = retryLogin
            )

        } else if (
            loginState is LoginScreenStates.RegistrationRequiredState
        ) {
            ErrorAlertDialog(
                title = "Need to Register User",
                error = "feature isn't implemnented",
                onDismiss = retryLogin
            )
        } else if (
            loginState is LoginScreenStates.UserSignedInState
        ) {
            navigateToHomeScreen(loginState.email)
        }

        LoginScreenStateContent(
            modifier = modifier,
            loginState = if (loginState is LoginScreenStates.LoginScreenState) loginState else LoginScreenStates.LoginScreenState(),
            signInManually = signInManually,
            initiateGoogleSignIn = initiateGoogleSignIn,
        )
    }
}

@Composable
private fun LoginScreenStateContent(
    modifier: Modifier = Modifier,
    loginState: LoginScreenStates.LoginScreenState,
    signInManually: (userName: String, password: String) -> Unit = { _, _ -> },
    initiateGoogleSignIn: () -> Unit = {},
) {
    var containsIncompleteCredentials by remember {
        mutableStateOf(false)
    }
    AppColumnContainer(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (containsIncompleteCredentials) {
            ErrorAlertDialog(
                title = "Invalid Credentials",
                error = "please fill in the required information",
                onDismiss = {
                    containsIncompleteCredentials = false
                }
            )
        }
        CustomOutlineTextField(
            value = loginState.email.value,
            onValueChange = { userNameUpdate ->
                loginState.email.value = userNameUpdate
            },
            leadingIcon = imageHolder(
                leadingIcon = R.drawable.email_envelope,
                description = "last name avatar",
            ),
            label = "Email",
        )

        Spacer(
            modifier = Modifier.size(20.dp)
        )


        CustomPasswordTextField(
            value = loginState.password.value,
            onValueChange = { passwordUpdate ->
                loginState.password.value = passwordUpdate
            },
        )
        Spacer(
            modifier = Modifier.size(10.dp)
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Forgot Password?",
            textAlign = TextAlign.End,
            color = Color.White,
        )

        Spacer(
            modifier = Modifier.size(40.dp)
        )

        StandardButton(
            text = "Log in",
            onClick = {
                if (loginState.containsValidCredentials()) {
                    signInManually(
                        loginState.email.value,
                        loginState.password.value,
                    )
                } else {
                    containsIncompleteCredentials = true
                }
            },
        )

        Spacer(
            modifier = Modifier.size(20.dp)
        )

        CustomDividerText()

        Spacer(
            modifier = Modifier.size(20.dp)
        )

        GoogleButton(
            onClick = initiateGoogleSignIn,
        )

        Spacer(
            modifier = Modifier.size(10.dp)
        )
    }
}