package com.morris.android.medicationapp

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.TimeZone
import java.util.UUID

@Entity
data class Medicine(
    @PrimaryKey var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var endDateTime: Date = Date(),
    var repeatOption: RepeatOption = RepeatOption.NONE,
    var remark: String = ""
)
