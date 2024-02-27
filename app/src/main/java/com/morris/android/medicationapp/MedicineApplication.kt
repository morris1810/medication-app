package com.morris.android.medicationapp

import android.app.Application

class MedicineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MedicineRepository.init(this)
    }
}