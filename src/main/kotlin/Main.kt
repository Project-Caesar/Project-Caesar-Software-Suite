import games.TrainingWheelsApp
import tornadofx.launch

fun main(args: Array<String>) {
    println(args[0])
    launch<TrainingWheelsApp>(args)
}