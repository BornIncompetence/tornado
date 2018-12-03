package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*
import kotlin.random.Random

class CreateAppointment : View() {
    private val name = SimpleStringProperty("")
    private val start = SimpleStringProperty("")
    private val end = SimpleStringProperty("")

    private val ctrl: ChooseAppointment by inject()

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Create New Appointment") {
                field("Appointment Name").textfield(name)
                field("Start Date").textfield(start).promptText = "YYYY-MM-DD HH:MM:SS"
                field("End Date").textfield(end).promptText = "YYYY-MM-DD HH:MM:SS"
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
                    //TODO: Update appointments
                    ctrl.updateComboBox()
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
        val aptID = name.value.hashCode() + (account.id + Random.nextInt(100))

        return try {
            appointmentStatement.executeUpdate(
                SQL.createAppointment(
                    name.value,
                    start.value,
                    end.value,
                    account.id,
                    aptID
                )
            )
            "Appointment created!" to true
        } catch (e: Exception) {
            e.printStackTrace()
            "You cannot have duplicate tasks" to false
        }
    }
}