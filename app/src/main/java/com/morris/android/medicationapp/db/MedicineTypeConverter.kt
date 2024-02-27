package com.morris.android.medicationapp.db

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class MedicineTypeConverter {
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinEpoch: Long?): Date? {
        return millisSinEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String? {
        return uuid?.toString()
    }
}