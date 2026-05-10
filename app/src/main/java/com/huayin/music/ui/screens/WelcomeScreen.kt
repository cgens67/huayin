package com.huayin.music.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.huayin.music.R
import com.huayin.music.constants.AgreedToPrivacyPolicyKey
import com.huayin.music.constants.HasSeenWelcomeKey
import com.huayin.music.utils.dataStore
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var privacyAgreed by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.music_note),
                contentDescription = "HuaYin Logo",
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "欢迎使用华音 (HuaYin)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "华音是一款合规、纯净的音乐播放器。在您开始使用之前，请务必仔细阅读并同意《用户服务协议》与《隐私政策》。我们承诺严格保护您的个人信息与数据安全。",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 24.sp
            )
            
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Checkbox(
                    checked = privacyAgreed,
                    onCheckedChange = { privacyAgreed = it }
                )
                Text(
                    text = "我已阅读并同意《用户协议》与《隐私政策》",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (privacyAgreed) {
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
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "同意并继续", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showWarningDialog) {
        AlertDialog(
            onDismissRequest = { showWarningDialog = false },
            title = { Text("温馨提示") },
            text = { Text("为了您的合法权益，请先阅读并勾选同意《用户服务协议》与《隐私政策》。") },
            confirmButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text("我知道了")
                }
            }
        )
    }
}
