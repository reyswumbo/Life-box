package com.reyswumbo.lifeinbox.domain.pet

enum class BodyShape { ROUND, BOXY, FLUFFY, TRIANGLE, HEART }

enum class EyeStyle { NORMAL, SLEEPY, HAPPY, CURIOUS }

enum class Accessory { NONE, CROWN, HORNS, HAT }

enum class PetTexture { SMOOTH, FLUFFY, FUZZY }

data class PetConfig(
    val name: String = "Pocket Pal",
    val bodyShape: BodyShape = BodyShape.ROUND,
    val eyeStyle: EyeStyle = EyeStyle.NORMAL,
    val accessory: Accessory = Accessory.NONE,
    val bodyColorHex: String = "#A9C39A",
    val eyeColorHex: String = "#33382F",
    val accentColorHex: String = "#F3E9B9",
    val texture: PetTexture = PetTexture.SMOOTH
) {
    companion object {
        val Default = PetConfig()

        val bodyPalette: List<String> = listOf(
            "#A9C39A",
            "#F3C8A0",
            "#C9B8E8",
            "#F3B0B0",
            "#9FC6E8",
            "#E8D48B",
            "#B7D9C0"
        )

        val eyePalette: List<String> = listOf(
            "#33382F",
            "#4A6FA5",
            "#7A4E8F",
            "#8A5A2B",
            "#2E7D6F"
        )

        val accentPalette: List<String> = listOf(
            "#F3E9B9",
            "#F6C6A8",
            "#C9B8E8",
            "#F3B0B0",
            "#A9C39A"
        )
    }
}
