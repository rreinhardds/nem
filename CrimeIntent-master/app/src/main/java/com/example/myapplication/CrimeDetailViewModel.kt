package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.myapplication.database.CrimeRepository
import java.util.*

class CrimeDetailViewModel: ViewModel() {
    private val crimeRepository =
        CrimeRepository.get()
    private val crimeIdLiveData =
        MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

   public fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId
    }
    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }
}