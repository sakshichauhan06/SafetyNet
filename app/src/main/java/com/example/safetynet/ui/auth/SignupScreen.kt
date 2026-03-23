package com.example.safetynet.ui.auth

import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockPerson
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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

    // ---------------- Navigation and Error Handling ----------------------
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

    // --------------------- UI Layout ---------------------
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6FAFF))
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ----------------- Branding Section --------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = com.example.safetynet.R.drawable.ellipse_logo),
                    contentDescription = "SafetyNet Logo",
                    modifier = Modifier
                        .width(45.dp)
                        .height(33.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "SafetyNet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001B3D)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Create your \naccount.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF001B3D)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Join a community built on mutual \nprotection and unwavering support.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ------------------ Login Form -------------------------

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                // Email field
                Text(
                    text = " EMAIL ADDRESS",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text("name@example.com", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = "Email address",
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE4E9ED),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Create Password field
                Text(
                    text = " CREATE PASSWORD",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(text = "••••••••", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE4E9ED),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Outlined.Visibility
                        else
                            Icons.Outlined.VisibilityOff

                        val description = if (isPasswordVisible)
                            "Hide Password"
                        else
                            "Show Password"

                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password field
                Text(
                    text = " CONFIRM PASSWORD",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(text = "••••••••", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE4E9ED),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Outlined.Visibility
                        else
                            Icons.Outlined.VisibilityOff

                        val description = if (isPasswordVisible)
                            "Hide Password"
                        else
                            "Show Password"

                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ------------------ Legal Info --------------------
            Text(
                text = "By signing in to SafetyNet, you agree to our \nTerms of Service and Privacy Policy",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Signup button
            Button(
                onClick = {
                    authViewModel.signup(email, password)
                },
                enabled = authViewModel.isEmailValid(email) && password.length >= 6 && authState != AuthState.Loading,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF011C3E))
            ) {
                Text(
                    text = "Sign Up",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- Already Login page --------------
            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Already Have An Account?")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFFBA002C),
                                fontWeight = FontWeight.Bold
                            ),
                        ) {
                            append(" Login")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Icon(
                    imageVector = Icons.Outlined.LockPerson,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "ENCRYPTED END-TO-END, YOUR SAFETY \nDATA IS PRIVATE AND SECURE.",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.labelSmall
                )
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

