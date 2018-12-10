package com.greer.tornado

import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation
import javafx.geometry.Pos
import tornadofx.*

class ChangeCancelAppointment : Fragment() {
    private val ctrlAppointment: AppointmentController by inject()
    private var appointmentModel = AppointmentModel(ctrlAppointment.appointments.first())

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Edit Appointment") {
                combobox(appointmentModel.itemProperty, ctrlAppointment.appointments) {
                    cellFormat {
                        text = "NAME: ${it.title} DATES: (${it.startDate}) - (${it.endDate}) ID: ${it.id}"
                    }
                }
                field("Appointment Name").textfield(appointmentModel.title)
                hbox {
                    alignment = Pos.CENTER
                    spacing = 20.0

                    field("Start Date").datepicker(appointmentModel.startDate)
                    field().textfield(appointmentModel.startTime).promptText = "HH:MM:SS"
                }
                hbox {
                    alignment = Pos.CENTER
                    spacing = 20.0

                    field("End Date").datepicker(appointmentModel.endDate)
                    field().textfield(appointmentModel.endTime).promptText = "HH:MM:SS"
                }
                field("Remind me in...").combobox(property = appointmentModel.reminder) {
                    items = observableList(0, 30, 60, 120, 240, 3600)
                    this.cellFormat {
                        text = when (it.toInt()) {
                            0 -> "Never"
                            30 -> "Minutes"
                            in 60..119 -> "1 Hour"
                            in 120..3599 -> "${it.toInt() / 60} Hours"
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

            button("Change").action {
                val result = change()

                find<Popup>(mapOf(Popup::message to result))
                    .openModal(resizable = false)

                ctrlAppointment.updateComboBox()
            }
            button("Go back").action {
                close()
            }
            button("Cancel appointment").action {
                val result = cancel()

                find<Popup>(mapOf(Popup::message to result))
                    .openModal(resizable = false)

                ctrlAppointment.updateComboBox()
            }
        }
    }

    private fun cancel(): String {
        return if (appointmentModel.item != null) {
            val appointment = Appointment(
                appointmentModel.title.value,
                appointmentModel.startDate.value,
                appointmentModel.startTime.value,
                appointmentModel.endDate.value,
                appointmentModel.endTime.value,
                appointmentModel.id.value.toInt(),
                appointmentModel.reminder.value.toInt()
            )
            val deleteStatement = connection.createStatement()
            return try {
                deleteStatement.executeUpdate(
                    SQL.removeAppointment(appointment.id)
                )
                "Removed ${appointment.title}"
            } catch (e: Exception) {
                "Fatal error! Unable to remove appointment!"
            }
        } else {
            "Invalid ID used, please select an appointment from the list"
        }
    }

    private fun change(): String {
        return if (appointmentModel.item != null) {
            val appointment = Appointment(
                appointmentModel.title.value,
                appointmentModel.startDate.value,
                appointmentModel.startTime.value,
                appointmentModel.endDate.value,
                appointmentModel.endTime.value,
                appointmentModel.id.value.toInt(),
                appointmentModel.reminder.value.toInt()
            )
            var success = true
            val titleStatement = connection.createStatement()
            val startStatement = connection.createStatement()
            val endStatement = connection.createStatement()
            val remindStatement = connection.createStatement()

            titleStatement.executeUpdate(
                SQL.changeTitle(appointment.title, appointment.id)
            )
            try {
                startStatement.executeUpdate(
                    SQL.changeStart(
                        "${appointment.startDate} ${appointment.startTime}",
                        appointment.id
                    )
                )
            } catch (e: MysqlDataTruncation) {
                success = false
            }
            try {
                endStatement.executeUpdate(
                    SQL.changeEnd(
                        "${appointment.endDate} ${appointment.endTime}",
                        appointment.id
                    )
                )
            } catch (e: MysqlDataTruncation) {
                success = false
            }

            remindStatement.executeUpdate(
                SQL.changeReminder(appointment.reminder, appointment.id)
            )

            if (success) {
                "Successfully updated all fields"
            } else {
                "Failed to update at least one field: Wrong format for date-time entry"
            }
        } else {
            "Invalid ID used, please select an appointment from the list"
        }
    }
}

