import java.util.*

data class ParkingSpace(var vehicle: Vehicle)

data class Parking(val vehicles: MutableSet<Vehicle>) {
    val maxCapacity: Int = 20

    fun addVehicle(vehicle: Vehicle): Boolean{
        var response = false

        if(vehicles.count() < maxCapacity){
            response = vehicles.add(vehicle)
        }

        if(response)
            println("Welcome to AlkeParking!")
        else
            println("Sorry, the has check-in failed")

        return response
    }
}// Se define como set para no almacenar vehiculos con la misma placa

data class Vehicle(val plate: String, val type: VehicleType, val checkIntTime: Calendar, val discountCard: String? = null) {

    val parkedTime: Long
    get() = (Calendar.getInstance().timeInMillis - checkIntTime.timeInMillis) / 60000
    // Function states that two Vehicles are equal if plates are equal.
    override fun equals(other: Any?): Boolean {
        if(other is Vehicle) {
            return this.plate == other.plate
        }
        return super.equals(other)
    }

    // Functions states that the hasCode(Used internally in search functions in sets and arrays) is the hashCode of
    // the plate.
    override fun hashCode() : Int = this.plate.hashCode()
}

enum class VehicleType(val rate: Int) {
    CAR(20),
    MOTORCYCLE(15),
    MINIBUS(25),
    BUS(30)
}

fun main() {
    var vehiclesTest = listOf<Vehicle>(
        Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001"),
        Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001"),
        Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001"),
        Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001"),
        Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001"),
    )

    val car = Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001")
    val moto = Vehicle("B222BBB", VehicleType.MOTORCYCLE, Calendar.getInstance())
    val minibus = Vehicle("CC333CC", VehicleType.MINIBUS, Calendar.getInstance())
    val bus = Vehicle("DD444DD", VehicleType.BUS, Calendar.getInstance(), "DISCOUNT_CARD_002")

    val parking = Parking(mutableSetOf(car, moto, minibus, bus))

    println(parking.vehicles.contains(car))
    println(parking.vehicles.contains(moto))
    println(parking.vehicles.contains(minibus))
    println(parking.vehicles.contains(bus))

}