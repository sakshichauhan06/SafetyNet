package com.example.safetynet.ui.auth

import android.R
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    val isChecked = remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Authenticated -> {
                navController.navigate("map") {
                    popUpTo("login") { inclusive = true }
                }
            }
            is AuthState.Error -> {
                val message = (authState as AuthState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = {
            navController.navigate("signup")
        }) {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Gray, fontWeight = FontWeight.Normal)) {
                        append("Don't Have An Account?")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold
                        ),) {
                        append(" Sign Up")
                    }
                },
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(
                    text = "Email Address",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(
                    text = "Password",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = isChecked.value,
                    onCheckedChange = { isChecked.value = it },
                    modifier = Modifier.padding(8.dp),
                    enabled = true,
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Blue,
                        uncheckedColor = Color.DarkGray,
                        checkmarkColor = Color.White
                    ),
                    interactionSource = remember { MutableInteractionSource() }
                )
                Text(
                    text = if (authState == AuthState.Loading) "Loading..." else "Remember me",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            TextButton(
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.login(email, password)
            },
            enabled = authState != AuthState.Loading,
            modifier = Modifier
                .width(300.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(
                text = "Login",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Or Continue with",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(400.dp)
        ) {
            Button(
                onClick = {  },
                modifier = Modifier
                    .padding(16.dp)
                    .width(148.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = true,
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
            ) {
                Text(
                    text = "Google",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = {  },
                modifier = Modifier
                    .padding(16.dp)
                    .width(148.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = true,
                contentPadding = PaddingValues(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A0A1C))
            ) {
                Text(
                    text = "Facebook",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}