package com.huayin.music.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huayin.music.LocalPlayerAwareWindowInsets
import com.huayin.music.R
import com.huayin.music.constants.AccountNameKey
import com.huayin.music.constants.InnerTubeCookieKey
import com.huayin.music.ui.component.*
import com.huayin.music.ui.utils.backToMain
import com.huayin.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    latestVersion: Long,
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
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

        // EXPRESSIVE HEADER CARD
        ElevatedCard(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User Avatar Display
                AvatarDisplay(size = 80.dp, showBorder = true)

                Spacer(Modifier.height(12.dp))

                Text(
                    text = if (isLoggedIn) accountName else stringResource(R.string.not_logged_in),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(20.dp))

                ButtonGroup(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { if (isLoggedIn) navController.navigate("settings/account") else navController.navigate("login") },
                        modifier = Modifier.weight(1f),
                        shape = ButtonGroupDefaults.connectedLeadingButtonShapes().shape
                    ) {
                        Text(if (isLoggedIn) stringResource(R.string.account) else stringResource(R.string.action_login))
                    }
                    
                    if (isLoggedIn) {
                        OutlinedButton(
                            onClick = { /* Logout logic through ViewModel */ },
                            modifier = Modifier.weight(1f),
                            shape = ButtonGroupDefaults.connectedTrailingButtonShapes().shape
                        ) {
                            Text(stringResource(R.string.logout))
                        }
                    }
                }
            }
        }

        // CATEGORIES
        SettingsCategory(
            title = stringResource(R.string.general_settings),
            items = listOf(
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.palette),
                    title = { Text(stringResource(R.string.appearance)) },
                    description = { Text("Theme, accent colors, and button shapes") },
                    onClick = { navController.navigate("settings/appearance") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.play),
                    title = { Text(stringResource(R.string.player_and_audio)) },
                    description = { Text("Audio quality, normalization, and queue") },
                    onClick = { navController.navigate("settings/player") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.language),
                    title = { Text(stringResource(R.string.content)) },
                    description = { Text("Language, location, and explicit filter") },
                    onClick = { navController.navigate("settings/content") }
                )
            )
        )

        Spacer(Modifier.height(16.dp))

        SettingsCategory(
            title = "Data & System",
            items = listOf(
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.storage),
                    title = { Text(stringResource(R.string.storage)) },
                    description = { Text("Manage cache and downloaded files") },
                    onClick = { navController.navigate("settings/storage") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.restore),
                    title = { Text(stringResource(R.string.backup_restore)) },
                    description = { Text("Export or import your local library") },
                    onClick = { navController.navigate("settings/backup_restore") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.security),
                    title = { Text(stringResource(R.string.privacy)) },
                    description = { Text("History and screenshot settings") },
                    onClick = { navController.navigate("settings/privacy") }
                )
            )
        )

        Spacer(Modifier.height(16.dp))

        SettingsCategory(
            title = "OpenTune",
            items = listOf(
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text(stringResource(R.string.about)) },
                    onClick = { navController.navigate("settings/about") }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.schedule),
                    title = { Text(stringResource(R.string.Changelog)) },
                    onClick = { /* Open Changelog */ }
                )
            )
        )
        
        Spacer(Modifier.height(120.dp))
    }

    TopAppBar(
        title = { Text(stringResource(R.string.settings), fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
    )
}