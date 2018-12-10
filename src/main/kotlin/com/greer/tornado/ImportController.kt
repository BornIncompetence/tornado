package com.greer.tornado

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate

class ImportController : Controller() {
    val appointments: ObservableList<Appointment> =
        FXCollections.observableArrayList<Appointment>()

    fun read(file: File) {
        appointments.clear()
        file.forEachLine { line ->
            val tokens = line.split(",")

            if (tokens.size != 4) {
                throw ProjectException()
            }

            val start = tokens[1].split(" ")
            val end = tokens[2].split(" ")

            appointments.add(
                Appointment(
                    tokens[0],
                    LocalDate.parse(start[0]),
                    start[1],
                    LocalDate.parse(end[0]),
                    end[1],
                    tokens[3].toInt()
                )
            )
        }
    }

    class ProjectException : Exception("The file is formatted incorrectly")
}