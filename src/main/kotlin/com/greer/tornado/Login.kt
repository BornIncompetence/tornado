package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Login : View("Login") {
    private val username = SimpleStringProperty("")
    private val password = SimpleStringProperty("")

    override val root = vbox {
        form {
            fieldset("Login") {
                field("Username").textfield(username)
                field("Password").passwordfield(password)
            }
            button("Sign in").action {
                val result = login(username.value, password.value)
                if (result.second) {
                    replaceWith<Welcome>(ViewTransition.Metro(0.3.seconds), true)
                } else {
                    find<Popup>(mapOf(Popup::message to result.first))
                        .openModal(resizable = false)
                }
            }
        }
    }

    private fun login(name: String, pass: String): Pair<String, Boolean> {
        try {
            connection = SQL.tryConnection()
        } catch (e: Exception) {
            e.printStackTrace()
            return "A connection could not be established to database" to false
        }

        val statement = connection.createStatement()
        val result = statement.executeQuery(SQL.getMatchingRow(name, pass))
        return if (result.next()) {
            val email = result.getString("email")
            val username = result.getString("username")
            val password = result.getString("password")
            val phone = result.getString("phone")
            val id = result.getInt("user_id")

            account = Account(email, username, password, phone, id)
            "Login successful" to true
        } else {
            "Username or password incorrect" to false
        }
    }
}