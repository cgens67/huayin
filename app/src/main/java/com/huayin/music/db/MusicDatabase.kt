package com.huayin.music.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.huayin.music.db.entities.AlbumArtistMap
import com.huayin.music.db.entities.AlbumEntity
import com.huayin.music.db.entities.ArtistEntity
import com.huayin.music.db.entities.Event
import com.huayin.music.db.entities.FormatEntity
import com.huayin.music.db.entities.LyricsEntity
import com.huayin.music.db.entities.PlayCountEntity
import com.huayin.music.db.entities.PlaylistEntity
import com.huayin.music.db.entities.PlaylistSongMap
import com.huayin.music.db.entities.PlaylistSongMapPreview
import com.huayin.music.db.entities.RelatedSongMap
import com.huayin.music.db.entities.SearchHistory
import com.huayin.music.db.entities.SetVideoIdEntity
import com.huayin.music.db.entities.SongAlbumMap
import com.huayin.music.db.entities.SongArtistMap
import com.huayin.music.db.entities.SongEntity
import com.huayin.music.db.entities.SortedSongAlbumMap
import com.huayin.music.db.entities.SortedSongArtistMap

class MusicDatabase(
    private val delegate: InternalDatabase,
) : DatabaseDao by delegate.dao {
    val openHelper: SupportSQLiteOpenHelper
        get() = delegate.openHelper

    fun query(block: MusicDatabase.() -> Unit) =
        with(delegate) {
            queryExecutor.execute {
                block(this@MusicDatabase)
            }
        }

    fun transaction(block: MusicDatabase.() -> Unit) =
        with(delegate) {
            transactionExecutor.execute {
                runInTransaction {
                    block(this@MusicDatabase)
                }
            }
        }

    fun close() = delegate.close()
}

@Database(
    entities =[
        SongEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        PlaylistEntity::class,
        SongArtistMap::class,
        SongAlbumMap::class,
        AlbumArtistMap::class,
        PlaylistSongMap::class,
        SearchHistory::class,
        FormatEntity::class,
        LyricsEntity::class,
        Event::class,
        RelatedSongMap::class,
        SetVideoIdEntity::class,
        PlayCountEntity::class
    ],
    views =[
        SortedSongArtistMap::class,
        SortedSongAlbumMap::class,
        PlaylistSongMapPreview::class,
    ],
    version = 1, // Reset to version 1 for the new Chinese app
    exportSchema = false // No need to export schemas since we aren't migrating
)
@TypeConverters(Converters::class)
abstract class InternalDatabase : RoomDatabase() {
    abstract val dao: DatabaseDao

    companion object {
        const val DB_NAME = "song.db"

        fun newInstance(context: Context): MusicDatabase =
            MusicDatabase(
                delegate =
                    Room
                        .databaseBuilder(context, InternalDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration() // Safely rebuilds the DB if ever needed
                        .build(),
            )
    }
}