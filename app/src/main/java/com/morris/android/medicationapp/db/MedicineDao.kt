package com.morris.android.medicationapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.morris.android.medicationapp.Medicine
import java.util.UUID

@Dao
interface MedicineDao {
    @Query("SELECT * FROM medicine")
    fun getMedicines(): LiveData<List<Medicine>>

    @Query("SELECT * FROM medicine WHERE id=(:id)")
    fun getMedicine(id: UUID): LiveData<Medicine?>

    @Update
    fun updateMedicine(medicine: Medicine)

    @Insert
    fun addMedicine(medicine: Medicine)

    @Delete
    fun removeMedicine(medicine: Medicine)
}