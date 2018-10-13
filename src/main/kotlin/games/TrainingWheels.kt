package games

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.InputEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TouchEvent
import javafx.scene.layout.Pane
import tornadofx.*
import java.io.File
import java.util.concurrent.ThreadLocalRandom

class TrainingWheels(imageFile : String, val shrinkLimit : Double = .10, val shrinkRate : Int = 1) : View() {

    private val image = Image(File(imageFile).toURI().toString())
    private var successCount = 0
    private var selectCount = 0

    // set the root as a basic Pane()
    override val root = Pane()

    init {
        // This just sets up the root as part of the initialization of MainView. Otherwise, just
        // add { } to Pane and put all of this in there
        with(root) {

            // creates and adds the imageview object to root
            imageview(image) {
                preserveRatioProperty().value = true

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

                widthProperty().onChangeOnce {
                    if (image.width >= image.height) {
                        fitWidth = root.width * .9
                        fitHeight = (fitWidth / image.width) * image.height
                    } else {
                        fitHeight = root.height * .9
                        fitWidth = (fitHeight / image.height) * image.width
                    }
                }


                // catch any touch or mouse clicked event on the imageview and call
                // targetSelected when those events occur
                addEventFilter(InputEvent.ANY) {
                    if (it.eventType == MouseEvent.MOUSE_CLICKED || it.eventType == TouchEvent.TOUCH_PRESSED) {
                        targetSelected(this)
                        successCount++
                    }
                }
            }

            addEventFilter(InputEvent.ANY) {
                if (it.eventType == MouseEvent.MOUSE_CLICKED || it.eventType == TouchEvent.TOUCH_PRESSED) {
                    selectCount++
                }
            }
        }

    }

    // This function either shrinks or moves the imageview around the screen
    private fun targetSelected(target : ImageView) {
        if (target.fitHeight < root.height * shrinkLimit) {
            target.xProperty().unbind()
            target.yProperty().unbind()

            // Gets random x and y coordinates such that the entire image will always
            // appear in the window based on its size at the time the imageview is clicked/touched.
            // Some type changes used based on what needs an int or a double
            target.x = ThreadLocalRandom.current().nextInt((root.width - target.fitWidth).toInt()).toDouble()
            target.y = ThreadLocalRandom.current().nextInt((root.height - target.fitHeight).toInt()).toDouble()
        } else {
            if (successCount % shrinkRate == 0) {
                target.fitWidth = target.fitWidth * 0.9
                target.fitHeight = target.fitHeight * 0.9
            }
        }
        //println("${target.fitWidth} ${target.fitHeight} ${target.x} ${target.y} ${root.width} ${root.height}")
    }

}
