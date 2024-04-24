package com.dicoding.asclepius.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "History")
@TypeConverters(DateConverters::class)
@Parcelize
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "score")
    var score: String? = null,

    @ColumnInfo(name = "foto")
    var foto: ByteArray? = null,

    @ColumnInfo(name = "created_at")
    var createdAt: Date? = null

): Parcelable
