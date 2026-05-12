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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val (dynamicTheme, onDynamicThemeChange) = rememberPreference(DynamicThemeKey, true)
    val (darkMode, onDarkModeChange) = rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val (pureBlack, onPureBlackChange) = rememberPreference(PureBlackKey, false)
    val (sliderStyle, onSliderStyleChange) = rememberEnumPreference(SliderStyleKey, SliderStyle.SQUIGGLY)
    
    val (smallButtonsShape, onSmallButtonsShapeChange) = rememberPreference(SmallButtonsShapeKey, DefaultSmallButtonsShape)
    val (miniPlayerShape, onMiniPlayerShapeChange) = rememberPreference(MiniPlayerThumbnailShapeKey, DefaultMiniPlayerThumbnailShape)

    var showSliderStyleSheet by remember { mutableStateOf(false) }

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
                    onSmallButtonsShapeSelected = onSmallButtonsShapeChange,
                    onMiniPlayerShapeSelected = onMiniPlayerShapeChange
                )},
                { PreferenceEntry(
                    title = { Text("Seek Bar Style") },
                    description = sliderStyle.name.lowercase().capitalize(),
                    icon = { Icon(painterResource(R.drawable.sliders), null) },
                    onClick = { showSliderStyleSheet = true }
                )},
                { ThumbnailCornerRadiusSelectorButton { /* Value is saved directly inside the component */ } }
            )
        )

        SettingsGeneralCategory(
            title = "Personalization",
            items = listOf(
                { LanguagePreference() }
            )
        )

        AvatarSelector(modifier = Modifier.padding(16.dp))
    }

    if (showSliderStyleSheet) {
        SliderStyleSelectorBottomSheet(
            selectedStyle = sliderStyle,
            onStyleSelected = {
                onSliderStyleChange(it)
                showSliderStyleSheet = false
            },
            onDismiss = { showSliderStyleSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderStyleSelectorBottomSheet(
    selectedStyle: SliderStyle,
    onStyleSelected: (SliderStyle) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Seek Bar Style",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderStyle.values().forEach { style ->
                val isSelected = style == selectedStyle
                Card(
                    onClick = { onStyleSelected(style) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = style.name.lowercase().capitalize(),
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Preview
                        com.huayin.music.ui.screens.settings.EnhancedProgressBar(
                            position = 5000,
                            duration = 10000,
                            isPlaying = true,
                            sliderStyle = style
                        )
                    }
                }
            }
        }
    }
}

enum class DarkMode { ON, OFF, AUTO }
enum class NavigationTab { HOME, EXPLORE, LIBRARY }
enum class LyricsPosition { LEFT, CENTER, RIGHT }
enum class PlayerTextAlignment { SIDED, CENTER }