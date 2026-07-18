package com.example.safetynet.ui

import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.safetynet.ui.theme.safeContentPadding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SOSScreen(
    navController: NavController,
    mapViewModel: MapViewModel,
    viewModel: ProfileViewModel = hiltViewModel(),
    sosViewModel: SOSViewModel = hiltViewModel(),
    onCancel: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val userLocation by mapViewModel.userLocation
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val sosState by sosViewModel.sosState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Get data from ROOM
    val contactName = user?.emergencyContactName ?: "Emergency Contact"
    val phoneNumber = user?.emergencyContact ?: ""

    // Timer states
    var timeLeft by remember { mutableIntStateOf(5) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // When isTimerRunning becomes true, start the countdown
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // When timer hits 0, trigger the call
            if (isTimerRunning && phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = "tel:$phoneNumber".toUri()
                }
                context.startActivity(intent)
            }
            // Reset timer for next time
            isTimerRunning = false
            timeLeft = 5
        }
    }

    LaunchedEffect(sosState) {
        when (val state = sosState) {
            is SOSState.Sending -> {
                // optional later but show loading indicator
            }
            is SOSState.Success -> {
                // Show share sheet for non-app contacts
                if (state.nonAppContacts.isNotEmpty()) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, state.message)
                    }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "Share SOS via...")
                    )
                }

                // Show success snackbar
                snackbarHostState.showSnackbar(
                    "Sent ${state.appNotificationSent} app alerts. Share with others manually."
                )
                sosViewModel.resetState()
            }
            is SOSState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                sosViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SafetyNet",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("manage_contacts")
                    }) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Manage Contacts",
                            tint = Color(0xFF1A237E)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .safeContentPadding()
                .padding(24.dp)
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // System active tag
            Surface(
                color = Color.Gray.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = Color.Red, shape = CircleShape)
                            .size(6.dp)
                    ) {}

                    Text(
                        text = "System Active & Monitoring",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isTimerRunning) {
                Text(
                    text = "Need Help?",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Press the button below to call your emergency contact: $contactName",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(32.dp))

                PulsingSOSButton(
                    onClick = {
                        if (phoneNumber.isNotEmpty()) {
                            isTimerRunning = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Helpers
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    //Quick SOS Alert
                    Surface(
                        color = Color.Gray.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable{
                                val location = userLocation

                                if (location == null) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Location not available. Please enable GPS.")
                                    }
                                    return@clickable
                                }

                                if (user == null) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("User not logged in.")
                                    }
                                    return@clickable
                                }

                                sosViewModel.sendSOSAlert(
                                    userName = user?.name ?: "Someone",
                                    location = location
                                )
                            }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(color = Color(0xFFFFDAD8), shape = CircleShape)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.NotificationsActive,
                                    contentDescription = "Alert Option",
                                    tint = Color(0xFF7E0411),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Quick SOS Alert",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Text(
                                    text = "Notify 5 trusted Contacts Instantly",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nearby Safe Havens
                    Surface(
                        color = Color.Gray.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(color = Color(0xFFEBDCFF), shape = CircleShape)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = "Shields",
                                    tint = Color(0xFF260058),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Nearby Safe Havens",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Text(
                                    text = "Police, Hospitals, Shelters",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recording
                    Surface(
                        color = Color.Gray.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(color = Color(0xFFE8E8EA), shape = CircleShape)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "Record",
                                    tint = Color(0xFF000666),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Stealth Recording",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Text(
                                    text = "Background audio evidence",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Location tag
                Surface(
                    color = Color.Gray.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "YOUR LOCATION IS BEING BROADCASTED",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

            } else {
                // Countdown UI
                Text(
                    text = "CALLING $contactName IN...",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$timeLeft",
                    fontSize = 120.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Red
                )

                Spacer(modifier = Modifier.height(40.dp))

                // The "I'm Safe" / Cancel button
                Button(
                    onClick = {
                        isTimerRunning = false
                        timeLeft = 5
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("CANCEL (I'M SAFE)", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (phoneNumber.isEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No contact number found. Please add one in 'Manage Profile'",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PulsingSOSButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSize = 260.dp
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val rings = listOf(0f, 0.33f, 0.66f) // Three rings with staggered delays

    // Box to contain button + rings
    Box(
        modifier = modifier.size(buttonSize),
        contentAlignment = Alignment.Center
    ) {
        // Rings behind button
        rings.forEachIndexed { index, delayFraction ->
            val delay = (delayFraction * 2000).toInt() // 2-second cycle

            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 2.5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        delayMillis = delay,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "scale_$index"
            )

            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 2000,
                        delayMillis = delay,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "alpha_$index"
            )

            // Ring surface
            Surface(
                modifier = Modifier
                    .size(buttonSize)
                    .scale(scale)
                    .alpha(alpha)
                    .zIndex(0f), // Behind button
                shape = CircleShape,
                color = Color(0xFFBA002C).copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    Color(0xFFBA002C).copy(alpha = 0.5f)
                )
            ) { }
        }

        // Actual button on top
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(buttonSize)
                .zIndex(1f),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA002C)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Call Phone",
                    tint = Color.White,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CALL HELPLINE",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "IMMEDIATE ASSISTANCE",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}















