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
                title = "前言说明准侧提示须知：",
                content = "欢迎您选择华音使用音频解析支持播放工具架构组合体验服务！为了全方位严护履行遵守我们有关技术支持中的本地合法化规范限制边界，我们要求任何安装执行端客户详细悉知这本通册章文所披露记录的使用边界以及采集授权安全承诺管理事项规则约定！\n点击继续默认则意味着视为确认已知悉该规内容且愿意被相关约束并自知风险。"
            )

            PolicySection(
                title = "1. 关于如何处理数据留迹及采集存储机制要求？",
                content = "依据最小化及脱钩无追踪设计架构宗旨思想。任何通过该架构在正常使用场景过程当所触发执行动作关联数据状态如下阐明执行方法落落底管控：\n\n" +
                        "• 本机留存原则机制方案设定：如产生的检索记录历史数据，浏览轨迹页面喜好内容资产及应用本地歌单资产集合，只接受依赖采用由本地系统驱动空间做非集中单向加密物理写读保护保管动作运行！本机构团队未搭建也决不对客户端用户实施或利用其它第三方跟踪等私自上传云服务器动作提取窥视手段窃盗行为了解此数据隐私秘密资源管理。\n\n" +
                        "• 外接公有链拉流与凭条鉴据对接环境触发操作场景：在使用依赖爬拉云检索或者通过导入功能做曲目与内容资产获取下载以及数据展示串联解析中。我们需要按照云源要求投寄相关的验证和搜索字符资源特征字。此间所依赖产生的行为活动由于跨网络投取，完全采用脱密脱身份直接寻址方案或匿名机制发送请求保障防伪劣风险泄漏漏洞等！同时第三方平台的联动服务(即云账本服务支持),也全由端本地持久文件在密信授权环节作唯一加密保封凭留，严防云授权失窃！"
            )

            PolicySection(
                title = "2. 服务连接状态前置不可抗地理阻碍风险提醒及说明设定指引。",
                content = "由于多源应用媒体服务端与应用所依靠的大部分开源音乐分发接口本身服务范围对华境内及相应封闭审查防火地域受阻屏蔽现象频发或严格采取隔离！此类网络特性是系统设计应用本身非主动主观设下的门槛或破坏缺陷阻碍事实体验原因故障造成所形成。\n如果您面临受地理路由节点访问拦截现象从而致使列表展现失灵,流源错误或接口调用连接失窃的场景；需您充分明确具备一定的环境自主解决自保意识前置或在入库首配置下合理引入具备网络放行效果设置项做规避限制。针对该技术类连通行前设影响责任需提前做好告知防微且自付。"
            )

            PolicySection(
                title = "3. 用户环境相关赋予特权配置调用范围说明解释管控说明管理办法机制管理规则实施内容解释。",
                content = "华音本身仅依靠维持合理操作正常音乐执行过程去使用对应的必要功能操作系统软权支持包括对后台存流任务和音响硬件音频聚焦或视听缓存管控写入许可的支持；杜绝过分的采集读取和过境隐私索查扫描（不设后门暗道调用获取录音麦克文件图像与信息流拦截！），对于敏感资源只采取绝对明确的主动作提示拦截机制防止误操窃界。"
            )

            PolicySection(
                title = "4. 有关法律性质服务准约条款",
                content = "• 本品基于无直接广告获利驱动的清爽无追踪纯开发分享，其软件一切的资源归从互联网外部公允搜索以及第三云链接拉取获取展示。不得基于此类渠道套用资源工具化应用软件拿用于谋做收费私立等非分不正商企经营暴取及衍生获利目的行为从事黑色运营项目牟作手段活动使用服务产生！使用者发生或牵发该系列问题所衍生风险问题概与开发维推团队没有且拒绝担系连坐一切责任承压后果。\n" +
                        "• 用户不可以使用相关系统抓爬破坏服务器规度恶意攻击瘫流资源获取接口稳定等极刑影响公众利益或实施有害内容服务平台数据活动传播恶毒非法影响内容发布宣贯等一切禁违禁破坏！若因您不良恶意违纪操引流封接口服务故障现象发生将必须付连全部民管后果自费负责惩定管理等影响行为并立即自行解除注销放弃使用的特权力使用效。"
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "华音 组委声明维护颁发",
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