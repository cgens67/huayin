// File: huayin-main/app/src/main/java/com/huayin/music/ui/screens/settings/SettingsScreen.kt
package com.huayin.music.ui.screens.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
    val accountName by rememberPreference(AccountNameKey, "")
    val innerTubeCookie by rememberPreference(InnerTubeCookieKey, "")
    val isLoggedIn = innerTubeCookie.contains("SAPISID")

    var showChangelogBottomSheet by remember { mutableStateOf(false) }
    var showSupportBottomSheet by remember { mutableStateOf(false) }

    SettingsPage(
        title = stringResource(R.string.settings),
        navController = navController,
        scrollBehavior = scrollBehavior
    ) {
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { if (isLoggedIn) navController.navigate("settings/account") else navController.navigate("login") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp, topEnd = 4.dp, bottomEnd = 4.dp)
                    ) {
                        Text(if (isLoggedIn) stringResource(R.string.account) else stringResource(R.string.action_login))
                    }
                    
                    if (isLoggedIn) {
                        Button(
                            onClick = { /* Logout Logic */ },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(stringResource(R.string.logout))
                        }
                    }
                }
            }
        }

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
                    onClick = { showChangelogBottomSheet = true }
                ),
                SettingsCategoryItem(
                    icon = painterResource(R.drawable.favorite),
                    title = { Text("Support Development") },
                    onClick = { showSupportBottomSheet = true }
                )
            )
        )
        
        Spacer(Modifier.height(120.dp))
    }

    if (showChangelogBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChangelogBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            com.huayin.music.ui.component.ChangelogScreen()
            Spacer(Modifier.height(32.dp))
        }
    }

    if (showSupportBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSupportBottomSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            SupportDevelopmentScreen()
        }
    }
}

@Composable
fun SupportDevelopmentScreen() {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.favorite),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Support OpenTune",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "If you like OpenTune, consider supporting its development. Your contribution helps keep the project alive and ad-free.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(32.dp))
        
        Button(
            onClick = { uriHandler.openUri("https://github.com/sponsors/Arturo254") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
        ) {
            Icon(painterResource(R.drawable.github), contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text("GitHub Sponsors")
        }
        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { uriHandler.openUri("https://www.paypal.me/OpenTune") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00457C), contentColor = Color.White)
        ) {
            Icon(painterResource(R.drawable.paypal), contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text("PayPal")
        }
    }
}