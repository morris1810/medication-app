package com.morris.android.medicationapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.UUID

class MedicineDetailViewModel: ViewModel() {

    private val medicineRepository: MedicineRepository = MedicineRepository.get()
    private val medicineIdLiveData = MutableLiveData<UUID>()

    var medicineLiveData: LiveData<Medicine?> = Transformations.switchMap(medicineIdLiveData) {
        medicineRepository.getMedicine(it)
    }

    fun loadMedicine(medicineId: UUID) {
        medicineIdLiveData.value = medicineId
    }

    fun saveMedicine(medicine: Medicine) {
        medicineRepository.updateMedicine(medicine)
    }
    fun deleteMedicine(medicine: Medicine) {
        medicineRepository.removeMedicine(medicine)
    }
}