package com.huayin.music.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import me.saket.squiggles.SquigglySlider

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

    val (lyricsPosition, onLyricsPositionChange) = rememberEnumPreference(LyricsTextPositionKey, LyricsPosition.CENTER)
    val (animateLyrics, onAnimateLyricsChange) = rememberPreference(AnimateLyricsKey, true)

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
                    description = sliderStyle.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    icon = { Icon(painterResource(R.drawable.sliders), null) },
                    onClick = { showSliderStyleSheet = true }
                )},
                { ThumbnailCornerRadiusSelectorButton { /* Value is saved directly inside the component */ } }
            )
        )

        SettingsGeneralCategory(
            title = "Personalization",
            items = listOf(
                { LanguagePreference() },
                { EnumListPreference(
                    title = { Text("Lyrics Alignment") },
                    icon = { Icon(painterResource(R.drawable.format_align_center), null) },
                    selectedValue = lyricsPosition,
                    onValueSelected = onLyricsPositionChange,
                    valueText = { it.name }
                )},
                { SwitchPreference(
                    title = { Text("Animate Lyrics") },
                    icon = { Icon(painterResource(R.drawable.music_note), null) }, // Fixed Unresolved reference
                    checked = animateLyrics,
                    onCheckedChange = onAnimateLyricsChange
                )}
            )
        )

        AvatarSelector(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        )
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
        ) {
            Text(
                text = "Seek Bar Style",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            SliderStyle.entries.forEach { style ->
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
                            text = style.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Mock preview to ensure 100% compilation
                        when (style) {
                            SliderStyle.DEFAULT -> {
                                Slider(
                                    value = 0.5f,
                                    onValueChange = {},
                                    enabled = false,
                                    colors = SliderDefaults.colors(
                                        disabledThumbColor = MaterialTheme.colorScheme.primary,
                                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                                        disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            }
                            SliderStyle.SQUIGGLY -> {
                                SquigglySlider(
                                    value = 0.5f,
                                    valueRange = 0f..1f,
                                    onValueChange = {},
                                    enabled = false,
                                    colors = SliderDefaults.colors(
                                        disabledThumbColor = MaterialTheme.colorScheme.primary,
                                        disabledActiveTrackColor = MaterialTheme.colorScheme.primary,
                                        disabledInactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    squigglesSpec = SquigglySlider.SquigglesSpec(
                                        amplitude = 2.dp,
                                        strokeWidth = 3.dp
                                    )
                                )
                            }
                            SliderStyle.SLIM -> {
                                LinearProgressIndicator(
                                    progress = { 0.5f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
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