package com.greer.tornado

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller
import tornadofx.observable
import java.io.File
import java.time.LocalDate

class AppointmentController : Controller() {
    private val ctrlAccount: AccountController by inject()

    val appointments: ObservableList<Appointment> =
        FXCollections.observableArrayList<Appointment>()

    init {
        updateComboBox()
    }

    fun updateComboBox() {
        appointments.clear()
        val getAptStatement = connection.createStatement()
        val aptResult = getAptStatement.executeQuery(SQL.getAppointments(ctrlAccount.account.username))

        while (aptResult.next()) {
            val title = aptResult.getString("title")
            val start = aptResult.getString("start_date").split(" ")
            val end = aptResult.getString("end_date").split(" ")
            val id = aptResult.getInt("appointment_id")
            val reminder = aptResult.getInt("reminder")

            appointments.add(
                Appointment(
                    title,
                    LocalDate.parse(start.first()),
                    start.last(),
                    LocalDate.parse(end.first()),
                    end.last(),
                    id,
                    reminder
                )
            )
        }
    }


}