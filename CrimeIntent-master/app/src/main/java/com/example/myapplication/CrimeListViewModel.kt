package com.example.myapplication

import androidx.lifecycle.ViewModel
import com.example.myapplication.database.CrimeRepository
import java.util.*

class CrimeListViewModel : ViewModel() {

    //val crimes = mutableListOf<Crime>()

  /*  init {
        for (i in 0 until 100) {
          val crime = Crime()
            crime.setTitle("Crime #$i")
            crime.isSolved()
                crime.setRequiresPolice(i % 3 == 0)
            crime.setDate(Date())
            crimes += crime
        }


    }*/

  private val crimeRepository = CrimeRepository.get()
  val crimeListLiveData =crimeRepository.getCrimes()

  fun addCrime(crime:Crime){
    crimeRepository.addCrime(crime)
  }

}
