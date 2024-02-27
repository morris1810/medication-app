package com.morris.android.medicationapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.morris.android.medicationapp.db.MedicineDao
import com.morris.android.medicationapp.db.MedicineDb
import com.morris.android.medicationapp.db.migration_1_2
import java.lang.IllegalStateException
import java.util.UUID
import java.util.concurrent.Executor
import java.util.concurrent.Executors


private const val DB_NAME = "medicine-database"
class MedicineRepository private constructor(context: Context) {

    private val db: MedicineDb = Room.databaseBuilder(context.applicationContext, MedicineDb::class.java, DB_NAME)
        .addMigrations(migration_1_2)
        .build()

    private val medicineDao: MedicineDao = db.medicineDao()

    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun getMedicines(): LiveData<List<Medicine>> = medicineDao.getMedicines()

    fun getMedicine(id: UUID): LiveData<Medicine?> = medicineDao.getMedicine(id)

    fun updateMedicine(medicine: Medicine) {
        executor.execute {
            medicineDao.updateMedicine(medicine)
        }
    }

    fun addMedicine(medicine: Medicine) {
        executor.execute {
            medicineDao.addMedicine(medicine)
        }
    }

    fun removeMedicine(medicine: Medicine) {
        executor.execute {
            medicineDao.removeMedicine(medicine)
        }
    }

    companion object {

        private var INSTANCE: MedicineRepository? = null

        fun init(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MedicineRepository(context)
            }
        }

        fun get(): MedicineRepository {
            return INSTANCE ?: throw IllegalStateException("Repository haven't be initialized before call get() method. ")
        }
    }
}