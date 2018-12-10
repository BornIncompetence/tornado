package com.greer.tornado

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*
import java.time.LocalDate
import kotlin.random.Random

class CreateAppointment : View() {
    private val name = SimpleStringProperty("")
    private val startDate = SimpleObjectProperty<LocalDate>()
    private val startTime = SimpleStringProperty("")
    private val endDate = SimpleObjectProperty<LocalDate>()
    private val endTime = SimpleStringProperty("")
    private val reminder = SimpleIntegerProperty(0)

    private val ctrlAppointment: AppointmentController by inject()
    private val ctrlAccount: AccountController by inject()

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Create New Appointment") {
                field("Appointment Name").textfield(name)
                hbox {
                    alignment = Pos.CENTER
                    spacing = 20.0

                    field("Start Date").datepicker(startDate)
                    field().textfield(startTime).promptText = "HH:MM"
                }
                hbox {
                    alignment = Pos.CENTER
                    spacing = 20.0

                    field("End Date").datepicker(endDate)
                    field().textfield(endTime).promptText = "HH:MM"
                }
                field("Remind me in...").combobox<Int> {
                    items = observableList(0, 30, 60, 120, 240, 3600)
                    cellFormat {
                        text = when (it) {
                            0 -> "Never"
                            30 -> "Minutes"
                            in 60..119 -> "1 Hour"
                            in 120..3599 -> "2 Hours"
                            3600 -> "1 Day"
                            else -> ""
                        }
                    }
                }
            }
        }
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0

            button("Create").action {
                val result = create()

                find<Popup>(mapOf(Popup::message to result.first))
                    .openModal(resizable = false)

                if (result.second) {
                    ctrlAppointment.updateComboBox()
                    close()
                }
            }
            button("Go back").action {
                close()
            }
        }
    }

    private fun create(): Pair<String, Boolean> {
        val appointmentStatement = connection.createStatement()
        val aptID = name.value.hashCode() + (ctrlAccount.account.id + Random.nextInt(100))

        return try {
            appointmentStatement.executeUpdate(
                SQL.createAppointment(
                    name.value,
                    "${startDate.value} ${startTime.value}",
                    "${endDate.value} ${endTime.value}",
                    ctrlAccount.account.id,
                    aptID,
                    reminder.value.toInt()
                )
            )
            "Appointment created!" to true
        } catch (e: Exception) {
            e.printStackTrace()
            "FATAL ERROR: Hash collision detected: You cannot have duplicate tasks" to false
        } catch (e: MysqlDataTruncation) {
            "Wrong format for date-time entry" to false
        }
    }
}