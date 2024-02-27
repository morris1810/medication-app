package com.morris.android.medicationapp

import androidx.lifecycle.ViewModel

class MedicineListViewModel: ViewModel() {
    private val medicineRepository = MedicineRepository.get()
    val medicineListLiveData = medicineRepository.getMedicines()

    fun addMedicine(medicine: Medicine) {
        medicineRepository.addMedicine(medicine)
    }
}