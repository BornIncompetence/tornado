package com.greer.tornado

import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import tornadofx.*
import java.io.File
import kotlin.random.Random

class FileExplorer : View("Import Screen") {
    val file: File by param()

    private val ctrlAccount: AccountController by inject()
    private val ctrlImport: ImportController by inject()
    private val isOverwrite = SimpleBooleanProperty()

    override val root = borderpane {
        center = vbox {
            paddingAll = 20
            spacing = 10.0

            tableview(ctrlImport.appointments) {
                column("Select", Appointment::selectedProperty).useCheckbox()
                column("ID", Appointment::idProperty)
                column("Title", Appointment::titleProperty)
                column("Start Date", Appointment::startDateProperty)
                column("End Date", Appointment::endDateProperty)
            }
            checkbox("Override pre-existing appointments", isOverwrite) {
                isSelected = true
            }
        }

        bottom = hbox {
            paddingAll = 20
            spacing = 20.0
            alignment = Pos.CENTER_RIGHT

            button("Import").action {
                if (isOverwrite.value) {
                    val (adds, overwrites) = overWrite()
                    find<Popup>(mapOf(Popup::message to "Added $adds entries and overwritten $overwrites entries"))
                        .openModal(resizable = false)
                } else {
                    val adds = softWrite()
                    find<Popup>(mapOf(Popup::message to "Added $adds new entries"))
                        .openModal(resizable = false)
                }
            }
            button("Cancel").action {
                close()
            }
        }
    }

    private fun softWrite(): Int {
        var i = 0

        ctrlImport.appointments.forEach {
            val idStatement = connection.createStatement()
            val idResult = idStatement.executeQuery(SQL.checkForExistingApt(it.id))

            // Skip over appointment is ID already exists
            if (!idResult.next()) {
                val creationStatement = connection.createStatement()
                val aptID = it.title.hashCode() + (ctrlAccount.account.id + Random.nextInt(100))
                creationStatement.executeUpdate(
                    SQL.createAppointment(
                        it.title,
                        "${it.startDate} ${it.startTime}",
                        "${it.endDate} ${it.endTime}",
                        ctrlAccount.account.id,
                        aptID,
                        it.reminder
                    )
                )
                ++i
            }
        }

        return i
    }

    private fun overWrite(): Pair<Int, Int> {
        var adds = 0
        var overwrites = 0

        ctrlImport.appointments.forEach {
            val idStatement = connection.createStatement()
            val idResult = idStatement.executeQuery(SQL.checkForExistingApt(it.id))

            // Modify if the ID already exists, else create a new one
            if (idResult.next()) {
                val titleStatement = connection.createStatement()
                val startStatement = connection.createStatement()
                val endStatement = connection.createStatement()

                titleStatement.executeUpdate(SQL.changeTitle(it.title, it.id))
                startStatement.executeUpdate(
                    SQL.changeStart(
                        "${it.startDate} ${it.startTime}",
                        it.id
                    )
                )
                endStatement.executeUpdate(
                    SQL.changeEnd(
                        "${it.endDate} ${it.endTime}",
                        it.id
                    )
                )
                ++overwrites
            } else {
                val creationStatement = connection.createStatement()
                val aptID = it.title.hashCode() + (ctrlAccount.account.id + Random.nextInt(100))
                creationStatement.executeUpdate(
                    SQL.createAppointment(
                        it.title,
                        "${it.startDate} ${it.startTime}",
                        "${it.endDate} ${it.endTime}",
                        ctrlAccount.account.id,
                        aptID,
                        it.reminder
                    )
                )
                ++adds
            }
        }

        return adds to overwrites
    }
}