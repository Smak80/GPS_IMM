package com.example.gps_imm

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationData {
    val location = MutableLiveData<Location>()
}