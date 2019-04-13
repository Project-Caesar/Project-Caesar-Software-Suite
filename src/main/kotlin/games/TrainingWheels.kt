package games

import javafx.util.Duration
import javafx.animation.FadeTransition
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType
import javafx.scene.image.ImageView
import javafx.scene.input.InputEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TouchEvent
import javafx.scene.layout.Pane
import javafx.scene.media.AudioClip
import javafx.scene.shape.Rectangle
import java.util.concurrent.ThreadLocalRandom
import tornadofx.*
import usecases.CSV
import usecases.callArduino
import java.sql.Timestamp
import java.util.*
import kotlin.concurrent.timerTask

class TrainingWheels : View() {


    private val viewModel : TrainingWheelsViewModel by inject()

    private lateinit var gameBackground : Rectangle
    private lateinit var gameTarget : ImageView

    private val startTimer = Timer()
    private val delayTimer = Timer()

    private var startTime = 0.toLong()

    private var readyToStart = false

    private var successCount = 0
    private var currentFailCount = 0

    private lateinit var targetFadeIn : FadeTransition
    private lateinit var targetFadeOut : FadeTransition

    private val successAudio = AudioClip(viewModel.successAudio.value.source)
    private val failAudio = AudioClip(viewModel.failAudio.value.source)

    private val exitDialog = Alert(AlertType.CONFIRMATION).apply {
        // set up exitDialog text
        this.headerText = "End Training Wheels Test"
        this.contentText = "Select OK to end test or cancel to continue"
    }
    //private val exitTest = mutableListOf(false,false,false,false)

    private val csvWriter = CSV(
            viewModel.csvHeaders,
            "${viewModel.testeeName.value}_${Timestamp(System.currentTimeMillis())}.csv".replace(":", "_"),
            viewModel.dataFileDirectory.value
    )


    // set the root as a basic Pane()
    override val root = Pane()

    init {
        //println("\"${viewModel.testeeName.value}_${Timestamp(System.currentTimeMillis())}.csv".replace(" ", "_") + " ${viewModel.dataFileDirectory.value}")
        with(root) {

            // Creates and adds the imageview target to root
            imageview(viewModel.selectedIconPreview.value) {

                gameTarget = this

                preserveRatioProperty().value = true
                opacity = 0.0

                // initialize the fade variables
                targetFadeIn = this.fade(Duration(1000.0), 100, play=false)
                targetFadeOut = this.fade(Duration(1000.0), 0, play=false)

                targetFadeOut.setOnFinished {

                    // moves or shrinks target
                    targetSelected(targetFadeOut.node as ImageView)

                    // triggers the arduino to release a pellet for the subject
                    //callArduino(viewModel.pid_num)
                    callArduino("Caeser")

                    successCount++
                    // call the csv writer
                    callCSVWriter(targetFadeOut.node as ImageView)
                    currentFailCount = 0

                    delayTimer.schedule(
                            timerTask {
                                targetFadeIn.play()
                            },
                            viewModel.delayBetweenTrials.value.toLong() * 1000
                    )
                }

                /**
                 * binding sets the bound property value to another. In this case
                 * the x and y coordinates (top left hand corner of the imageview) are
                 * being bound so that the image view is always in the center of the window,
                 * no matter what the size of the window is and even changes as the window
                 * size changes
                 */
                xProperty().bind(root.widthProperty()/2 - fitWidthProperty()/2)
                yProperty().bind(root.heightProperty()/2 - fitHeightProperty()/2)

                // this is just setting the starting size of the image based on the starting
                // window size

                heightProperty().onChangeOnce {
                    if (image.width >= image.height) {
                        fitWidth = root.width * viewModel.iconStartSize.value
                        fitHeight = (fitWidth / image.width) * image.height
                        if (fitHeight > root.height) {
                            fitHeight = root.height * viewModel.iconStartSize.value
                            fitWidth = (fitHeight / image.height) * image.width
                        }
                    } else {
                        fitHeight = root.height * viewModel.iconStartSize.value
                        fitWidth = (fitHeight / image.height) * image.width
                        if (fitWidth > root.width) {
                            fitWidth = root.width * viewModel.iconStartSize.value
                            fitHeight = (fitWidth / image.width) * image.height
                        }
                    }
                }


                // catch any touch or mouse clicked event on the imageview and call
                // targetSelected when those events occur
                addEventFilter(InputEvent.ANY) {
                    if (it.eventType == MouseEvent.MOUSE_RELEASED && readyToStart) {
                        if (this.opacity == 1.0) {
                            successAudio.play()
                            targetFadeOut.play()
                        }
                    }
                }
            }

            rectangle {

                gameBackground = this

                // Set to window size
                widthProperty().bind(root.widthProperty())
                heightProperty().bind(root.heightProperty())

                // rectangle fill is black, so the fades are called in reverse order


                // Keep the screen dark for 30 seconds, then start the test
                startTimer.schedule(
                        timerTask {
                            this@rectangle.opacity = 0.0
                            targetFadeIn.play()
                            readyToStart = true
                            startTimer.cancel()
                            startTime = System.currentTimeMillis()
                        },
                        viewModel.delayAtStart.longValue()
                )

                // try mouse dragged entered and exit?
                setOnMouseDragged {
                    // check and see if the point is close to a corner and set tht value as true
                    // in exitTest
                    if (it.sceneX < 50 && it.sceneY < 50) {
                        viewModel.passExitTestAt(0)
                        //exitTest[0] = true
                        //println(0)
                    } else if (it.sceneX < 50 && it.sceneY > root.height - 50) {
                        viewModel.passExitTestAt(1)
                        //exitTest[1] = true
                        //println(1)
                    } else if (it.sceneX > root.width - 50 && it.sceneY < 50) {
                        viewModel.passExitTestAt(2)
                        //exitTest[2] = true
                        //println(2)
                    } else if (it.sceneX > root.width - 50 && it.sceneY > root.height - 50) {
                        viewModel.passExitTestAt(3)
                        //exitTest[3] = true
                        //println(3)
                    }
                }

                // Check for any mouse click or touchscreen events
                addEventFilter(InputEvent.ANY) {
                    // if there is input
                    if ((it.eventType == MouseEvent.MOUSE_RELEASED || it.eventType == TouchEvent.TOUCH_RELEASED)) {

                        // Check if the input fails the exit condition
                        if (!viewModel.checkExitTestPass()) {
                            viewModel.resetExitTest()

                            // if the input fails the exit condition during the test when a fail can
                            // be accepted, then trigger fail
                            if (readyToStart && gameTarget.opacity == 1.0) {
                                if (viewModel.useFailAuidio.value && !isAudioPlaying()) failAudio.play()
                                currentFailCount++
                            }

                            // else the input passes the exit condition
                        } else {

                            // give user opportunity to end the test
                            val popupResult = exitDialog.showAndWait()

                            if (popupResult.get() == ButtonType.OK) {
                                println("Test Exit Dialog Triggered")
                                replaceWith(find<TrainingWheelsMenu>())
                            } else {
                                viewModel.resetExitTest()
                            }
                        }
                    }
                }
            }.toBack()
        }
    }

    private fun isAudioPlaying() : Boolean {
        //println("${failAudio.isPlaying} ${successAudio.isPlaying} ${failAudio.isPlaying || successAudio.isPlaying}")
        return failAudio.isPlaying || successAudio.isPlaying
    }

    // This function either shrinks or moves the imageview around the screen
    private fun targetSelected(target : ImageView) {
        if (target.fitHeight < root.height * viewModel.iconShrinkLimit.value) {
            target.xProperty().unbind()
            target.yProperty().unbind()

            // Gets random x and y coordinates such that the entire image will always
            // appear in the window based on its size at the time the imageview is clicked/touched.
            // Some type changes used based on what needs an int or a double
            target.x = ThreadLocalRandom.current().nextInt((root.width - target.fitWidth).toInt()).toDouble()
            target.y = ThreadLocalRandom.current().nextInt((root.height - target.fitHeight).toInt()).toDouble()
        } else {
            if (successCount % viewModel.iconClicksToShrink.value == 0) {
                target.fitWidth = target.fitWidth * viewModel.iconShrinkRatio.value
                target.fitHeight = target.fitHeight * viewModel.iconShrinkRatio.value
            }
        }
    }

    private fun callCSVWriter(imageView : ImageView) {
        val timeTillSuccess = System.currentTimeMillis() - startTime
        val stuff = arrayOf(
                viewModel.testeeName.value,
                viewModel.selectedIconName.value,
                imageView.fitHeight.toString(),
                imageView.fitHeight.toString(),
                imageView.x.toString(),
                imageView.y.toString(),
                currentFailCount.toString(),
                timeFormatter(timeTillSuccess),
                Timestamp(System.currentTimeMillis()).toString()
        )

        csvWriter.addLine(stuff)

        startTime = System.currentTimeMillis()
    }

    private fun timeFormatter(time : Long) : String {
        val millis = time % 1000
        val second = time / 1000 % 60
        val minute = time / (1000 * 60) % 60
        val hour = time / (1000 * 60 * 60) % 24

        return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis)
    }

}


