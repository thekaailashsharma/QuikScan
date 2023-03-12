package com.quik.scan.history.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.quik.scan.qrcode.analyzer.BarCodeTypes

@Entity(tableName = "History")
data class History(
    @PrimaryKey
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "type")
    val type: BarCodeTypes,
    @ColumnInfo(name = "isScanned")
    val isScanned: Boolean,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "isSaved")
    val isSaved: Boolean = false,
    @ColumnInfo(name = "isShared")
    val isShared: Boolean = false,
    @ColumnInfo(name = "sharedFrom")
    val sharedFrom: String? = null,
)

