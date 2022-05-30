package com.example.culturalproject

class Book(
    val id: String,
    val title : String="Nieznany",
    val author: String="Nieznany",
    var read:String = "TAK")
{
    constructor():this("", "","", ""){

    }
}