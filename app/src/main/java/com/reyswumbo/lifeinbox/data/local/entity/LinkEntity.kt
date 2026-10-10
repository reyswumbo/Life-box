package com.reyswumbo.lifeinbox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "links",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collectionId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["url"], unique = true),
        Index(value = ["collectionId"]),
        Index(value = ["createdAt"])
    ]
)
data class LinkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "originalUrl")
    val originalUrl: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "domain")
    val domain: String,

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "collectionId")
    val collectionId: Long,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "updatedAt")
    val updatedAt: Long
)
