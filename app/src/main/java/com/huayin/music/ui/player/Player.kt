package com.huayin.music.ui.player

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.audiofx.AudioEffect
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.huayin.music.LocalDownloadUtil
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.R
import com.huayin.music.constants.BlurRadiusKey
import com.huayin.music.constants.DarkModeKey
import com.huayin.music.constants.DefaultPlayPauseButtonShape
import com.huayin.music.constants.DefaultSmallButtonsShape
import com.huayin.music.constants.DisableBlurKey
import com.huayin.music.constants.PlayerBackgroundStyle
import com.huayin.music.constants.PlayerBackgroundStyleKey
import com.huayin.music.constants.PlayerButtonsStyle
import com.huayin.music.constants.PlayerButtonsStyleKey
import com.huayin.music.constants.PlayerCustomBlurKey
import com.huayin.music.constants.PlayerCustomBrightnessKey
import com.huayin.music.constants.PlayerCustomContrastKey
import com.huayin.music.constants.PlayerCustomImageUriKey
import com.huayin.music.constants.PlayerDesignStyle
import com.huayin.music.constants.PlayerDesignStyleKey
import com.huayin.music.constants.PlayerHorizontalPadding
import com.huayin.music.constants.PlayPauseButtonShapeKey
import com.huayin.music.constants.PureBlackKey
import com.huayin.music.constants.QueuePeekHeight
import com.huayin.music.constants.SeekExtraSeconds
import com.huayin.music.constants.SliderStyle
import com.huayin.music.constants.SliderStyleKey
import com.huayin.music.constants.SmallButtonsShapeKey
import com.huayin.music.db.entities.FormatEntity
import com.huayin.music.db.entities.Song
import com.huayin.music.extensions.togglePlayPause
import com.huayin.music.extensions.toggleRepeatMode
import com.huayin.music.models.MediaMetadata
import com.huayin.music.playback.PlayerConnection
import com.huayin.music.ui.component.BottomSheet
import com.huayin.music.ui.component.BottomSheetPageState
import com.huayin.music.ui.component.BottomSheetState
import com.huayin.music.ui.component.LocalBottomSheetPageState
import com.huayin.music.ui.component.LocalMenuState
import com.huayin.music.ui.component.Lyrics
import com.huayin.music.ui.component.MenuState
import com.huayin.music.ui.component.PlayerSliderTrack
import com.huayin.music.ui.component.ResizableIconButton
import com.huayin.music.ui.component.rememberBottomSheetState
import com.huayin.music.ui.menu.PlayerMenu
import com.huayin.music.ui.screens.settings.DarkMode
import com.huayin.music.ui.theme.PlayerColorExtractor
import com.huayin.music.ui.utils.ShowMediaInfo
import com.huayin.music.utils.getPlayPauseShape
import com.huayin.music.utils.getSmallButtonShape
import com.huayin.music.utils.makeTimeString
import com.huayin.music.utils.rememberEnumPreference
import com.huayin.music.utils.rememberPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.saket.squiggles.SquigglySlider
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap

private const val SeekbarSettleToleranceMs = 1_500L
private const val V7BackdropBlurHeightFraction = 0.54f

object PlayerBackgroundColorUtils {
    fun buildBlurOverlayStops(gradientColors: List<Color>): Array<Pair<Float, Color>> {
        return arrayOf(
            0.0f to Color.Transparent,
            0.5f to Color.Black.copy(alpha = 0.2f),
            1.0f to Color.Black.copy(alpha = 0.7f)
        )
    }

    fun buildColoringStops(baseColor: Color): Array<Pair<Float, Color>> {
        return arrayOf(
            0.0f to baseColor.copy(alpha = 0.8f),
            1.0f to Color.Black.copy(alpha = 0.8f)
        )
    }

    fun buildBlurGradientStops(gradientColors: List<Color>): Array<Pair<Float, Color>> {
        val c1 = gradientColors.getOrElse(0) { Color.DarkGray }
        val c2 = gradientColors.getOrElse(1) { Color.Black }
        return arrayOf(
            0.0f to c1.copy(alpha = 0.5f),
            1.0f to c2.copy(alpha = 0.8f)
        )
    }

    fun ensureComfortableColor(color: Color): Color = color
}

object PlayerSliderColors {
    @Composable
    fun standardSliderColors(activeColor: Color) = SliderDefaults.colors(
        activeTrackColor = activeColor,
        inactiveTrackColor = activeColor.copy(alpha = 0.3f),
        thumbColor = activeColor
    )
    @Composable
    fun wavySliderColors(activeColor: Color) = SliderDefaults.colors(
        activeTrackColor = activeColor,
        inactiveTrackColor = activeColor.copy(alpha = 0.3f),
        thumbColor = activeColor
    )
    @Composable
    fun thickSliderColors(activeColor: Color) = SliderDefaults.colors(
        activeTrackColor = activeColor,
        inactiveTrackColor = activeColor.copy(alpha = 0.3f),
        thumbColor = Color.Transparent
    )
    @Composable
    fun circularSliderColors(activeColor: Color) = SliderDefaults.colors(
        activeTrackColor = activeColor,
        inactiveTrackColor = activeColor.copy(alpha = 0.3f),
        thumbColor = activeColor
    )
    @Composable
    fun simpleSliderColors(activeColor: Color) = SliderDefaults.colors(
        activeTrackColor = activeColor,
        inactiveTrackColor = activeColor.copy(alpha = 0.3f),
        thumbColor = Color.Transparent
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    onOpenFullscreenLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val menuState = LocalMenuState.current

    val bottomSheetPageState = LocalBottomSheetPageState.current

    val playerConnection = LocalPlayerConnection.current ?: return

    val playerDesignStyle by rememberEnumPreference(
        key = PlayerDesignStyleKey,
        defaultValue = PlayerDesignStyle.V4
    )
    
    val playerBackground by rememberEnumPreference(
        key = PlayerBackgroundStyleKey,
        defaultValue = PlayerBackgroundStyle.DEFAULT
    )

    val (playerCustomImageUri) = rememberPreference(PlayerCustomImageUriKey, "")
    val (playerCustomBlur) = rememberPreference(PlayerCustomBlurKey, 0f)
    val (playerCustomContrast) = rememberPreference(PlayerCustomContrastKey, 1f)
    val (playerCustomBrightness) = rememberPreference(PlayerCustomBrightnessKey, 1f)
    
    val (disableBlur) = rememberPreference(DisableBlurKey, false)
    val (blurRadius) = rememberPreference(BlurRadiusKey, 36f)
    val (showCodecOnPlayer) = rememberPreference(androidx.datastore.preferences.core.booleanPreferencesKey("show_codec_on_player"), false)
    val (incrementalSeekSkipEnabled) = rememberPreference(SeekExtraSeconds, defaultValue = false)
    
    val pureBlack by rememberPreference(PureBlackKey, defaultValue = false)
    
    var keyboardSkipMultiplier by remember { mutableStateOf(1) }
    var lastKeyboardTapTime by remember { mutableLongStateOf(0L) }

    val playerButtonsStyle by rememberEnumPreference(
        key = PlayerButtonsStyleKey,
        defaultValue = PlayerButtonsStyle.DEFAULT
    )

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, defaultValue = DarkMode.AUTO)
    val useDarkTheme = remember(darkTheme, isSystemInDarkTheme) {
        if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
    }
    
    val onBackgroundColor = when (playerBackground) {
        PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.secondary
        else ->
            if (useDarkTheme)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onPrimary
    }
    
    val useBlackBackground =
        remember(isSystemInDarkTheme, darkTheme, pureBlack) {
            val useDark =
                if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON
            useDark && pureBlack
        }

    val playbackState by playerConnection.playbackState.collectAsState()
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val currentSongLiked = currentSong?.song?.liked == true
    val currentFormat by playerConnection.currentFormat.collectAsState(initial = null)
    val queueWindows by playerConnection.queueWindows.collectAsState()
    val currentWindowIndex by playerConnection.currentWindowIndex.collectAsState()
    val playerVolume = playerConnection.service.playerVolume.collectAsState()

    val repeatMode by playerConnection.repeatMode.collectAsState()

    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()

    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.Standard)

    var position by rememberSaveable(mediaMetadata?.id) {
        mutableLongStateOf(playerConnection.player.currentPosition)
    }
    var duration by rememberSaveable(mediaMetadata?.id) {
        mutableLongStateOf(playerConnection.player.duration)
    }
    var sliderPosition by remember(mediaMetadata?.id) {
        mutableStateOf<Long?>(null)
    }
    var isUserSeeking by remember(mediaMetadata?.id) {
        mutableStateOf(false)
    }
    
    val isLoading = playbackState == STATE_BUFFERING || sliderPosition != null

    var gradientColors by remember {
        mutableStateOf<List<Color>>(emptyList())
    }
    
    var previousThumbnailUrl by remember { mutableStateOf<String?>(null) }
    var previousGradientColors by remember { mutableStateOf<List<Color>>(emptyList()) }
    
    val gradientColorsCache = remember { mutableMapOf<String, List<Color>>() }

    val defaultGradientColors = listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    val fallbackColor = MaterialTheme.colorScheme.surface.toArgb()
    
    LaunchedEffect(mediaMetadata?.id) {
        val currentThumbnail = mediaMetadata?.thumbnailUrl
        if (currentThumbnail != previousThumbnailUrl) {
            previousThumbnailUrl = currentThumbnail
            previousGradientColors = gradientColors
        }
    }
    
    LaunchedEffect(mediaMetadata?.id, playerBackground) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT || playerBackground == PlayerBackgroundStyle.COLORING || playerBackground == PlayerBackgroundStyle.BLUR_GRADIENT || playerBackground == PlayerBackgroundStyle.GLOW || playerBackground == PlayerBackgroundStyle.GLOW_ANIMATED) {
            val currentMetadata = mediaMetadata
            if (currentMetadata != null && currentMetadata.thumbnailUrl != null) {
                val cachedColors = gradientColorsCache[currentMetadata.id]
                if (cachedColors != null) {
                    gradientColors = cachedColors
                } else {
                    val request = ImageRequest.Builder(context)
                        .data(currentMetadata.thumbnailUrl)
                        .size(400, 400) // Fallback constants
                        .allowHardware(false)
                        .build()

                    val result = runCatching {
                        withContext(Dispatchers.IO) {
                            context.imageLoader.execute(request)
                        }
                    }.getOrNull()

                    if (result != null) {
                        val bitmap = result.drawable?.toBitmap()
                        if (bitmap != null) {
                            val palette = withContext(Dispatchers.Default) {
                                Palette.from(bitmap)
                                    .maximumColorCount(24)
                                    .resizeBitmapArea(22500)
                                    .generate()
                            }

                            val extractedColors = PlayerColorExtractor.extractGradientColors(
                                palette = palette,
                                fallbackColor = fallbackColor
                            )

                            gradientColorsCache[currentMetadata.id] = extractedColors
                            gradientColors = extractedColors
                        } else {
                            gradientColors = defaultGradientColors
                        }
                    } else {
                        gradientColors = defaultGradientColors
                    }
                }
            } else {
                gradientColors = emptyList()
            }
        } else {
            gradientColors = emptyList()
        }
    }

    val TextBackgroundColor =
        if (playerDesignStyle == PlayerDesignStyle.V7) Color.White
        else when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.onBackground
            PlayerBackgroundStyle.BLUR -> Color.White
            PlayerBackgroundStyle.GRADIENT -> Color.White
            PlayerBackgroundStyle.COLORING -> Color.White
            PlayerBackgroundStyle.BLUR_GRADIENT -> Color.White
            PlayerBackgroundStyle.GLOW -> Color.White
            PlayerBackgroundStyle.GLOW_ANIMATED -> Color.White
            PlayerBackgroundStyle.CUSTOM -> Color.White
        }

    val icBackgroundColor =
        if (playerDesignStyle == PlayerDesignStyle.V7) Color.Black
        else when (playerBackground) {
            PlayerBackgroundStyle.DEFAULT -> MaterialTheme.colorScheme.surface
            PlayerBackgroundStyle.BLUR -> Color.Black
            PlayerBackgroundStyle.GRADIENT -> Color.Black
            PlayerBackgroundStyle.COLORING -> Color.Black
            PlayerBackgroundStyle.BLUR_GRADIENT -> Color.Black
            PlayerBackgroundStyle.GLOW -> Color.Black
            PlayerBackgroundStyle.GLOW_ANIMATED -> Color.Black
            PlayerBackgroundStyle.CUSTOM -> Color.Black
        }

    val (textButtonColor, iconButtonColor) = when (playerButtonsStyle) {
        PlayerButtonsStyle.DEFAULT -> Pair(TextBackgroundColor, icBackgroundColor)
        PlayerButtonsStyle.SECONDARY -> Pair(
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.onSecondary
        )
        PlayerButtonsStyle.PRIMARY -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        PlayerButtonsStyle.TERTIARY -> Pair(
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.onTertiary
        )
    }.let { (tb, ib) ->
        if (playerDesignStyle == PlayerDesignStyle.V7) Pair(Color.White, Color.Black) else Pair(tb, ib)
    }

    val download by LocalDownloadUtil.current.getDownload(mediaMetadata?.id ?: "")
        .collectAsState(initial = null)

    var showDetailsDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(mediaMetadata?.id, playbackState) {
        val startTime = SystemClock.elapsedRealtime()
        if (playbackState == STATE_READY) {
            while (isActive) {
                delay(100)
                val isTransitioning = playerConnection.player.currentMediaItem?.mediaId != mediaMetadata?.id
                val currentPlayerPosition = playerConnection.player.currentPosition
                val currentPlayerDuration = playerConnection.player.duration
                
                if (isTransitioning) {
                    val elapsedSinceStart = SystemClock.elapsedRealtime() - startTime
                    position = elapsedSinceStart
                    mediaMetadata?.let {
                        val metaDuration = it.duration.toLong() * 1000
                        duration = if (metaDuration > 0) metaDuration else 0L
                    }
                } else {
                    position = currentPlayerPosition
                    duration = currentPlayerDuration
                    if (!isUserSeeking) {
                        sliderPosition?.let { targetPosition ->
                            val clampedTargetPosition = when {
                                currentPlayerDuration > 0L && currentPlayerDuration != C.TIME_UNSET -> {
                                    targetPosition.coerceIn(0L, currentPlayerDuration)
                                }
                                else -> targetPosition.coerceAtLeast(0L)
                            }
                            if (abs(currentPlayerPosition - clampedTargetPosition) <= SeekbarSettleToleranceMs) {
                                sliderPosition = null
                            }
                        }
                    }
                }
            }
        } else {
            mediaMetadata?.let {
                val metaDuration = it.duration.toLong() * 1000
                duration = if (metaDuration > 0) metaDuration else 0L
            }
            val currentPlayerPosition = playerConnection.player.currentPosition
            if (sliderPosition == null && currentPlayerPosition > 0L) {
                position = currentPlayerPosition
            }
        }
    }

    val dynamicQueuePeekHeight =
        if (playerDesignStyle == PlayerDesignStyle.V5) {
            0.dp
        } else if (showCodecOnPlayer) {
            88.dp
        } else {
            QueuePeekHeight
        }

    val dismissedBound = dynamicQueuePeekHeight + WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    val queueSheetState = rememberBottomSheetState(
        dismissedBound = dismissedBound,
        expandedBound = state.expandedBound,
        collapsedBound = dismissedBound,
        initialAnchor = 0
    )
    
    val lyricsSheetState = rememberBottomSheetState(
        dismissedBound = 0.dp,
        expandedBound = state.expandedBound,
        collapsedBound = 0.dp,
        initialAnchor = 1
    )

    BackHandler(
        enabled = (!lyricsSheetState.isCollapsed && !lyricsSheetState.isDismissed) ||
                  (!queueSheetState.isCollapsed && !queueSheetState.isDismissed) ||
                  (!state.isCollapsed && !state.isDismissed)
    ) {
        when {
            !lyricsSheetState.isCollapsed && !lyricsSheetState.isDismissed -> lyricsSheetState.collapseSoft()
            !queueSheetState.isCollapsed && !queueSheetState.isDismissed -> queueSheetState.collapseSoft()
            !state.isCollapsed && !state.isDismissed -> state.collapseSoft()
        }
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.isExpanded) {
        if (state.isExpanded) {
            focusRequester.requestFocus()
        }
    }

    if (showDetailsDialog) {
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { showDetailsDialog = false },
            icon = {
                Icon(
                    painter = painterResource(R.drawable.info),
                    contentDescription = null,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDetailsDialog = false },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .sizeIn(minWidth = 280.dp, maxWidth = 560.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    listOf(
                        stringResource(R.string.song_title) to mediaMetadata?.title,
                        stringResource(R.string.song_artists) to mediaMetadata?.artists?.joinToString { it.name },
                        stringResource(R.string.media_id) to mediaMetadata?.id,
                        "Itag" to currentFormat?.itag?.toString(),
                        stringResource(R.string.mime_type) to currentFormat?.mimeType,
                        stringResource(R.string.codecs) to currentFormat?.codecs,
                        stringResource(R.string.bitrate) to currentFormat?.bitrate?.let { "${it / 1000} Kbps" },
                        stringResource(R.string.sample_rate) to currentFormat?.sampleRate?.let { "$it Hz" },
                        stringResource(R.string.loudness) to currentFormat?.loudnessDb?.let { "$it dB" },
                        stringResource(R.string.volume) to "${(playerConnection.player.volume * 100).toInt()}%",
                    ).forEach { (label, text) ->
                        val displayText = text ?: stringResource(R.string.unknown)
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = displayText,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    clipboardManager.setPrimaryClip(ClipData.newPlainText(label, displayText))
                                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
                                },
                            ),
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            },
        )
    }

    BottomSheet(
        state = state,
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
            if (keyEvent.type != KeyEventType.KeyDown || state.isCollapsed) return@onKeyEvent false

            when (keyEvent.key) {
                Key.DirectionLeft -> {
                    val now = SystemClock.uptimeMillis()
                    if (incrementalSeekSkipEnabled && now - lastKeyboardTapTime < 1000) {
                        keyboardSkipMultiplier++
                    } else {
                        keyboardSkipMultiplier = 1
                    }
                    lastKeyboardTapTime = now
                    val skipAmount = 5000L * keyboardSkipMultiplier
                    playerConnection.player.seekTo((playerConnection.player.currentPosition - skipAmount).coerceAtLeast(0))
                    true
                }
                Key.DirectionRight -> {
                    val now = SystemClock.uptimeMillis()
                    if (incrementalSeekSkipEnabled && now - lastKeyboardTapTime < 1000) {
                        keyboardSkipMultiplier++
                    } else {
                        keyboardSkipMultiplier = 1
                    }
                    lastKeyboardTapTime = now
                    val skipAmount = 5000L * keyboardSkipMultiplier
                    playerConnection.player.seekTo((playerConnection.player.currentPosition + skipAmount).coerceAtMost(playerConnection.player.duration))
                    true
                }
                Key.DirectionUp -> {
                    playerConnection.service.playerVolume.value = (playerConnection.service.playerVolume.value + 0.05f).coerceAtMost(1f)
                    true
                }
                Key.DirectionDown -> {
                    playerConnection.service.playerVolume.value = (playerConnection.service.playerVolume.value - 0.05f).coerceAtLeast(0f)
                    true
                }
                Key.Spacebar -> {
                    playerConnection.player.togglePlayPause()
                    true
                }
                Key.N -> {
                    if (keyEvent.isShiftPressed) {
                        playerConnection.seekToNext()
                        true
                    } else false
                }
                Key.P -> {
                    if (keyEvent.isShiftPressed) {
                        playerConnection.seekToPrevious()
                        true
                    } else false
                }
                Key.L -> {
                    playerConnection.toggleLike()
                    true
                }
                else -> false
            }
        },
        backgroundColor = if (playerDesignStyle == PlayerDesignStyle.V7) {
            val progress = ((state.value - state.collapsedBound) / (state.expandedBound - state.collapsedBound))
                .coerceIn(0f, 1f)
            val fadeProgress = if (progress < 0.2f) {
                ((0.2f - progress) / 0.2f).coerceIn(0f, 1f)
            } else {
                0f
            }
            Color.Black.copy(alpha = 1f - fadeProgress)
        } else when (playerBackground) {
            PlayerBackgroundStyle.BLUR, PlayerBackgroundStyle.GRADIENT -> {
                val progress = ((state.value - state.collapsedBound) / (state.expandedBound - state.collapsedBound))
                    .coerceIn(0f, 1f)
                val fadeProgress = if (progress < 0.2f) {
                    ((0.2f - progress) / 0.2f).coerceIn(0f, 1f)
                } else {
                    0f
                }
                MaterialTheme.colorScheme.surface.copy(alpha = 1f - fadeProgress)
            }
            else -> {
                val progress = ((state.value - state.collapsedBound) / (state.expandedBound - state.collapsedBound))
                    .coerceIn(0f, 1f)
                val fadeProgress = if (progress < 0.2f) {
                    ((0.2f - progress) / 0.2f).coerceIn(0f, 1f)
                } else {
                    0f
                }
                if (useBlackBackground) {
                    Color.Black.copy(alpha = 1f - fadeProgress)
                } else {
                    MaterialTheme.colorScheme.surface.copy(alpha = 1f - fadeProgress)
                }
            }
        },
        onDismiss = {
            playerConnection.player.stop()
            playerConnection.player.clearMediaItems()
        },
        collapsedContent = {
            MiniPlayer(
                position = position,
                duration = duration,
                pureBlack = pureBlack,
            )
        },
    ) {
        val onSliderValueChange: (Long) -> Unit = {
            isUserSeeking = true
            sliderPosition = it
        }
        val onSliderValueChangeFinished: () -> Unit = {
            sliderPosition?.let {
                val isTransitioning = playerConnection.player.currentMediaItem?.mediaId != mediaMetadata?.id
                if (isTransitioning) {
                    playerConnection.player.seekToNext()
                    playerConnection.player.seekTo(it)
                } else {
                    playerConnection.player.seekTo(it)
                }
                position = it
            }
            isUserSeeking = false
        }
        val seekEnabled = duration > 0L && duration != C.TIME_UNSET
        val updatedOnSliderValueChange by rememberUpdatedState(onSliderValueChange)
        val updatedOnSliderValueChangeFinished by rememberUpdatedState(onSliderValueChangeFinished)

        val enrichedMetadata = remember(mediaMetadata, currentSong) {
            val meta = mediaMetadata ?: return@remember null
            if (meta.album != null) return@remember meta
            val dbAlbum = currentSong?.album
            val dbAlbumId = currentSong?.song?.albumId
            when {
                dbAlbum != null -> meta.copy(
                    album = MediaMetadata.Album(id = dbAlbum.id, title = dbAlbum.title)
                )
                dbAlbumId != null -> meta.copy(
                    album = MediaMetadata.Album(
                        id = dbAlbumId,
                        title = currentSong?.song?.albumName.orEmpty()
                    )
                )
                else -> meta
            }
        }

        val controlsContent: @Composable ColumnScope.(MediaMetadata) -> Unit = { mediaMetadata ->
            PlayerControlsContent(
                mediaMetadata = mediaMetadata,
                playerDesignStyle = playerDesignStyle,
                sliderStyle = sliderStyle,
                playbackState = playbackState,
                isPlaying = isPlaying,
                isLoading = isLoading,
                repeatMode = repeatMode,
                canSkipPrevious = canSkipPrevious,
                canSkipNext = canSkipNext,
                textButtonColor = textButtonColor,
                iconButtonColor = iconButtonColor,
                textBackgroundColor = TextBackgroundColor,
                icBackgroundColor = icBackgroundColor,
                sliderPosition = sliderPosition,
                position = position,
                duration = duration,
                playerConnection = playerConnection,
                navController = navController,
                state = state,
                menuState = menuState,
                bottomSheetPageState = bottomSheetPageState,
                clipboardManager = clipboardManager,
                context = context,
                onSliderValueChange = onSliderValueChange,
                onSliderValueChangeFinished = onSliderValueChangeFinished,
                currentFormat = if (playerDesignStyle == PlayerDesignStyle.V7) currentFormat else null,
            )
        }

        if (!state.isCollapsed && playerDesignStyle != PlayerDesignStyle.V5 && playerDesignStyle != PlayerDesignStyle.V7) {
            PlayerBackground(
                playerBackground = playerBackground,
                mediaMetadata = mediaMetadata,
                gradientColors = gradientColors,
                disableBlur = disableBlur,
                blurRadius = blurRadius,
                playerCustomImageUri = playerCustomImageUri,
                playerCustomBlur = playerCustomBlur,
                playerCustomContrast = playerCustomContrast,
                playerCustomBrightness = playerCustomBrightness
            )
        }

        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (playerDesignStyle == PlayerDesignStyle.V5) {
                    val littleBackground = MaterialTheme.colorScheme.primaryContainer
                    val littleTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                    val displayPositionMs = sliderPosition ?: position
                    val progressFraction =
                        remember(displayPositionMs, duration) {
                            if (duration <= 0L || duration == C.TIME_UNSET) 0f
                            else (displayPositionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                        }
                    val progressOverlayColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(littleBackground),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(progressFraction)
                                .align(Alignment.TopStart)
                                .background(progressOverlayColor),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .littlePlayerOverlayGestures(
                                    seekEnabled = seekEnabled,
                                    durationMs = duration,
                                    progressFraction = progressFraction,
                                    canSkipPrevious = canSkipPrevious,
                                    canSkipNext = canSkipNext,
                                    onSeekToPositionMs = updatedOnSliderValueChange,
                                    onSeekFinished = updatedOnSliderValueChangeFinished,
                                    onSkipPrevious = playerConnection::seekToPrevious,
                                    onSkipNext = playerConnection::seekToNext,
                                )
                                .windowInsetsPadding(
                                    WindowInsets.systemBars.only(
                                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top + WindowInsetsSides.Bottom
                                    )
                                ),
                        ) {
                            enrichedMetadata?.let { metadata ->
                                LittlePlayerContent(
                                    mediaMetadata = metadata,
                                    sliderPosition = sliderPosition,
                                    positionMs = position,
                                    durationMs = duration,
                                    textColor = littleTextColor,
                                    liked = currentSongLiked,
                                    onCollapse = state::collapseSoft,
                                    onToggleLike = playerConnection::toggleLike,
                                    onExpandQueue = queueSheetState::expandSoft,
                                    onMenuClick = {
                                        menuState.show {
                                            PlayerMenu(
                                                mediaMetadata = metadata,
                                                navController = navController,
                                                playerBottomSheetState = state,
                                                onShowDetailsDialog = {
                                                    showDetailsDialog = true
                                                },
                                                onDismiss = menuState::dismiss
                                            )
                                        }
                                    },
                                )
                            }
                        }
                    }
                } else if (playerDesignStyle == PlayerDesignStyle.V7) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        V7PlayerBackdrop(
                            thumbnailUrl = mediaMetadata?.thumbnailUrl,
                            disableBlur = disableBlur,
                            label = "v7BackdropLandscape",
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = queueSheetState.collapsedBound)
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                                .nestedScroll(state.preUpPostDownNestedScrollConnection),
                        ) {
                            enrichedMetadata?.let { controlsContent(it) }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                            .padding(bottom = queueSheetState.collapsedBound + 48.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f),
                        ) {
                            val screenWidth = LocalConfiguration.current.screenWidthDp
                            val thumbnailSize = (screenWidth * 0.4).dp
                            Thumbnail(
                                sliderPositionProvider = { sliderPosition },
                                modifier = Modifier.size(thumbnailSize),
                                isPlayerExpanded = state.isExpanded
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top)),
                        ) {
                            Spacer(Modifier.weight(1f))
                            enrichedMetadata?.let { controlsContent(it) }
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            else -> {
                if (playerDesignStyle == PlayerDesignStyle.V5) {
                    val littleBackground = MaterialTheme.colorScheme.primaryContainer
                    val littleTextColor = MaterialTheme.colorScheme.onPrimaryContainer
                    val displayPositionMs = sliderPosition ?: position
                    val progressFraction =
                        remember(displayPositionMs, duration) {
                            if (duration <= 0L || duration == C.TIME_UNSET) 0f
                            else (displayPositionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                        }
                    val progressOverlayColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(littleBackground),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(progressFraction)
                                .align(Alignment.TopStart)
                                .background(progressOverlayColor),
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .littlePlayerOverlayGestures(
                                    seekEnabled = seekEnabled,
                                    durationMs = duration,
                                    progressFraction = progressFraction,
                                    canSkipPrevious = canSkipPrevious,
                                    canSkipNext = canSkipNext,
                                    onSeekToPositionMs = updatedOnSliderValueChange,
                                    onSeekFinished = updatedOnSliderValueChangeFinished,
                                    onSkipPrevious = playerConnection::seekToPrevious,
                                    onSkipNext = playerConnection::seekToNext,
                                )
                                .windowInsetsPadding(
                                    WindowInsets.systemBars.only(
                                        WindowInsetsSides.Horizontal + WindowInsetsSides.Top + WindowInsetsSides.Bottom
                                    )
                                ),
                        ) {
                            enrichedMetadata?.let { metadata ->
                                LandscapeLikeBox(modifier = Modifier.fillMaxSize()) {
                                    LittlePlayerContent(
                                        mediaMetadata = metadata,
                                        sliderPosition = sliderPosition,
                                        positionMs = position,
                                        durationMs = duration,
                                        textColor = littleTextColor,
                                        liked = currentSongLiked,
                                        onCollapse = state::collapseSoft,
                                        onToggleLike = playerConnection::toggleLike,
                                        onExpandQueue = queueSheetState::expandSoft,
                                        onMenuClick = {
                                            menuState.show {
                                                PlayerMenu(
                                                    mediaMetadata = metadata,
                                                    navController = navController,
                                                    playerBottomSheetState = state,
                                                    onShowDetailsDialog = {
                                                        showDetailsDialog = true
                                                    },
                                                    onDismiss = menuState::dismiss
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                } else if (playerDesignStyle == PlayerDesignStyle.V7) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        V7PlayerBackdrop(
                            thumbnailUrl = mediaMetadata?.thumbnailUrl,
                            disableBlur = disableBlur,
                            label = "v7BackdropPortrait",
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = queueSheetState.collapsedBound)
                                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                                .nestedScroll(state.preUpPostDownNestedScrollConnection),
                        ) {
                            enrichedMetadata?.let { controlsContent(it) }
                            Spacer(Modifier.height(24.dp))
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                            .padding(bottom = queueSheetState.collapsedBound),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.weight(1f),
                        ) {
                            Thumbnail(
                                sliderPositionProvider = { sliderPosition },
                                modifier = Modifier.nestedScroll(state.preUpPostDownNestedScrollConnection),
                                isPlayerExpanded = state.isExpanded
                            )
                        }

                        enrichedMetadata?.let { controlsContent(it) }
                        Spacer(Modifier.height(30.dp))
                    }
                }
            }
        }

        val queueOnBackgroundColor = if (useBlackBackground) Color.White else MaterialTheme.colorScheme.onSurface
        val queueSurfaceColor = if (useBlackBackground) Color.Black else MaterialTheme.colorScheme.surface

        val (queueTextButtonColor, queueIconButtonColor) = when (playerButtonsStyle) {
            PlayerButtonsStyle.DEFAULT -> Pair(queueOnBackgroundColor, queueSurfaceColor)
            PlayerButtonsStyle.SECONDARY -> Pair(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.onSecondary)
            PlayerButtonsStyle.PRIMARY -> Pair(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            PlayerButtonsStyle.TERTIARY -> Pair(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
        }

        Queue(
            state = queueSheetState,
            playerBottomSheetState = state,
            navController = navController,
            backgroundColor = if (useBlackBackground) Color.Black else MaterialTheme.colorScheme.surfaceContainer,
            onBackgroundColor = queueOnBackgroundColor,
            textBackgroundColor = TextBackgroundColor,
        )

        mediaMetadata?.let { metadata ->
            BottomSheet(
                state = lyricsSheetState,
                backgroundColor = Color.Unspecified,
                onDismiss = { },
                collapsedContent = { }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.surface.copy(
                                alpha = lyricsSheetState.progress.coerceIn(0f, 1f)
                            )
                        )
                ) {
                    Lyrics(
                        sliderPositionProvider = { sliderPosition },
                        onNavigateBack = { lyricsSheetState.collapseSoft() },
                        mediaMetadata = metadata,
                        onBackClick = { lyricsSheetState.collapseSoft() }
                    )
                }
            }
        }
    }
}

@Composable
private fun V7PlayerBackdrop(
    thumbnailUrl: String?,
    disableBlur: Boolean,
    label: String,
    modifier: Modifier = Modifier,
) {
    val blurMaskStart = (1f - V7BackdropBlurHeightFraction).coerceIn(0f, 0.85f)
    val blurMaskMid = (blurMaskStart + 0.12f).coerceIn(blurMaskStart, 0.95f)
    val blurMaskSolid = (blurMaskStart + 0.22f).coerceIn(blurMaskMid, 1f)
    val baseArtworkScale = if (disableBlur) 1.03f else 1.06f
    val baseArtworkAlpha = if (disableBlur) 0.72f else 0.82f
    val surfaceTint = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        AnimatedContent(
            targetState = thumbnailUrl,
            transitionSpec = {
                fadeIn(tween(900)) togetherWith fadeOut(tween(900))
            },
            label = label,
        ) { artworkUrl ->
            if (artworkUrl != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = artworkUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = baseArtworkScale
                                scaleY = baseArtworkScale
                                alpha = baseArtworkAlpha
                            }
                    )

                    if (!disableBlur) {
                        AsyncImage(
                            model = artworkUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .blur(100.dp)
                                .graphicsLayer {
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .drawWithCache {
                                    val blurMask = Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0f to Color.Transparent,
                                            blurMaskStart to Color.Transparent,
                                            blurMaskMid to Color.Black.copy(alpha = 0.6f),
                                            blurMaskSolid to Color.Black,
                                            1f to Color.Black,
                                        )
                                    )

                                    onDrawWithContent {
                                        drawContent()
                                        drawRect(
                                            brush = blurMask,
                                            blendMode = BlendMode.DstIn,
                                        )
                                    }
                                }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.08f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Black.copy(alpha = 0.18f),
                            0.34f to Color.Transparent,
                            0.64f to Color.Black.copy(alpha = 0.22f),
                            1f to Color.Black.copy(alpha = 0.82f),
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to Color.Transparent,
                            0.56f to Color.Transparent,
                            0.8f to surfaceTint.copy(alpha = 0.16f),
                            1f to surfaceTint.copy(alpha = 0.3f),
                        )
                    )
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LittlePlayerContent(
    mediaMetadata: MediaMetadata,
    sliderPosition: Long?,
    positionMs: Long,
    durationMs: Long,
    textColor: Color,
    liked: Boolean,
    onCollapse: () -> Unit,
    onToggleLike: () -> Unit,
    onExpandQueue: () -> Unit,
    onMenuClick: () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val titleColor = textColor.copy(alpha = 0.95f)
        val secondaryColor = textColor.copy(alpha = 0.6f)
        val timeColor = textColor.copy(alpha = 0.85f)

        val scale =
            minOf(maxWidth / 420.dp, maxHeight / 260.dp)
                .coerceIn(0.78f, 1.15f)

        val titleSize = (56f * scale).sp
        val timeSize = (44f * scale).sp
        val iconSize = (26f * scale).dp
        val collapseIconSize = (28f * scale).dp
        val horizontalPadding = (18f * scale).dp
        val verticalPadding = (10f * scale).dp

        val displayPositionMs = sliderPosition ?: positionMs

        val timeText = remember(displayPositionMs, durationMs) {
            val positionText = makeTimeString(displayPositionMs)
            val durationText = if (durationMs != C.TIME_UNSET) makeTimeString(durationMs) else ""
            if (durationText.isBlank()) positionText else "$positionText/$durationText"
        }

        val artistsText = remember(mediaMetadata.artists) {
            mediaMetadata.artists.joinToString(separator = ", ") { artist -> artist.name }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        ) {
            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AnimatedContent(
                        targetState = mediaMetadata.title,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "little_title",
                    ) { title ->
                        Text(
                            text = title,
                            color = titleColor,
                            fontSize = titleSize,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.basicMarquee(),
                        )
                    }

                    Spacer(Modifier.height((10f * scale).dp))

                    mediaMetadata.album?.title?.takeIf { it.isNotBlank() }?.let { albumTitle ->
                        AnimatedContent(
                            targetState = albumTitle,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "little_album",
                        ) { album ->
                            Text(
                                text = album,
                                color = secondaryColor,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.basicMarquee(),
                            )
                        }
                    }

                    artistsText.takeIf { it.isNotBlank() }?.let { artists ->
                        AnimatedContent(
                            targetState = artists,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "little_artists",
                        ) { artistLine ->
                            Text(
                                text = "by - $artistLine",
                                color = secondaryColor,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.basicMarquee(),
                            )
                        }
                    }
                }

                Spacer(Modifier.width((16f * scale).dp))

                Text(
                    text = timeText,
                    color = timeColor,
                    fontSize = timeSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    modifier = Modifier.widthIn(min = (140f * scale).dp),
                )
            }

            Spacer(Modifier.height((14f * scale).dp))

            Spacer(Modifier.height((6f * scale).dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.expand_more),
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(collapseIconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onCollapse,
                        ),
                )

                Spacer(Modifier.weight(1f))

                Icon(
                    painter = painterResource(if (liked) R.drawable.favorite else R.drawable.favorite_border),
                    contentDescription = null,
                    tint = if (liked) MaterialTheme.colorScheme.error.copy(alpha = 0.9f) else textColor.copy(alpha = 0.78f),
                    modifier = Modifier
                        .size(iconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onToggleLike,
                        ),
                )

                Spacer(Modifier.width((18f * scale).dp))

                Icon(
                    painter = painterResource(R.drawable.queue_music),
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.78f),
                    modifier = Modifier
                        .size(iconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onExpandQueue,
                        ),
                )

                Spacer(Modifier.width((18f * scale).dp))

                Icon(
                    painter = painterResource(R.drawable.more_vert),
                    contentDescription = null,
                    tint = textColor.copy(alpha = 0.78f),
                    modifier = Modifier
                        .size(iconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onMenuClick,
                        ),
                )
            }
        }
    }
}

@Composable
private fun LandscapeLikeBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier.graphicsLayer { clip = true },
    ) { measurables, constraints ->
        val measurable = measurables.firstOrNull()
        if (measurable == null) {
            layout(constraints.minWidth, constraints.minHeight) {}
        } else {
            val swappedConstraints =
                Constraints(
                    minWidth = constraints.minHeight,
                    maxWidth = constraints.maxHeight,
                    minHeight = constraints.minWidth,
                    maxHeight = constraints.maxWidth,
                )

            val placeable = measurable.measure(swappedConstraints)
            val width = constraints.maxWidth
            val height = constraints.maxHeight
            val rotatedWidth = placeable.height
            val rotatedHeight = placeable.width

            val x = ((width - rotatedWidth) / 2).coerceAtLeast(0)
            val y = ((height - rotatedHeight) / 2).coerceAtLeast(0)

            layout(width, height) {
                placeable.placeWithLayer(x, y) {
                    transformOrigin = TransformOrigin(0f, 0f)
                    rotationZ = 90f
                    translationX = placeable.height.toFloat()
                }
            }
        }
    }
}

private fun Modifier.littlePlayerOverlayGestures(
    seekEnabled: Boolean,
    durationMs: Long,
    progressFraction: Float,
    canSkipPrevious: Boolean,
    canSkipNext: Boolean,
    onSeekToPositionMs: (Long) -> Unit,
    onSeekFinished: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
): Modifier {
    return pointerInput(seekEnabled, durationMs, canSkipPrevious, canSkipNext) {
        var lastTapUptimeMs = 0L
        var lastTapPosition: Offset? = null
        val doubleTapTimeoutMs = viewConfiguration.doubleTapTimeoutMillis.toLong()
        val touchSlop = viewConfiguration.touchSlop

        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = true)
            val pointerId = down.id

            var upPosition = down.position
            val minOverlayHeightPx = 24.dp.toPx()
            val overlayHeightPx =
                (progressFraction * size.height).coerceAtLeast(minOverlayHeightPx)
            val seekAllowedFromDown =
                seekEnabled &&
                        durationMs > 0L &&
                        durationMs != C.TIME_UNSET &&
                        down.position.y <= overlayHeightPx

            var isSeeking = false

            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Main)
                val change = event.changes.firstOrNull { it.id == pointerId } ?: continue
                upPosition = change.position

                if (!change.pressed) break

                if (!isSeeking && seekAllowedFromDown) {
                    val distanceFromDown = (change.position - down.position).getDistance()
                    if (distanceFromDown > touchSlop) isSeeking = true
                }

                if (isSeeking) {
                    val fraction =
                        if (size.height > 0) (change.position.y / size.height.toFloat()) else 0f
                    val clampedFraction = fraction.coerceIn(0f, 1f)

                    val targetMs =
                        (durationMs.toDouble() * clampedFraction.toDouble()).roundToLong().coerceIn(0L, durationMs)
                    onSeekToPositionMs(targetMs)
                    change.consume()
                }
            }

            if (isSeeking) {
                onSeekFinished()
                lastTapUptimeMs = 0L
                lastTapPosition = null
            } else {
                val now = SystemClock.uptimeMillis()
                val previousTapPosition = lastTapPosition
                val isDoubleTap =
                    previousTapPosition != null &&
                            (now - lastTapUptimeMs) <= doubleTapTimeoutMs &&
                            (upPosition - previousTapPosition).getDistance() <= (touchSlop * 2f)

                if (isDoubleTap) {
                    val isTopSide = upPosition.y < size.height / 2f
                    if (isTopSide) {
                        if (canSkipPrevious) onSkipPrevious()
                    } else {
                        if (canSkipNext) onSkipNext()
                    }
                    lastTapUptimeMs = 0L
                    lastTapPosition = null
                } else {
                    lastTapUptimeMs = now
                    lastTapPosition = upPosition
                }
            }
        }
    }
}

4. Create MiniPlayer.kt

qw/app/src/main/java/com/huayin/music/ui/player/MiniPlayer.kt

package com.huayin.music.ui.player

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.R
import com.huayin.music.constants.MiniPlayerHeight
import com.huayin.music.extensions.togglePlayPause
import com.huayin.music.models.MediaMetadata
import com.huayin.music.playback.PlayerConnection
import com.huayin.music.constants.SwipeSensitivityKey
import com.huayin.music.constants.SwipeThumbnailKey
import com.huayin.music.utils.rememberPreference
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun MiniPlayer(
    position: Long,
    duration: Long,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
) {
    val playerConnection = LocalPlayerConnection.current ?: return
    val layoutDirection = LocalLayoutDirection.current
    val coroutineScope = rememberCoroutineScope()
    val swipeSensitivity by rememberPreference(SwipeSensitivityKey, 0.73f)
    val swipeThumbnail by rememberPreference(SwipeThumbnailKey, true)

    SwipeableMiniPlayerBox(
        modifier = modifier,
        swipeSensitivity = swipeSensitivity,
        swipeThumbnail = swipeThumbnail,
        playerConnection = playerConnection,
        layoutDirection = layoutDirection,
        coroutineScope = coroutineScope,
        pureBlack = pureBlack,
        useLegacyBackground = false
    ) { offsetX ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .clip(RoundedCornerShape(32.dp))
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
        ) {
            NewMiniPlayerContent(
                position = position,
                duration = duration,
                playerConnection = playerConnection
            )
        }
    }
}

@Composable
fun SwipeableMiniPlayerBox(
    modifier: Modifier = Modifier,
    swipeSensitivity: Float,
    swipeThumbnail: Boolean,
    playerConnection: PlayerConnection,
    layoutDirection: LayoutDirection,
    coroutineScope: CoroutineScope,
    pureBlack: Boolean = false,
    useLegacyBackground: Boolean = false,
    content: @Composable (Float) -> Unit
) {
    val offsetXAnimatable = remember { Animatable(0f) }
    var dragStartTime by remember { mutableStateOf(0L) }
    var totalDragDistance by remember { mutableFloatStateOf(0f) }

    val animationSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun calculateAutoSwipeThreshold(swipeSensitivity: Float): Int {
        return (600 / (1f + kotlin.math.exp(-(-11.44748 * swipeSensitivity + 9.04945)))).roundToInt()
    }
    val autoSwipeThreshold = calculateAutoSwipeThreshold(swipeSensitivity)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MiniPlayerHeight)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
            .let { baseModifier ->
                if (useLegacyBackground) {
                    baseModifier.background(
                        if (pureBlack) Color.Black
                        else MaterialTheme.colorScheme.surfaceContainer
                    )
                } else {
                    baseModifier.padding(horizontal = 12.dp)
                }
            }
            .let { baseModifier ->
                if (swipeThumbnail) {
                    baseModifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragStart = {
                                dragStartTime = System.currentTimeMillis()
                                totalDragDistance = 0f
                            },
                            onDragCancel = {
                                coroutineScope.launch {
                                    offsetXAnimatable.animateTo(
                                        targetValue = 0f,
                                        animationSpec = animationSpec
                                    )
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                val adjustedDragAmount =
                                    if (layoutDirection == LayoutDirection.Rtl) -dragAmount else dragAmount
                                val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                                val canSkipNext = playerConnection.player.nextMediaItemIndex != -1
                                val allowLeft = adjustedDragAmount < 0 && canSkipNext
                                val allowRight = adjustedDragAmount > 0 && canSkipPrevious
                                if (allowLeft || allowRight) {
                                    totalDragDistance += kotlin.math.abs(adjustedDragAmount)
                                    coroutineScope.launch {
                                        offsetXAnimatable.snapTo(offsetXAnimatable.value + adjustedDragAmount)
                                    }
                                }
                            },
                            onDragEnd = {
                                val dragDuration = System.currentTimeMillis() - dragStartTime
                                val velocity = if (dragDuration > 0) totalDragDistance / dragDuration else 0f
                                val currentOffset = offsetXAnimatable.value

                                val minDistanceThreshold = 50f
                                val velocityThreshold = (swipeSensitivity * -8.25f) + 8.5f

                                val shouldChangeSong = (
                                        kotlin.math.abs(currentOffset) > minDistanceThreshold &&
                                                velocity > velocityThreshold
                                        ) || (kotlin.math.abs(currentOffset) > autoSwipeThreshold)

                                if (shouldChangeSong) {
                                    val isRightSwipe = currentOffset > 0
                                    val canSkipPrevious = playerConnection.player.previousMediaItemIndex != -1
                                    val canSkipNext = playerConnection.player.nextMediaItemIndex != -1

                                    if (isRightSwipe && canSkipPrevious) {
                                        playerConnection.player.seekToPreviousMediaItem()
                                    } else if (!isRightSwipe && canSkipNext) {
                                        playerConnection.player.seekToNext()
                                    }
                                }

                                coroutineScope.launch {
                                    offsetXAnimatable.animateTo(
                                        targetValue = 0f,
                                        animationSpec = animationSpec
                                    )
                                }
                            }
                        )
                    }
                } else {
                    baseModifier
                }
            }
    ) {
        content(offsetXAnimatable.value)

        // Visual indicator
        if (offsetXAnimatable.value.absoluteValue > 50f) {
            Box(
                modifier = Modifier
                    .align(if (offsetXAnimatable.value > 0) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(
                        if (offsetXAnimatable.value > 0) R.drawable.skip_previous else R.drawable.skip_next
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(
                        alpha = (offsetXAnimatable.value.absoluteValue / autoSwipeThreshold).coerceIn(0f, 1f)
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun RowScope.MiniPlayerInfo(
    mediaMetadata: MediaMetadata
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedContent(
            targetState = mediaMetadata.title,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "title"
        ) { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
        }

        AnimatedContent(
            targetState = mediaMetadata.artists,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "artist"
        ) { artists ->
            Text(
                text = artists.joinToString { it.name },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

@Composable
private fun MiniPlayerArtwork(
    mediaMetadata: MediaMetadata?,
    position: Long,
    duration: Long,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(47.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        } else {
            CircularProgressIndicator(
                progress = { if (duration > 0) (position.toFloat() / duration).coerceIn(0f, 1f) else 0f },
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(37.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        ) {
            val thumbnailUrl = mediaMetadata?.thumbnailUrl
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.music_note),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerTransportButton(
    iconResId: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = false
) {
    val containerColor =
        if (isPrimary) MaterialTheme.colorScheme.surface else Color.Transparent
    val borderColor =
        if (enabled) MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val iconTint =
        if (enabled) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .then(modifier)
            .size(if (isPrimary) 40.dp else 36.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Icon(
            painter = painterResource(iconResId),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(if (isPrimary) 22.dp else 18.dp)
        )
    }
}

@Composable
private fun MiniPlayerTransportControls(
    isPlaying: Boolean,
    playbackState: Int,
    canSkipPrevious: Boolean,
    canSkipNext: Boolean,
    playerConnection: PlayerConnection
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MiniPlayerTransportButton(
            iconResId = R.drawable.skip_previous,
            contentDescription = null,
            onClick = playerConnection::seekToPrevious,
            enabled = canSkipPrevious
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)
        ) {
            MiniPlayerTransportButton(
                iconResId = when {
                    playbackState == Player.STATE_ENDED -> R.drawable.replay
                    isPlaying -> R.drawable.pause
                    else -> R.drawable.play
                },
                contentDescription = stringResource(
                    if (playbackState == Player.STATE_ENDED || !isPlaying) R.string.play else R.string.play
                ).let {
                    if (isPlaying && playbackState != Player.STATE_ENDED) "Pause" else it
                },
                onClick = {
                    if (playbackState == Player.STATE_ENDED) {
                        playerConnection.player.seekTo(0, 0)
                        playerConnection.player.playWhenReady = true
                    } else {
                        playerConnection.player.togglePlayPause()
                    }
                },
                isPrimary = true
            )
        }

        MiniPlayerTransportButton(
            iconResId = R.drawable.skip_next,
            contentDescription = null,
            onClick = playerConnection::seekToNext,
            enabled = canSkipNext
        )
    }
}

@Composable
fun NewMiniPlayerContent(
    position: Long,
    duration: Long,
    playerConnection: PlayerConnection
) {
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val playbackState by playerConnection.playbackState.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val canSkipPrevious by playerConnection.canSkipPrevious.collectAsState()
    val canSkipNext by playerConnection.canSkipNext.collectAsState()

    val isLoading = playbackState == Player.STATE_BUFFERING

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
    ) {
        MiniPlayerArtwork(
            mediaMetadata = mediaMetadata,
            position = position,
            duration = duration,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.width(5.dp))

        mediaMetadata?.let {
            MiniPlayerInfo(mediaMetadata = it)
        } ?: Spacer(Modifier.weight(1f))

        Spacer(modifier = Modifier.width(12.dp))

        MiniPlayerTransportControls(
            isPlaying = isPlaying,
            playbackState = playbackState,
            canSkipPrevious = canSkipPrevious,
            canSkipNext = canSkipNext,
            playerConnection = playerConnection
        )
    }
}