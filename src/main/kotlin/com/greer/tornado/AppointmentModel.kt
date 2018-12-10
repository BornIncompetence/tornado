package com.greer.tornado

import tornadofx.ItemViewModel

class AppointmentModel(appointment: Appointment) : ItemViewModel<Appointment>(appointment) {
    val title = bind(Appointment::titleProperty)
    val startDate = bind(Appointment::startDateProperty)
    val startTime = bind(Appointment::startTimeProperty)
    val endDate = bind(Appointment::endDateProperty)
    val endTime = bind(Appointment::endTimeProperty)
    val id = bind(Appointment::idProperty)
    val reminder = bind(Appointment::reminderProperty)
}