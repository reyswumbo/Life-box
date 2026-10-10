package com.reyswumbo.lifeinbox.data.local

import androidx.room.Embedded
import com.reyswumbo.lifeinbox.data.local.entity.CollectionEntity

data class CollectionWithCount(
    @Embedded val collection: CollectionEntity,
    val linkCount: Int
)
