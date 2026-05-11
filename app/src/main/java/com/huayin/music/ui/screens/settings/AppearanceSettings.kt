package com.huayin.music.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huayin.music.R
import com.huayin.music.constants.*
import com.huayin.music.ui.component.*
import com.huayin.music.utils.rememberEnumPreference
import com.huayin.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppearanceSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (dynamicTheme, onDynamicThemeChange) = rememberPreference(DynamicThemeKey, true)
    val (darkMode, onDarkModeChange) = rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val (pureBlack, onPureBlackChange) = rememberPreference(PureBlackKey, false)
    val (sliderStyle, onSliderStyleChange) = rememberEnumPreference(SliderStyleKey, SliderStyle.SQUIGGLY)
    
    // Shape State Collection (Fixed from previous turn)
    val smallButtonsShape by rememberPreference(SmallButtonsShapeKey, DefaultSmallButtonsShape)
    val miniPlayerShape by rememberPreference(MiniPlayerThumbnailShapeKey, DefaultMiniPlayerThumbnailShape)

    SettingsPage(
        title = stringResource(R.string.appearance),
        navController = navController,
        scrollBehavior = scrollBehavior
    ) {
        SettingsGeneralCategory(
            title = stringResource(R.string.theme),
            items = listOf(
                { SwitchPreference(
                    title = { Text(stringResource(R.string.enable_dynamic_theme)) },
                    icon = { Icon(painterResource(R.drawable.palette), null) },
                    checked = dynamicTheme,
                    onCheckedChange = onDynamicThemeChange
                )},
                { EnumListPreference(
                    title = { Text(stringResource(R.string.dark_theme)) },
                    icon = { Icon(painterResource(R.drawable.dark_mode), null) },
                    selectedValue = darkMode,
                    onValueSelected = onDarkModeChange,
                    valueText = {
                        when (it) {
                            DarkMode.ON -> "On"
                            DarkMode.OFF -> "Off"
                            else -> "Follow System"
                        }
                    }
                )},
                { SwitchPreference(
                    title = { Text(stringResource(R.string.pure_black)) },
                    icon = { Icon(painterResource(R.drawable.contrast), null) },
                    checked = pureBlack,
                    onCheckedChange = onPureBlackChange,
                    isEnabled = darkMode != DarkMode.OFF
                )}
            )
        )

        SettingsGeneralCategory(
            title = "Layout & Shapes",
            items = listOf(
                { UnifiedShapeSelectorButton(
                    smallButtonsShape = smallButtonsShape,
                    miniPlayerShape = miniPlayerShape,
                    onSmallButtonsShapeSelected = { /* Logic to save via DataStore */ },
                    onMiniPlayerShapeSelected = { /* Logic to save via DataStore */ }
                )},
                { EnumListPreference(
                    title = { Text("Seek Bar Style") },
                    icon = { Icon(painterResource(R.drawable.sliders), null) },
                    selectedValue = sliderStyle,
                    onValueSelected = onSliderStyleChange,
                    valueText = { it.name.lowercase().capitalize() }
                )},
                { ThumbnailCornerRadiusSelectorButton { /* Logic to save */ } }
            )
        )

        // Language & Avatars at the bottom
        SettingsGeneralCategory(
            title = "Personalization",
            items = listOf(
                { LanguagePreference() }
            )
        )

        AvatarSelector(modifier = Modifier.padding(16.dp))
    }
}