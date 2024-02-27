package com.morris.android.medicationapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.morris.android.medicationapp.Medicine

@Database(entities = [Medicine::class], version = 2, exportSchema = false)
@TypeConverters(MedicineTypeConverter::class)
abstract class MedicineDb: RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
}

val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Medicine ADD COLUMN suspect TEXT NOT NULL DEFAULT ''"
        )
    }
}
