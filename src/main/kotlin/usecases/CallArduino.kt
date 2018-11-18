package usecases

fun callArduino(pid_num: String) {
    val command = arrayOf("screen", "-S", pid_num, "-p", "0", "-X", "stuff", "\"1\"")
    val echoProcess = Runtime.getRuntime().exec(command)
    echoProcess.waitFor()
    //Thread.sleep(1_000)
}