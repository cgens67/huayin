package com.huayin.music.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.huayin.music.R
import com.huayin.music.constants.AgreedToPrivacyPolicyKey
import com.huayin.music.constants.HasSeenWelcomeKey
import com.huayin.music.constants.ProxyEnabledKey
import com.huayin.music.constants.ProxyTypeKey
import com.huayin.music.constants.ProxyUrlKey
import com.huayin.music.extensions.toInetSocketAddress
import com.huayin.music.innertube.YouTube
import com.huayin.music.utils.dataStore
import com.huayin.music.utils.rememberEnumPreference
import com.huayin.music.utils.rememberPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.Proxy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var privacyAgreed by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var isReadyToAnimate by remember { mutableStateOf(false) }

    // Preferencias iniciales de red y entorno 
    val (proxyEnabled, onProxyEnabledChange) = rememberPreference(key = ProxyEnabledKey, defaultValue = false)
    val (proxyType, onProxyTypeChange) = rememberEnumPreference(key = ProxyTypeKey, defaultValue = java.net.Proxy.Type.HTTP)
    val (proxyUrl, onProxyUrlChange) = rememberPreference(key = ProxyUrlKey, defaultValue = "127.0.0.1:10808")
    
    var expandProxyDetails by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(150)
        isReadyToAnimate = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Material 3 Expressive Hero Layout using an aggressive radius 
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 16.dp),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 120.dp, bottomEnd = 24.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(top = 56.dp, bottom = 64.dp, start = 24.dp, end = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(38.dp)
                            )
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.music_note),
                            contentDescription = "HuaYin Logo",
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Text(
                        text = "开启华音",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        letterSpacing = (-1.5).sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "随心享受、探索与定制专属个人的聆听节奏！通过云原生流获取无穷媒体曲库信息",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Material Expressive Connectivity Setup Configuration (Accordion Type Container)
            AnimatedVisibility(
                visible = isReadyToAnimate,
                enter = fadeIn(tween(800)) + expandVertically(tween(600))
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .animateContentSize(tween(400)),
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { expandProxyDetails = !expandProxyDetails }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.onTertiaryContainer.copy(0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ){
                                Icon(
                                    painter = painterResource(if (proxyEnabled) R.drawable.wifi_proxy else R.drawable.wifi),
                                    contentDescription = "Proxy",
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "关于区域流服务",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "境内需特定代理网络建立连通",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                                )
                            }
                            Switch(
                                checked = proxyEnabled,
                                onCheckedChange = { 
                                    onProxyEnabledChange(it)
                                    if(it) expandProxyDetails = true
                                }
                            )
                        }

                        // Detailed fields for Proxy handling (Tunnels through if Mainland is blocked)
                        AnimatedVisibility(
                            visible = proxyEnabled && expandProxyDetails,
                            enter = fadeIn() + expandVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    var typeDropdownExpanded by remember { mutableStateOf(false) }

                                    // Exposed dropdown exclusively customized in shape bounds.
                                    ExposedDropdownMenuBox(
                                        expanded = typeDropdownExpanded,
                                        onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        OutlinedTextField(
                                            value = proxyType.name,
                                            onValueChange = {},
                                            readOnly = true,
                                            modifier = Modifier.menuAnchor(),
                                            label = { Text("通道配置") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = TextFieldDefaults.colors(
                                                unfocusedContainerColor = Color.Transparent,
                                                focusedContainerColor = Color.Transparent
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            expanded = typeDropdownExpanded,
                                            onDismissRequest = { typeDropdownExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("HTTP") },
                                                onClick = {
                                                    onProxyTypeChange(java.net.Proxy.Type.HTTP)
                                                    typeDropdownExpanded = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("SOCKS") },
                                                onClick = {
                                                    onProxyTypeChange(java.net.Proxy.Type.SOCKS)
                                                    typeDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }

                                    OutlinedTextField(
                                        value = proxyUrl,
                                        onValueChange = { onProxyUrlChange(it) },
                                        label = { Text("URL及接口") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.weight(2f),
                                        colors = TextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent
                                        )
                                    )
                                }
                                Text(
                                    text = "将在授权完成后生效全局环境接管",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top=8.dp, start=6.dp),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha=0.6f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { privacyAgreed = !privacyAgreed }
                    .padding(horizontal = 8.dp, vertical = 14.dp)
            ) {
                Checkbox(
                    checked = privacyAgreed,
                    onCheckedChange = { privacyAgreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                
                val policyText = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append("我已通读接受声明准则：")
                    }
                    
                    val userAgreementLink = LinkAnnotation.Clickable("user_agreement") {
                        navController.navigate("privacy_policy")
                    }
                    withLink(userAgreementLink) {
                        withStyle(SpanStyle(
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.Bold
                        )) {
                            append("《用户服务协议》")
                        }
                    }
                    
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
                        append("及")
                    }

                    val privacyPolicyLink = LinkAnnotation.Clickable("privacy_policy") {
                        navController.navigate("privacy_policy")
                    }
                    withLink(privacyPolicyLink) {
                        withStyle(SpanStyle(
                            color = MaterialTheme.colorScheme.primary, 
                            fontWeight = FontWeight.Bold
                        )) {
                            append("《隐私政策》")
                        }
                    }
                }

                Text(
                    text = policyText,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Extra Chunky Button reflecting massive M3 bounds guidelines 
            Button(
                onClick = {
                    if (privacyAgreed) {
                        // Apply Network hot state override so API connects without restarts
                        try {
                            if (proxyEnabled && proxyUrl.contains(":")) {
                                YouTube.proxy = Proxy(proxyType, proxyUrl.toInetSocketAddress())
                            } else {
                                YouTube.proxy = null
                            }
                        } catch(ignored: Exception){}
                        
                        coroutineScope.launch {
                            context.dataStore.edit {
                                it[HasSeenWelcomeKey] = true
                                it[AgreedToPrivacyPolicyKey] = true
                            }
                            navController.navigate(Screens.Home.route) {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    } else {
                        showWarningDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(72.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "现在踏入音旅",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { 
                Text(
                    text = "法定合规预通知", 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.error
                ) 
            },
            text = { 
                Text(
                    text = "针对《APP合法依规使用规范》，为了您在使用中的权利得到系统隔离支持，请先在面板前文确认通览底部的《服务声明条款》协议信息后再执行开锁。",
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = { showWarningDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "我已经知道了", 
                        fontWeight = FontWeight.ExtraBold, 
                        fontSize = 16.sp
                    )
                }
            },
            shape = RoundedCornerShape(32.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }
}