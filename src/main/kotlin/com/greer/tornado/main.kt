package com.greer.tornado

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.sql.Connection

fun main(args: Array<String>) {
    launch<Scheduler>(args)
}

class Scheduler : App(Login::class)

data class Account(
    var email: String,
    var username: String,
    var password: String,
    var phone: String?,
    var id: Int
)

class Appointment(
    title: String? = null,
    startDate: String? = null,
    endDate: String? = null,
    id: Int? = null
) {
    val titleProperty = SimpleStringProperty(this, "", title)
    var title by titleProperty

    val startProperty = SimpleStringProperty(this, "", startDate)
    var startDate by startProperty

    val endProperty = SimpleStringProperty(this, "", endDate)
    var endDate by endProperty

    val idProperty = SimpleIntegerProperty(this, "", id!!)
    var id by idProperty
}

lateinit var connection: Connection
lateinit var account: Account
