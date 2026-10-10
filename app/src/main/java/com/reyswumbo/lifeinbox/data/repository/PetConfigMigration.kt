package com.reyswumbo.lifeinbox.data.repository

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object PetConfigMigration {

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `pet_config` (" +
                    "`id` INTEGER NOT NULL, " +
                    "`name` TEXT NOT NULL, " +
                    "`bodyShape` TEXT NOT NULL, " +
                    "`eyeStyle` TEXT NOT NULL, " +
                    "`accessory` TEXT NOT NULL, " +
                    "`bodyColorHex` TEXT NOT NULL, " +
                    "`eyeColorHex` TEXT NOT NULL, " +
                    "`accentColorHex` TEXT NOT NULL, " +
                    "`texture` TEXT NOT NULL, " +
                    "PRIMARY KEY(`id`))"
            )
            db.execSQL(
                "INSERT INTO `pet_config` (`id`, `name`, `bodyShape`, `eyeStyle`, `accessory`, " +
                    "`bodyColorHex`, `eyeColorHex`, `accentColorHex`, `texture`) " +
                    "VALUES (1, 'Pocket Pal', 'ROUND', 'NORMAL', 'NONE', " +
                    "'#A9C39A', '#33382F', '#F3E9B9', 'SMOOTH')"
            )
        }
    }
}