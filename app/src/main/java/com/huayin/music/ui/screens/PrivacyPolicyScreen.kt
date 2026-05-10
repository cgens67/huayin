package com.huayin.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huayin.music.LocalPlayerAwareWindowInsets
import com.huayin.music.R
import com.huayin.music.ui.component.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "用户协议与隐私政策",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "返回"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(
                    LocalPlayerAwareWindowInsets.current.only(
                        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            PolicySection(
                title = "前言",
                content = "欢迎您使用华音（OpenTune）！我们非常重视您的隐私保护和个人信息安全。本协议旨在向您说明我们在提供服务时收集、使用、存储和保护您个人信息的规则，以及您的相关权利。"
            )

            PolicySection(
                title = "1. 我们如何收集和使用您的信息",
                content = "作为一款开源、本地优先的第三方音乐客户端，我们在设计上遵循“最少够用”原则：\n" +
                        "• 本地数据存储：您的播放历史、自建歌单、偏好设置等数据均默认加密存储在您的设备本地，我们不会将其上传至任何开发者服务器。\n" +
                        "• 网络请求：当您搜索音乐、浏览歌曲列表、播放音乐时，应用会直接向第三方接口（如 YouTube Music API、Kugou API、Lrclib API等）发送必要的匿名网络请求，以获取对应的音频流和元数据。此过程不包含任何能识别您真实身份的敏感信息。\n" +
                        "• 第三方账号登录（可选）：如果您选择登录 YouTube 账号，登录凭证仅保存在您的本地设备中，用于与 YouTube 官方服务同步您的个人音乐库与偏好。"
            )

            PolicySection(
                title = "2. 第三方 SDK 与服务",
                content = "为保障应用功能的实现，我们会接入必要的第三方开源库与服务。主要包括：\n" +
                        "• 媒体流解析与获取：请求将会发送至相应的流媒体提供商服务器以获取公开的音频内容。\n" +
                        "• GitHub API：仅用于检查应用是否有新版本更新。\n" +
                        "我们承诺应用内不接入任何广告 SDK、用户追踪分析 SDK 或非法数据收集插件。"
            )

            PolicySection(
                title = "3. 我们如何保护您的信息",
                content = "我们致力于使用各种安全技术及配套的管理体系来防止您的信息被泄露、毁损或丢失。由于所有核心偏好与播放记录数据完全存储在本地，最大程度上避免了云端数据泄露的风险。"
            )

            PolicySection(
                title = "4. 您的权利",
                content = "您可以通过应用内的“设置”页面管理您的个人数据：\n" +
                        "• 清除缓存：您可以随时清除应用产生的音频缓存与图片缓存。\n" +
                        "• 清除记录：您可以随时一键清除本地的播放历史和搜索历史。\n" +
                        "• 退出登录：您可以随时退出您的第三方音乐平台账号并清除本地保存的凭证。\n" +
                        "如需撤回本政策的同意或彻底删除数据，您只需卸载本应用即可彻底清除所有应用关联数据。"
            )

            PolicySection(
                title = "5. 未成年人保护",
                content = "本应用主要面向成年人。若您是未满18周岁的未成年人，请在法定监护人的陪同与指导下阅读本政策，并在征得其同意的前提下使用我们的服务。"
            )

            PolicySection(
                title = "6. 用户服务协议",
                content = "• 服务性质：本应用是一款基于开源项目的第三方音乐客户端，所提供的所有音频内容均来源于互联网公开接口。本应用不提供任何破解、盗版或侵权内容的存储服务。\n" +
                        "• 使用规范：您同意不在使用本应用时从事任何违反中国法律法规的活动。不得利用本应用进行商业盈利、大规模恶意抓取或破坏第三方平台正常运营的行为。\n" +
                        "• 免责声明：受限于网络环境与第三方接口的稳定性，我们无法保证服务绝对不中断或毫无瑕疵。因不可抗力或第三方平台规则变更导致的服务不可用，开发者不承担相关责任。"
            )
            
            PolicySection(
                title = "7. 政策的更新与生效",
                content = "我们可能会适时对本隐私政策及用户协议进行修订。当条款发生重大变更时，我们将在您更新版本后的首次启动时向您展示变更后的内容。本政策自您点击“同意”之日起生效。"
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "华音 (OpenTune) 开发者团队",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )
    }
}