package games

import javafx.animation.FadeTransition
import javafx.scene.image.ImageView
import javafx.scene.input.InputEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TouchEvent
import javafx.scene.layout.Pane
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import java.util.concurrent.ThreadLocalRandom
import tornadofx.*

class TrainingWheels : View() {


    val viewModel : TrainingWheelsViewModel by inject()

    private var successCount = 0
    private var currentFailCount = 0 // -1 as a success will make it 0 in filter on line 88

    private lateinit var fadeIn : FadeTransition
    private lateinit var fadeOut : FadeTransition

    //private val successAudio = MediaPlayer(viewModel.successAudio.value)
    //private val failAudio = MediaPlayer(viewModel.failAudio.value)

    // set the root as a basic Pane()
    override val root = Pane()

    init {
        // This just sets up the root as part of the initialization of MainView. Otherwise, just
        // add { } to Pane and put all of this in there
        with(root) {

            rectangle {
                fill = Color.TRANSPARENT

                widthProperty().bind(root.widthProperty())
                heightProperty().bind(root.heightProperty())

                addEventFilter(InputEvent.ANY) {
                    if (it.eventType == MouseEvent.MOUSE_RELEASED || it.eventType == TouchEvent.TOUCH_RELEASED) {
                        currentFailCount++
                    }
                }
            }

            // creates and adds the imageview object to root
            imageview(viewModel.selectedIconPreview.value) {
                preserveRatioProperty().value = true
                opacity = 0.0

                fadeIn = this.fade(javafx.util.Duration(1000.0), 100, play=false)
                fadeOut = this.fade(javafx.util.Duration(1000.0), 0, play=false)

                fadeOut.setOnFinished {
                    targetSelected(this)
                    println(currentFailCount)
                    successCount++
                    // call the csv writer
                    currentFailCount = 0
                    fadeIn.play()
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
                    if (it.eventType == MouseEvent.MOUSE_RELEASED || it.eventType == TouchEvent.TOUCH_RELEASED) {
                        if (this.opacity == 1.0) fadeOut.play()
                    }
                }
            }
        }

        fadeIn.play()
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
        //println("${target.fitWidth} ${target.fitHeight} ${target.x} ${target.y} ${root.width} ${root.height}")
    }

}
