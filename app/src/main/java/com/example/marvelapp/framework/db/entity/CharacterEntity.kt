package com.example.marvelapp.framework.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core.data.Constants.CHARACTER_COLUMN_INFO_ID
import com.example.core.data.Constants.CHARACTER_COLUMN_INFO_IMAGE_URL
import com.example.core.data.Constants.CHARACTER_COLUMN_INFO_NAME
import com.example.core.data.Constants.CHARACTER_TABLE_NAME

@Entity(tableName = CHARACTER_TABLE_NAME)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val autoId: Int = 0,
    @ColumnInfo(name = CHARACTER_COLUMN_INFO_ID)
    val id: Int,
    @ColumnInfo(name = CHARACTER_COLUMN_INFO_NAME)
    val name: String,
    @ColumnInfo(name = CHARACTER_COLUMN_INFO_IMAGE_URL)
    val imageUrl: String
)
