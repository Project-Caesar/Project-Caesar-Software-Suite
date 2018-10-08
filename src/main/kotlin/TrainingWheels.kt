import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.input.TouchEvent
import javafx.scene.layout.Pane
import tornadofx.*
import java.util.concurrent.ThreadLocalRandom

fun main(args: Array<String>) {
    launch<TrainingWheelsApp>(args)
}

// create app class; contains main starting view and any stylesheets (not used here)
class TrainingWheelsApp : App(MainView::class)

// a view is what is displayed in the window. To add visuals, orverride and change/add items to the root
class MainView : View() {

    val image = Image("/Burrito-icon.png") // can read any file in the resources file

    // set the root as a basic Pane()
    override val root = Pane()

    init {
        // This just sets up the root as part of the initialization of MainView. Otherwise, just
        // add { } to Pane and put all of this in there
        with(root) {

            // set starting MainView size
            prefWidth = 800.0
            prefHeight = 800.0

            // creates and adds the imageview object to root
            imageview(image) {
                preserveRatioProperty().value = true

                /**
                 * binding sets the bound property value to another. In this case
                 * the x and y coordiantes (top left hand corner of the imageview) are
                 * being bound so that the image view is alwys in the center of the window,
                 * no matter what the size of the window is and even changes as the window
                 * size changes
                 */
                xProperty().bind(root.widthProperty()/2 - fitWidthProperty()/2)
                yProperty().bind(root.heightProperty()/2 - fitHeightProperty()/2)

                // this is just setting the starting size of the image based on the starting
                // window size
                fitWidth = root.prefWidth / 2
                fitHeight = (fitWidth / image.width) * image.height

                // catch any touch or mouse clicked event on the imageview and call
                // targetSelected when those events occur
                addEventFilter(MouseEvent.MOUSE_CLICKED) { targetSelected(this) }
                addEventFilter(TouchEvent.TOUCH_PRESSED) { targetSelected(this) }
            }


        }

    }

    // This function either shrinks or moves the imageview around the screen
    private fun targetSelected(target : ImageView) {
        if (target.fitHeight < root.height / 10) {

            target.xProperty().unbind()
            target.yProperty().unbind()

            // Gets random x and y coordinates such that the entire image will always
            // appear in the window based on its size at the time the imageview is clicked/touched.
            // Some type changes used based on what needs an int or a double
            target.x = ThreadLocalRandom.current().nextInt((root.width - target.fitWidth).toInt()).toDouble()
            target.y = ThreadLocalRandom.current().nextInt((root.height - target.fitHeight).toInt()).toDouble()
        } else {
            target.fitWidth = target.fitWidth * 0.9
            target.fitHeight = target.fitHeight * 0.9
        }
        //println("${target.fitWidth} ${target.fitHeight} ${target.x} ${target.y} ${root.width} ${root.height}")
    }

}
