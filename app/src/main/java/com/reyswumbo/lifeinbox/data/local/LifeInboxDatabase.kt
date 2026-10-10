package com.reyswumbo.lifeinbox.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity
import com.reyswumbo.lifeinbox.data.local.entity.LinkEntity

@Database(
    entities = [LinkEntity::class, CollectionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class LifeInboxDatabase : RoomDatabase() {

    abstract fun linkDao(): LinkDao

    abstract fun collectionDao(): CollectionDao

    companion object {
        const val DATABASE_NAME = "life_inbox.db"

        const val INBOX_COLLECTION_ID = 1L
        const val IDEAS_COLLECTION_ID = 2L
        const val INSPIRATION_COLLECTION_ID = 3L

        fun build(
            context: Context,
            inMemory: Boolean = false,
            allowMainThreadQueries: Boolean = false
        ): LifeInboxDatabase {
            val builder = if (inMemory) {
                Room.inMemoryDatabaseBuilder(context, LifeInboxDatabase::class.java)
            } else {
                Room.databaseBuilder(context, LifeInboxDatabase::class.java, DATABASE_NAME)
            }
            if (allowMainThreadQueries) {
                builder.allowMainThreadQueries()
            }
            return builder
                .addCallback(SeedCallback())
                .build()
        }
    }

    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            db.execSQL(
                "INSERT INTO collections (id, name, colorHex, icon, isDefault, sortOrder, createdAt) " +
                    "VALUES (${INBOX_COLLECTION_ID}, 'Inbox', '#E7EEDD', 'inbox', 1, 0, 0)"
            )
            db.execSQL(
                "INSERT INTO collections (id, name, colorHex, icon, isDefault, sortOrder, createdAt) " +
                    "VALUES (${IDEAS_COLLECTION_ID}, 'Ideas', '#F3E9B9', 'idea', 0, 1, 0)"
            )
            db.execSQL(
                "INSERT INTO collections (id, name, colorHex, icon, isDefault, sortOrder, createdAt) " +
                    "VALUES (${INSPIRATION_COLLECTION_ID}, 'Inspiration', '#EFEDF6', 'spark', 0, 2, 0)"
            )
        }
    }
}
