package com.sirius.net.ptaxi.model

data class OffreTaxi(var idOffer:String,
                    var adrDeparture:String,
                    var adrDestination:String,
                    var departLongitude: Float,
                     var departLatitude: Float,
                     var destinationLongitude: Float,
                     var destinationLatitude: Float,
                    var freePlaces: Int,
                     var price:Float,
                     val departTime:String,
                    val departDate: String,
val id_order : String,
val user : User?)