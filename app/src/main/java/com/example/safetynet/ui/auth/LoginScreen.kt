package com.example.safetynet.ui.auth

import android.R
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {

    // ------------ State Management ------------
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isChecked = remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showForgotDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    val savedEmail by viewModel.rememberedEmail.collectAsStateWithLifecycle()
    val savedChecked by viewModel.isRememberMeChecked.collectAsStateWithLifecycle()

    var rememberMe by remember { mutableStateOf(false) }
    var hasAutoFilled by remember { mutableStateOf(false) }

    // ------------------- Remember me? ----------------
    LaunchedEffect(savedEmail, savedChecked) {
        if (!hasAutoFilled && savedChecked == true && savedEmail != null) {
            email = savedEmail!!
            rememberMe = true
            hasAutoFilled = true
        }
    }

    // ------------ Google Sign-in setup ------------
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("804915311997-cnsoqp0n90hqvutm8jt58i85dvr68is4.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Create the Launcher to handle the request
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                // Send the token to the ViewModel
                viewModel.signInWithGoogle(token)
            }
        } catch (e: ApiException) {
            Timber.e("Google sign in failed: ${e.statusCode}")
            Toast.makeText(context, "Google Sign-in Failed", Toast.LENGTH_SHORT).show()
        }
    }

    // ------------ Forgot password flow ------------
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your email address to receive a rest link.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.resetPassword(resetEmail)
                    showForgotDialog = false
                }) {
                    Text("Send Link")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ------------ Navigation & Error handling ------------
    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Authenticated -> {
                navController.navigate("map") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    // ------------ UI Layout ------------
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
            //---------- Branding section --------------------
            Image( // Logo
                painter = painterResource(id = com.example.safetynet.R.drawable.ellipse_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .width(65.dp)
                    .height(53.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF001B3D)
            )

            // Login subsection
            TextButton(onClick = {
                navController.navigate("signup")
            }) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color.Gray,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("Don't Have An Account?")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFFBA002C),
                                fontWeight = FontWeight.Bold
                            ),
                        ) {
                            append(" Sign Up")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- Login Form ------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
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
                        Text(text = "Enter your email", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.AlternateEmail,
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
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black,
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password field
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = " PASSWORD",
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Start
                    )
                    TextButton(
                        onClick = { showForgotDialog = true }
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF011C3E)
                        )
                    }
                }

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
                    visualTransformation = if(isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Outlined.Visibility
                        else
                            Icons.Outlined.VisibilityOff

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

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // Removes the ripple if you want it super clean
                    ) {
                        rememberMe = !rememberMe
                    }
                ) {
                    // --- CUSTOM RADIO-STYLE BOX ---
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(
                                if (rememberMe) Color(0xFF001B3D) // Dark Indigo when checked
                                else Color(0xFFF0F2F5)            // Gray when unchecked
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rememberMe) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = "Remember me", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login button
                Button(
                    onClick = {
                        viewModel.login(email, password, rememberMe)
                    },
                    enabled = authState != AuthState.Loading,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF011C3E))
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ---------- Social Login Section -----------------------

            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Divider
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Text(
                    text = "OR CONTINUE WITH",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                // Right Divider
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Social Buttons Row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Google Sign-in Button
                Button(
                    onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = com.example.safetynet.R.drawable.google_logo),
                            contentDescription = "Google Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Google",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Phone Login button
                Button(
                    onClick = { navController.navigate("phone_login") },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Phone,
                            contentDescription = "Phone Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Phone",
                            color = Color.Black,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            // ------------------ Legal Info --------------------
            Text(
                text = "By signing in to SafetyNet, you agree to our \nTerms of Service and Privacy Policy",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }

        // --------- Loading Overlay -----------
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

















