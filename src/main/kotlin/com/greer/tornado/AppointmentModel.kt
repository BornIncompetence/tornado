package com.greer.tornado

import tornadofx.ItemViewModel

class AppointmentModel(appointment: Appointment) : ItemViewModel<Appointment>(appointment) {
    val title = bind(Appointment::title)
    val start = bind(Appointment::startDate)
    val end = bind(Appointment::endDate)
    val id = bind(Appointment::id)
}