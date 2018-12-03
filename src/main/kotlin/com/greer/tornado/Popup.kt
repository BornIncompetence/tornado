package com.greer.tornado

import javafx.geometry.Pos
import tornadofx.*

class Popup : Fragment("Message") {
    val message: String by param()

    override val root = gridpane {
        alignment = Pos.CENTER
        paddingAll = 20
        vgap = 10.0
        hgap = 10.0

        label(message).gridpaneConstraints {
            columnRowIndex(0, 0)
        }

        button("OK") {
            gridpaneConstraints { columnRowIndex(1, 1) }
            action { close() }
        }
    }
}