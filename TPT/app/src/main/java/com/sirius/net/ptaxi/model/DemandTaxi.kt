package com.sirius.net.ptaxi.model

data class DemandTaxi(var idDemand:String,
                 var adrDeparture:String,
                 var adrDestination:String,
                 var departLongitude: Float,
                      var departLatitude: Float,
                 var destinationLongitude: Float,
                      var destinationLatitude: Float,
                 var nbrPassengers: Int,
                      val departTime:String,
                 val departDate: String,
                      val demandState:String,
                      var isAlert: Int,
val user : User)