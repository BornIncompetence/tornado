package com.greer.tornado

import javafx.geometry.Pos
import tornadofx.*

class ChangeCancelAppointment : Fragment() {
    private val ctrl: ChooseAppointment by inject()
    private var appointmentModel = AppointmentModel(ctrl.appointments.first())

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Edit Appointment") {
                combobox(appointmentModel.itemProperty, ctrl.appointments) {
                    cellFormat {
                        text = "NAME: ${it.title} DATES: (${it.startDate}) - (${it.endDate}) ID: ${it.id}"
                    }
                }
                field("Appointment Name").textfield(appointmentModel.title)
                field("Start Date").textfield(appointmentModel.start).promptText = "YYYY-MM-DD HH:MM:SS"
                field("End Date").textfield(appointmentModel.end).promptText = "YYYY-MM-DD HH:MM:SS"
            }
        }
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0

            button("Change").action {
                val result = change()

                find<Popup>(mapOf(Popup::message to result))
                    .openModal(resizable = false)

                ctrl.updateComboBox()
            }
            button("Go back").action {
                close()
            }
            button("Cancel appointment").action {
                val result = cancel()

                find<Popup>(mapOf(Popup::message to result))
                    .openModal(resizable = false)

                ctrl.updateComboBox()
            }
        }
    }

    private fun cancel(): String {
        return if (appointmentModel.item != null) {
            val appointment = Appointment(
                appointmentModel.title.value,
                appointmentModel.start.value,
                appointmentModel.end.value,
                appointmentModel.id.value
            )
            val deleteStatement = connection.createStatement()
            return try {
                deleteStatement.executeUpdate(SQL.removeAppointment(appointment.id))
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
                appointmentModel.start.value,
                appointmentModel.end.value,
                appointmentModel.id.value
            )
            var success = true
            val titleStatement = connection.createStatement()
            val startStatement = connection.createStatement()
            val endStatement = connection.createStatement()


            titleStatement.executeUpdate(SQL.changeTitle(appointment.title, appointment.id))
            //TODO: A Sensible date-picker so we don't have to check for exceptions
            try {
                startStatement.executeUpdate(SQL.changeStart(appointment.startDate, appointment.id))
            } catch (e: Exception) {
                success = false
            }
            try {
                endStatement.executeUpdate(SQL.changeEnd(appointment.endDate, appointment.id))
            } catch (e: Exception) {
                success = false
            }

            if (success) {
                "Successfully updated all fields"
            } else {
                "Failed to update at least one field"
            }
        } else {
            "Invalid ID used, please select an appointment from the list"
        }
    }
}

