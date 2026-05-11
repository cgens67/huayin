package com.huayin.music.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huayin.music.LocalPlayerAwareWindowInsets
import com.huayin.music.R
import com.huayin.music.constants.AccountNameKey
import com.huayin.music.constants.InnerTubeCookieKey
import com.huayin.music.ui.component.*
import com.huayin.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    latestVersion: Long,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val context = LocalContext.current
    val accountName by rememberPreference(AccountNameKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = innerTubeCookie.contains("SAPISID")

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Spacer(Modifier.height(16.dp))

        // Expressive Profile Header
        ElevatedCard(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialShapes.Cookie9Sided.toShape())
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.person),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = if (isLoggedIn) accountName else stringResource(R.string.not_logged_in),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // BUTTON GROUP for Account Actions
                ButtonGroup(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { if (!isLoggedIn) navController.navigate("login") },
                        modifier = Modifier.weight(1f),
                        shape = ButtonGroupDefaults.connectedLeadingButtonShapes()
                    ) {
                        Text(if (isLoggedIn) stringResource(R.string.account) else stringResource(R.string.action_login))
                    }
                    
                    if (isLoggedIn) {
                        OutlinedButton(
                            onClick = { /* Handle logout */ },
                            modifier = Modifier.weight(1f),
                            shape = ButtonGroupDefaults.connectedTrailingButtonShapes()
                        ) {
                            Text(stringResource(R.string.logout))
                        }
                    }
                }
            }
        }

        // Settings Categories
        SettingsCategory(
            title = stringResource(R.string.general_settings),
            items = listOf(
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.appearance)) },
                    onClick = { navController.navigate("settings/appearance") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.play),
                    title = { Text(stringResource(R.string.player_and_audio)) },
                    onClick = { navController.navigate("settings/player") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.storage),
                    title = { Text(stringResource(R.string.storage)) },
                    onClick = { navController.navigate("settings/storage") }
                )
            )
        )

        Spacer(Modifier.height(16.dp))

        SettingsCategory(
            title = stringResource(R.string.community),
            items = listOf(
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.schedule),
                    title = { Text(stringResource(R.string.Changelog)) },
                    onClick = { /* show changelog */ }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.paypal),
                    title = { Text(stringResource(R.string.Donate)) },
                    isHighlighted = true,
                    onClick = { /* uri open */ }
                )
            )
        )
        
        Spacer(Modifier.height(100.dp)) // Extra space for FAB/Player
    }

    TopAppBar(
        title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    )
}