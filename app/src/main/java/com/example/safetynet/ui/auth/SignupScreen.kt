package com.example.safetynet.ui.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import timber.log.Timber

@Composable
fun SignupScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {

    // ----------------- State Management ------------------
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState = authViewModel.authState.collectAsState()
    val context = LocalContext.current

    val isChecked = remember { mutableStateOf(false) }

    var isPasswordVisible by remember { mutableStateOf(false) }

    // ------------------- Google Sign-in Launcher ---------------
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("804915311997-cnsoqp0n90hqvutm8jt58i85dvr68is4.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                authViewModel.signInWithGoogle(token)
            }
        } catch (e: ApiException) {
            Timber.e("Google sign in failed: ${e.statusCode}")
        }
    }

    // ---------------- Navigation and Error Handling
    LaunchedEffect(authState.value) {
        when(authState.value) {
            is AuthState.Authenticated -> {
                navController.navigate("map") {
                    popUpTo("signup") { inclusive = true }
                }
            }
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message,
                Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Normal)) {
                            append("Already Have An Account?")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold
                            ),) {
                            append(" Login")
                        }
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(
                        text = "Email Address",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(
                        text = "Enter your Password",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if(isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (isPasswordVisible)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    val description = if (isPasswordVisible)
                        "Hide Password"
                    else
                        "Show Password"

                    IconButton(onClick = {isPasswordVisible = !isPasswordVisible}) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Signup button
            Button(
                onClick = {
                    authViewModel.signup(email, password)
                },
                enabled = authState!= AuthState.Loading,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(
                    text = "Sign Up",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Or Continue with",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Google and Phone auth
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
                ) {
                    Text("Google", color = Color.White)
                }

                Button(
                    onClick = { navController.navigate("phone_login") },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
                ) {
                    Text("Phone", color = Color.White)
                }
            }
        }

        // Loading Overlay
        if (authState == AuthState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Blue)
            }
        }
    }
}

