package com.huayin.music.ui.screens

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
import androidx.compose.ui.Alignment
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
                    IconButton(
                        onClick = { navController.navigateUp() },
                        onLongClick = {}
                    ) {
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
                content = "欢迎您使用华音！我们非常重视您的隐私保护和个人信息安全。本协议旨在向您说明我们在提供服务时收集、使用、存储和保护您个人信息的规则，以及您的相关权利。"
            )

            PolicySection(
                title = "1. 我们如何收集和使用您的信息",
                content = "作为一款开源、本地优先的第三方音乐客户端，我们在设计上遵循“最少够用”原则：\n\n" +
                        "• 本地数据存储：您的播放历史、自建歌单、偏好设置等数据均默认加密存储在您的设备本地，我们不会将其主动隐式上传至任何开发者管控的外部集中服务器。\n\n" +
                        "• 网络资源获取：当您进行云搜索、歌词解析、资源爬取串流等涉及核心功能时，App 会在网络通畅时单点直接请求至外部的流服务公用接口（如 Google/YouTube API、各类音乐抓取接口）。该过程可能需要携带设备软硬件元数据发起匿名请求。\n\n" +
                        "• 第三方账号互通（部分情况可选）：若选择挂载其他厂商凭证进行跨站账号登录关联功能，其Cookie或Tokens同样受本地环境保存管控而拒绝私有侧泄漏与越权监听。"
            )

            PolicySection(
                title = "2. 有关可用性环境限制配置要求的特殊说明",
                content = "根据我国政策规范要求以及不同平台的风控地域约束条件考量，很多应用层服务在无科学代理或隧道翻墙情况下，对于中国大陆地区的用户群体表现出直连失效以及无访问解析可能等问题状况。\n\n如果您处于对应相关特殊封锁管制境内限制直通使用地区范围等环境且未启用相应的路由节点分流网络技术环境配置等处理工具，应用中极大机率可能会遇到因拉取媒体列表服务器连接握手无应答无法顺利开启与数据下发现象异常卡死等行为逻辑故障等表现状况。为了完整的元信息拉回音轨顺畅性获取操作过程体验请知悉前置科学用网支持技术方案为使用者提供侧基础运行必然必要能力要求等事实限制项等不可控表现不可预测使用状态阻滞问题事实说明存在与技术解决支持性依赖因素客观局限。"
            )

            PolicySection(
                title = "3. 我们如何保护您的信息",
                content = "我们致力于使用各种成熟的技术脱敏安全模型搭配配套管理机制体系以达到信息泄漏隔离阻拦保护规范防止用户私源资产如使用数据偏好痕迹记录日志以及歌库档案损坏损耗丢失状况。您的用户隐私凭条皆受到 Android OS 等设备系统层最高权属控制区沙盒封装着管控保障安全性。我们仅会在合法权限与系统安全范畴赋予权限运行逻辑状态内严格调用读取执行相关必要行为内容保障系统稳定性防篡流控制干预防护执行要求等原则保护事实性机制运作行为有效管控。"
            )

            PolicySection(
                title = "4. 您的相关权利功能指引设置说明管理事项执行规范",
                content = "在此平台设置或选项侧界面当中：\n" +
                        "您可以随时使用“清除记录与清除内存与数据重置归零或离线模式功能设定控制行为选择清除痕迹”。您能任意管理、操作注销所有会话或完全登出、注销等清理断连机制的权力。卸载本 App，由于应用是采取完全本地应用配置的数据闭环存放方案管理原则实施数据存档保存，将为您做同步销毁全部产生的运行痕迹以符合脱钩抹迹管控目的要求及需求预期清理原则设定期望达到安全诉求等管理实施落实等基本预期效果反馈保证原则功能管理标准等行为保障等管理诉求保证安全功能完整可自主实施功能诉求目的达标保护操作管控结果执行等标准效力与有效落实功能表现与最终清清除管理等承诺执行行为闭环。"
            )

            PolicySection(
                title = "5. 免责限制和禁止服务规则约束",
                content = "禁止恶意商用修改套用非法采集爬源破解外泄损害传播贩售以用于营利黑产牟取不正规服务盈利暴利与侵害违逆法律界定标准破坏公共应用环境服务安全准入规等禁止项管控准侧说明底线规定；且用户必须服从所有平台规则约定与合规道德限制承诺，否则本提供者将不做相关兜底保证并由此带来的连带法律维权等恶劣风险一概自行履行。"
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "华音 开发者团队",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
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
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 24.sp
        )
    }
}