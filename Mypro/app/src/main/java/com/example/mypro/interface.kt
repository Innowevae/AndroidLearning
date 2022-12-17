package com.example.mypro

interface Driveable {
    val maxSpeed: Double
    fun drive(): String
    fun brake(){
        println("The drivable is braking")
    }
}

// Class Car which extends the interface
open class Car(override val maxSpeed: Double,
               open val brandName: String
) : Driveable {
    /*open var range: Double = 0.0

    open fun extendRange(amount: Double) {
        if (amount > 0) {
            range += amount
        }

    }*/
    override fun drive(): String {
        println("Drove for $range KM")
    }
}