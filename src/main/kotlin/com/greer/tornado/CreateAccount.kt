package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*

class CreateAccount : View("Create Account") {
    private val email = SimpleStringProperty("")
    private val username = SimpleStringProperty("")
    private val password = SimpleStringProperty("")
    private val retypePassword = SimpleStringProperty("")

    private val regex = Regex("""\w+@\w+.\w+""")

    override val root = vbox {
        paddingAll = 20

        form().fieldset("Create Account") {
            field("Email").textfield(email)
            field("Username").textfield(username)
            field("Password").passwordfield(password)
            field("Retype Password").passwordfield(retypePassword)
        }
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0

            button("Register").action {
                val result = register()

                find<Popup>(mapOf(Popup::message to result.first))
                    .openModal(resizable = false)

                if (result.second) {
                    //TODO: Update appointments
                    close()
                }
            }
            button("Go back").action {
                close()
            }
        }
    }

    private fun register(): Pair<String, Boolean> {
        if (password.value != retypePassword.value) {
            return "The passwords do not match!" to false
        }

        if (!email.value.contains(regex)) {
            return "This is an invalid email address" to false
        }

        val usernameStatement = connection.createStatement()
        val usernameResult = usernameStatement
            .executeQuery(SQL.checkForExistingUsername(username.value))

        usernameResult.next()
        val usernameCount = usernameResult.getInt(1)
        if (usernameCount > 0) {
            return "This username has already been taken." to false
        }

        val emailStatement = connection.createStatement()
        val emailResult = emailStatement
            .executeQuery(SQL.checkForExistingEmail(email.value))

        emailResult.next()
        val emailCount = emailResult.getInt(1)
        if (emailCount > 0) {
            return "This email is already registered" to false
        }

        val userIDStatement = connection.createStatement()
        val userIDResult = userIDStatement.executeQuery(SQL.getMaxID())
        userIDResult.next()
        val maxID = userIDResult.getInt(1)

        val successStatement = connection.createStatement()
        val newAccount = Account(email.value, username.value, password.value, null, maxID + 1)
        try {
            successStatement.executeUpdate(SQL.createAccount(account))
            account = newAccount
        } catch (e: Exception) {
            e.printStackTrace()
            return "FATAL ERROR: Could not register user" to false
        }

        return "Registration Complete" to true
    }
}