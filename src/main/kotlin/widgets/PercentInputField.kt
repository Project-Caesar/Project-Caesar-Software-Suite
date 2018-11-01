package widgets

import javafx.beans.property.SimpleDoubleProperty
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import tornadofx.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class PercentInputField (
        label : String,
        input : SimpleDoubleProperty,
        default : Double,
        minimum : Double = 0.0,
        maximum : Double = 1.0
) : VBox() {
    
    init {

        label {
            text = label
        }

        hbox {
            textfield {
                alignment = Pos.CENTER_RIGHT

                text = doubleToString(default * 100)

                textProperty().onChange {
                    if (it == null) {
                        input.set(default)
                    } else {
                        it.trim()
                        if (it.isDouble() && it.toDouble() / 100 >= minimum) {
                            if (it.toDouble() / 100 > maximum) {
                                input.set(maximum)
                            } else {
                                input.set(it.toDouble() / 100)
                            }
                        } else {
                            input.set(default)
                        }
                    }
                }

                focusedProperty().onChange {
                    if (!it) {
                        text = doubleToString(checkDouble(input.value * 100))
                    }
                }
            }

            text {
                text = "%"
                font = Font.font(font.family, 20.0)
            }
        }
    }
}

private fun checkDouble(value : Double) : Double {
    return if (abs(value.toInt().toDouble() - value) < .01)
        value.toInt().toDouble()
    else
        value
}

private fun doubleToString (value : Double) : String {
    return if (value == value.toLong().toDouble())
        String.format("%d", value.toInt())
    else
        String.format("%s", value)
}