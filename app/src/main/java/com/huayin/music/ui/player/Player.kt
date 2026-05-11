package com.huayin.music.ui.player

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.text.format.Formatter
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.offline.DownloadService
import androidx.navigation.NavController
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.huayin.music.LocalDatabase
import com.huayin.music.LocalDownloadUtil
import com.huayin.music.LocalPlayerConnection
import com.huayin.music.R
import com.huayin.music.constants.*
import com.huayin.music.extensions.togglePlayPause
import com.huayin.music.models.MediaMetadata
import com.huayin.music.playback.ExoDownloadService
import com.huayin.music.playback.queues.YouTubeQueue
import com.huayin.music.ui.component.*
import com.huayin.music.ui.menu.PlayerMenu
import com.huayin.music.ui.screens.settings.DarkMode
import com.huayin.music.ui.theme.PlayerColorExtractor
import com.huayin.music.ui.theme.PlayerSliderColors
import com.huayin.music.utils.makeTimeString
import com.huayin.music.utils.rememberEnumPreference
import com.huayin.music.utils.rememberPreference
import me.saket.squiggles.SquigglySlider
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomSheetPlayer(
    state: BottomSheetState,
    navController: NavController,
    onOpenFullscreenLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val database = LocalDatabase.current
    val menuState = LocalMenuState.current
    val clipboardManager = LocalClipboardManager.current
    val playerConnection = LocalPlayerConnection.current ?: return

    val playerBackground by rememberEnumPreference(PlayerBackgroundStyleKey, PlayerBackgroundStyle.DEFAULT)
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val darkTheme by rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val pureBlack by rememberPreference(PureBlackKey, false)
    val useDarkTheme = if (darkTheme == DarkMode.AUTO) isSystemInDarkTheme else darkTheme == DarkMode.ON

    val playbackState by playerConnection.playbackState.collectAsState()
    val isPlaying by playerConnection.isPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()
    val currentSong by playerConnection.currentSong.collectAsState(initial = null)
    val sliderStyle by rememberEnumPreference(SliderStyleKey, SliderStyle.SQUIGGLY)

    var position by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.currentPosition) }
    var duration by rememberSaveable(playbackState) { mutableLongStateOf(playerConnection.player.duration) }
    var sliderPosition by remember { mutableStateOf<Long?>(null) }
    var gradientColors by remember { mutableStateOf<List<Color>>(emptyList()) }

    val expressiveAccent = if (useDarkTheme) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary

    // Animation for Play/Pause Morphing Shape
    val playPauseShape = remember(isPlaying) {
        if (isPlaying) MaterialShapes.Puffy.toShape() else MaterialShapes.Cookie9Sided.toShape()
    }

    LaunchedEffect(mediaMetadata) {
        if (playerBackground == PlayerBackgroundStyle.GRADIENT || playerBackground == PlayerBackgroundStyle.APPLE_MUSIC) {
            withContext(Dispatchers.IO) {
                val result = runCatching {
                    ImageLoader(context).execute(ImageRequest.Builder(context).data(mediaMetadata?.thumbnailUrl).allowHardware(false).build()).drawable as? BitmapDrawable
                }.getOrNull()
                result?.bitmap?.let { bitmap ->
                    val palette = Palette.from(bitmap).generate()
                    gradientColors = PlayerColorExtractor.extractGradientColors(palette, Color.Gray.toArgb())
                }
            }
        }
    }

    BottomSheet(
        state = state,
        modifier = modifier,
        background = {
            Box(Modifier.fillMaxSize().background(if (pureBlack && useDarkTheme) Color.Black else MaterialTheme.colorScheme.surfaceContainer))
        },
        collapsedContent = { MiniPlayer(position = position, duration = duration) },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Vertical))
        ) {
            // Thumbnail Section
            Box(modifier = Modifier.weight(1.2f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Thumbnail(sliderPositionProvider = { sliderPosition }, onOpenFullscreenLyrics = onOpenFullscreenLyrics)
            }

            // Info Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = PlayerHorizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                mediaMetadata?.let { meta ->
                    Text(
                        text = meta.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.basicMarquee()
                    )
                    Text(
                        text = meta.artists.joinToString { it.name },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Progress Section
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = PlayerHorizontalPadding)) {
                if (sliderStyle == SliderStyle.SQUIGGLY) {
                    SquigglySlider(
                        value = (sliderPosition ?: position).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = { sliderPosition = it.toLong() },
                        onValueChangeFinished = {
                            sliderPosition?.let { playerConnection.player.seekTo(it) }
                            sliderPosition = null
                        },
                        squigglesSpec = SquigglySlider.SquigglesSpec(
                            amplitude = if (isPlaying) 4.dp else 0.dp,
                            wavelength = 24.dp
                        )
                    )
                } else {
                    Slider(
                        value = (sliderPosition ?: position).toFloat(),
                        valueRange = 0f..(if (duration == C.TIME_UNSET) 0f else duration.toFloat()),
                        onValueChange = { sliderPosition = it.toLong() },
                        onValueChangeFinished = {
                            sliderPosition?.let { playerConnection.player.seekTo(it) }
                            sliderPosition = null
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = PlayerHorizontalPadding + 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(makeTimeString(sliderPosition ?: position), style = MaterialTheme.typography.labelMedium)
                Text(makeTimeString(duration), style = MaterialTheme.typography.labelMedium)
            }

            Spacer(Modifier.height(32.dp))

            // EXPRESSIVE BUTTON GROUP
            ButtonGroup(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                buttonHeight = 84.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Previous Button
                Button(
                    onClick = { playerConnection.seekToPrevious() },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = MaterialShapes.Pill.toShape(),
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Icon(painterResource(R.drawable.skip_previous), null, modifier = Modifier.size(32.dp))
                }

                // Play / Pause Button (Expressive Centerpiece)
                Button(
                    onClick = { playerConnection.player.togglePlayPause() },
                    modifier = Modifier.weight(1.5f).fillMaxHeight(),
                    shape = playPauseShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                        contentDescription = null,
                        modifier = Modifier.size(42.dp)
                    )
                }

                // Next Button
                Button(
                    onClick = { playerConnection.seekToNext() },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = MaterialShapes.Pill.toShape(),
                    colors = ButtonDefaults.filledTonalButtonColors()
                ) {
                    Icon(painterResource(R.drawable.skip_next), null, modifier = Modifier.size(32.dp))
                }
            }

            Spacer(Modifier.height(48.dp))
            
            // Secondary Action Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { playerConnection.player.toggleRepeatMode() }) {
                    Icon(painterResource(R.drawable.repeat), null, tint = if (repeatMode != REPEAT_MODE_OFF) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                }
                IconButton(onClick = { showChoosePlaylistDialog = true }) {
                    Icon(painterResource(R.drawable.playlist_add), null)
                }
                IconButton(onClick = { 
                    menuState.show { 
                         PlayerMenu(mediaMetadata, navController, state, onShowDetailsDialog = {}, onDismiss = { menuState.dismiss() })
                    }
                }) {
                    Icon(painterResource(R.drawable.more_horiz), null)
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}