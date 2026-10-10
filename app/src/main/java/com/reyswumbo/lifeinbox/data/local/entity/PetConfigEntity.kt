package com.reyswumbo.lifeinbox.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.reyswumbo.lifeinbox.domain.pet.Accessory
import com.reyswumbo.lifeinbox.domain.pet.BodyShape
import com.reyswumbo.lifeinbox.domain.pet.EyeStyle
import com.reyswumbo.lifeinbox.domain.pet.PetConfig
import com.reyswumbo.lifeinbox.domain.pet.PetTexture

@Entity(tableName = "pet_config")
data class PetConfigEntity(
    @PrimaryKey
    val id: Long = 1,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "bodyShape")
    val bodyShape: String,

    @ColumnInfo(name = "eyeStyle")
    val eyeStyle: String,

    @ColumnInfo(name = "accessory")
    val accessory: String,

    @ColumnInfo(name = "bodyColorHex")
    val bodyColorHex: String,

    @ColumnInfo(name = "eyeColorHex")
    val eyeColorHex: String,

    @ColumnInfo(name = "accentColorHex")
    val accentColorHex: String,

    @ColumnInfo(name = "texture")
    val texture: String
)

fun PetConfigEntity.toDomain(): PetConfig = PetConfig(
    name = name,
    bodyShape = BodyShape.entries.firstOrNull { it.name == bodyShape } ?: BodyShape.ROUND,
    eyeStyle = EyeStyle.entries.firstOrNull { it.name == eyeStyle } ?: EyeStyle.NORMAL,
    accessory = Accessory.entries.firstOrNull { it.name == accessory } ?: Accessory.NONE,
    bodyColorHex = bodyColorHex,
    eyeColorHex = eyeColorHex,
    accentColorHex = accentColorHex,
    texture = PetTexture.entries.firstOrNull { it.name == texture } ?: PetTexture.SMOOTH
)

fun PetConfig.toEntity(): PetConfigEntity = PetConfigEntity(
    id = 1,
    name = name,
    bodyShape = bodyShape.name,
    eyeStyle = eyeStyle.name,
    accessory = accessory.name,
    bodyColorHex = bodyColorHex,
    eyeColorHex = eyeColorHex,
    accentColorHex = accentColorHex,
    texture = texture.name
)