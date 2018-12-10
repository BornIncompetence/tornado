package com.greer.tornado

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.CheckBox
import tornadofx.*
import java.sql.Connection
import java.time.LocalDate

fun main(args: Array<String>) {
    launch<Scheduler>(args)
}

class Scheduler : App(Login::class)

class Account(
    email: String? = null,
    username: String? = null,
    password: String? = null,
    phone: String? = null,
    id: Int? = null
) {
    val emailProperty = SimpleStringProperty(this, "", email)
    var email by emailProperty

    val usernameProperty = SimpleStringProperty(this, "", username)
    var username by usernameProperty

    val passwordProperty = SimpleStringProperty(this, "", password)
    var password by passwordProperty

    val phoneProperty = SimpleStringProperty(this, "", phone)
    var phone by phoneProperty

    val idProperty = SimpleIntegerProperty(this, "", id!!)
    var id by idProperty
}

class Appointment(
    title: String? = null,
    startDate: LocalDate? = null,
    startTime: String? = null,
    endDate: LocalDate? = null,
    endTime: String? = null,
    id: Int = 0,
    reminder: Int? = null
) {
    val titleProperty = SimpleStringProperty(this, "", title)
    var title by titleProperty

    val startDateProperty = SimpleObjectProperty<LocalDate>(this, "", startDate)
    var startDate by startDateProperty

    val endDateProperty = SimpleObjectProperty<LocalDate>(this, "", endDate)
    var endDate by endDateProperty

    val startTimeProperty = SimpleStringProperty(this, "", startTime)
    var startTime by startTimeProperty

    val endTimeProperty = SimpleStringProperty(this, "", endTime)
    var endTime by endTimeProperty

    val idProperty = SimpleIntegerProperty(this, "", id)
    var id by idProperty

    val reminderProperty = SimpleIntegerProperty(this, "", reminder!!)
    var reminder by reminderProperty

    val selectedProperty = SimpleBooleanProperty()
}

lateinit var connection: Connection
// lateinit var account: Account
