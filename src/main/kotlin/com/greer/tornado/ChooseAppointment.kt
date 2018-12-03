package com.greer.tornado

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.Controller

class ChooseAppointment : Controller() {
    val appointments: ObservableList<Appointment> =
        FXCollections.observableArrayList<Appointment>()

    init {
        updateComboBox()
    }

    fun updateComboBox() {
        appointments.clear()
        val getAptStatement = connection.createStatement()
        val aptResult = getAptStatement.executeQuery(SQL.getAppointments(account.username))

        while (aptResult.next()) {
            val title = aptResult.getString("title")
            val start = aptResult.getString("start_date")
            val end = aptResult.getString("end_date")
            val id = aptResult.getInt("appointment_id")

            appointments.add(Appointment(title, start, end, id))
        }
    }
}