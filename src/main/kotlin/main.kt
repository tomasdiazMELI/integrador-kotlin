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

data class Parking(val vehicles: MutableSet<Vehicle>) {
    val maxCapacity: Int = 20

    private var checkoutVehiclesPair: Pair<Int, Int> = Pair(0, 0)

    fun listVehicles() {
        vehicles.forEach {
            println("Plate: ${it.plate}")
        }
    }

    fun showEarn(){
        println("${checkoutVehiclesPair.first} vehicles have checked out and have earnings of $${checkoutVehiclesPair.second}")
    }

    fun removeVehicle(plate: String, fee: Int) {
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

// Answer question one == why set is a list of data in no specific order, which cannot have duplicates
data class Vehicle(val plate: String, val type: VehicleType, val checkIntTime: Calendar, val checkOutTime: Calendar = Calendar.getInstance(), val discountCard: String? = null,) {
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
    var checkOutTime: Calendar = Calendar.getInstance()
    var checkInTime: Calendar = Calendar.getInstance()

    checkInTime.set(2021, 10, 26, 18,0)
    checkOutTime.set(2021, 10, 26, 20,0)


    var vehiclesTest = listOf<Vehicle>(
        Vehicle("AA111AA", VehicleType.CAR, checkInTime, checkOutTime), // DISCOUNT_CARD_001
        Vehicle("AA111A1", VehicleType.MOTORCYCLE, Calendar.getInstance()),
        Vehicle("AA111A2", VehicleType.BUS, Calendar.getInstance() ),
        Vehicle("AA111A3", VehicleType.MINIBUS, Calendar.getInstance()) ,
        Vehicle("AA111A4", VehicleType.CAR, Calendar.getInstance()) ,
        Vehicle("AA111A5", VehicleType.CAR, Calendar.getInstance() ),
        )


    val parking = Parking(mutableSetOf())
    val parkingSpace = ParkingSpace(parking)
    vehiclesTest.forEach {parkingSpace.checkIn(it)}

    var lala = vehiclesTest.first()

    parkingSpace.checkOutVehicle(lala.plate, ::onSuccess, ::onError)
    // parking.listVehicles()
    //parking.showEarn()


//    val car = Vehicle("AA111AA", VehicleType.CAR, Calendar.getInstance(), "DISCOUNT_CARD_001")
//    val moto = Vehicle("B222BBB", VehicleType.MOTORCYCLE, Calendar.getInstance())
//    val minibus = Vehicle("CC333CC", VehicleType.MINIBUS, Calendar.getInstance())
//    val bus = Vehicle("DD444DD", VehicleType.BUS, Calendar.getInstance(), "DISCOUNT_CARD_002")

    // val parking = Parking(mutableSetOf(car, moto, minibus, bus))
}

fun onSuccess(x: Int) =
    println("Your fee is $$x. Come back soon.")

fun onError() =
    println("Sorry, the check-out failed")