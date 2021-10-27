import java.util.*

data class ParkingSpace(val parking: Parking) {

    fun checkIn(vehicle: Vehicle){
        parking.addVehicle(vehicle)
    }
    fun calculateFee(type: VehicleType, parkedTime: Int, hasDiscountCard: Boolean): Int {
        var total = type.rate
        val time = parkedTime - 120
        val fraction = 15
        if(parkedTime > 120){
            val rest = if(time % fraction > 0 ) 1 else 0
            total += ((time / fraction) + rest )* 5
        }

        if(hasDiscountCard) {
            total -= (total * 15) / 100
        }

        return total
    }

    fun checkOutVehicle(plate: String, onSuccess: (Int) -> Unit, onError: () -> Unit){
        val vehicle: Vehicle? = parking.vehicles.firstOrNull { it.plate == plate}
        vehicle?.let{
            parking.removeVehicle(plate)
            onSuccess(calculateFee(it.type, it.parkedTime.toInt(), !it.discountCard.isNullOrEmpty()))
        }.run{
            onError()
        }
    }
}

data class Parking(val vehicles: MutableSet<Vehicle>) {
    val maxCapacity: Int = 20

    fun removeVehicle(plate: String) {
        val vehicle : Vehicle? = vehicles.firstOrNull { it.plate == plate}

        vehicle?.let{
            vehicles.remove(it)
            registerVehicle(fee)
        }
    }

    fun registerVehicle(amount : Int) {
        val (vehicle,_amount) = checkoutVehiclesPair
        checkoutVehiclesPair = Pair(vehicle + 1, _amount + amount)
    }

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
        Vehicle("AA111A1", VehicleType.MOTORCYCLE, Calendar.getInstance(), "DISCOUNT_CARD_002"),
        Vehicle("AA111A2", VehicleType.BUS, Calendar.getInstance(), "DISCOUNT_CARD_003"),
        Vehicle("AA111A3", VehicleType.MINIBUS, Calendar.getInstance(), "DISCOUNT_CARD_004"),
        Vehicle("AA111A4", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_005"),
        Vehicle("AA111A5", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_006"),
        )


    //val parking = Parking(mutableSetOf())
    val parkingSpace = ParkingSpace(Parking(mutableSetOf()))
    vehiclesTest.forEach {parkingSpace.checkIn(it)}

    var lala = vehiclesTest.firstOrNull()

    parkingSpace.checkOutVehicle(lala.plate, )

//    val car = Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001")
//    val moto = Vehicle("B222BBB", VehicleType.MOTORCYCLE, Calendar.getInstance())
//    val minibus = Vehicle("CC333CC", VehicleType.MINIBUS, Calendar.getInstance())
//    val bus = Vehicle("DD444DD", VehicleType.BUS, Calendar.getInstance(), "DISCOUNT_CARD_002")

    // val parking = Parking(mutableSetOf(car, moto, minibus, bus))
}