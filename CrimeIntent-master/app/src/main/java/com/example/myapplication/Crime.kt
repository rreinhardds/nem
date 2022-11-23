package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
@Entity
data class Crime(@PrimaryKey val id :UUID = UUID.randomUUID(),  var Title: String? = null,
                 var Date: Date = Date(), var isSolved: Boolean = false, var RequiresPolice : Boolean = false,var suspect :String = " "
    ){

}