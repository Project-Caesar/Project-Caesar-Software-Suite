package usecases

fun callArduino() {
    val command = arrayOf("sh", "-c", "echo 5 > /dev/ttyUSB0")
    val echoProcess = Runtime.getRuntime().exec(command)
    echoProcess.waitFor()
    //Thread.sleep(1_000)
}