package com.morris.android.medicationapp

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MedicineActivity: AppCompatActivity(), MedicineListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine)
        supportActionBar?.elevation = 0f

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_medicine_container)
        if (fragment == null) {
            val newFragment = MedicineListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_medicine_container, newFragment)
                .commit()
        }
    }

    override fun onMedicineListItemSelected(medicineId: UUID) {
        val newFragment = MedicineFragment.newInstance(medicineId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_medicine_container, newFragment)
            .addToBackStack(null)
            .commit()
    }
}
