import java.util.*

data class ParkingSpace(val parking: Parking) {

    fun checkIn(vehicle: Vehicle){
        parking.addVehicle(vehicle)
    }

    private fun calculateFee(type: VehicleType, parkedTime: Int, hasDiscountCard: Boolean): Int {
        var total = type.rate
        val time = parkedTime - 120
        val fraction = 15
        if(parkedTime > 120){
            val rest = if(time % fraction > 0 ) 1 else 0
            total += ((time / fraction) + rest ) * 5
        }

        if(hasDiscountCard) {
            total -= (total * 15) / 100
        }

        return total
    }

    fun checkOutVehicle(plate: String, onSuccess: (Int) -> Unit, onError: () -> Unit) {
        val vehicle: Vehicle? = parking.vehicles.firstOrNull { it.plate == plate }
        vehicle?.let {
            val fee = calculateFee(it.type, it.parkedTime.toInt(), !it.discountCard.isNullOrEmpty())
            parking.removeVehicle(plate, fee)
            onSuccess(fee)
        }?: run {
            onError()
        }
    }
}

// Set because we do not want to store vehicles with the same license plate
data class Parking(val vehicles: MutableSet<Vehicle>) {
    private val maxCapacity: Int = 20

    private var checkoutVehiclesPair: Pair<Int, Int> = Pair(0, 0)

    fun listVehicles() {
        vehicles.forEach {
            println("Plate: ${it.plate}")
        }
    }

    fun showEarnings(){
        println("${checkoutVehiclesPair.first} vehicles have checked out and have earnings of $${checkoutVehiclesPair.second}")
    }

    fun removeVehicle(plate: String, fee: Int) {
        val vehicle : Vehicle? = vehicles.firstOrNull { it.plate == plate}

        vehicle?.let{
            vehicles.remove(it)
            registerVehicle(fee)
        }
    }

    private fun registerVehicle(amount : Int) {
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

}

// Answer question one == why set is a list of data in no specific order, which cannot have duplicates
data class Vehicle(val plate: String, val type: VehicleType, val checkIntTime: Calendar, val checkOutTime: Calendar = Calendar.getInstance(), val discountCard: String? = null) {
    val parkedTime: Long
    get() = (checkOutTime.timeInMillis - checkIntTime.timeInMillis) / 60000
    // get() = (Calendar.getInstance().timeInMillis - checkIntTime.timeInMillis) / 60000
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
    showDemo()
}

fun showDemo(){
    println("Adding vehicles...")

    val vehiclesTest = mutableListOf(
        Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), discountCard = "DISCOUNT_CARD_001"), // DISCOUNT_CARD_001
        Vehicle("AA111A1", VehicleType.MOTORCYCLE, Calendar.getInstance()),
        Vehicle("AA111A2", VehicleType.BUS, Calendar.getInstance()),
        Vehicle("AA111A3", VehicleType.MINIBUS, Calendar.getInstance()),
        Vehicle("AA111A4", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A5", VehicleType.MOTORCYCLE, Calendar.getInstance()),
        Vehicle("AA111A6", VehicleType.BUS, Calendar.getInstance()),
        Vehicle("AA111A7", VehicleType.MINIBUS, Calendar.getInstance()),
        Vehicle("AA111A8", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A9", VehicleType.MOTORCYCLE, Calendar.getInstance()),
        Vehicle("AA111A10", VehicleType.BUS, Calendar.getInstance()),
        Vehicle("AA111A11", VehicleType.MINIBUS, Calendar.getInstance()),
        Vehicle("AA111A12", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A13", VehicleType.MOTORCYCLE, Calendar.getInstance()),
        Vehicle("AA111A14", VehicleType.BUS, Calendar.getInstance()),
        Vehicle("AA111A16", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A17", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A18", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A19", VehicleType.CAR, Calendar.getInstance()),
        Vehicle("AA111A20", VehicleType.CAR, Calendar.getInstance())
    )

    val parking = Parking(mutableSetOf())
    val parkingSpace = ParkingSpace(parking)
    vehiclesTest.forEach {parkingSpace.checkIn(it)}

    println("\nTest: Error Max Capacity")
    parkingSpace.checkIn(Vehicle("AA111A30", VehicleType.MINIBUS, Calendar.getInstance()))

    println("\nTest: Plate error")
    parkingSpace.checkIn(Vehicle("AA111A20", VehicleType.MINIBUS, Calendar.getInstance()))

    println("\nTest: Success check-out")
    parkingSpace.checkOutVehicle("AA111A4", ::onSuccess, ::onError)

    println("\nTest: Error check-out")
    parkingSpace.checkOutVehicle("AA111A4", ::onSuccess, ::onError)

    println("\nTest: Check-out with discount card")
    parkingSpace.checkOutVehicle("AA111AA", ::onSuccess, ::onError)

    println("\nTest: Show all vehicles in the parking")
    parking.listVehicles()

    println("\nTest: Show earnings")
    parking.showEarnings()
}

fun onSuccess(x: Int) =
    println("Your fee is $$x. Come back soon.")

fun onError() =
    println("Sorry, the check-out failed")