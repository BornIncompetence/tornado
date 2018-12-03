package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*

class ChangePassword : View("Change Password") {
    private val password = SimpleStringProperty("")
    private val newPassword = SimpleStringProperty("")
    private val retypePassword = SimpleStringProperty("")

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Change Password") {
                field("Old Password").passwordfield(password)
                field("New Password").passwordfield(newPassword)
                field("Retype New Password").passwordfield(retypePassword)
            }
            hbox {
                alignment = Pos.CENTER
                spacing = 20.0

                button("Confirm").action {
                    val result = changePassword()

                    find<Popup>(mapOf(Popup::message to result.first))
                        .openModal(resizable = false)

                    if (result.second) {
                        close()
                    }
                }
                button("Go back").action {
                    close()
                }
            }
        }
    }

    private fun changePassword(): Pair<String, Boolean> {
        val passwordStatement = connection.createStatement()
        val successStatement = connection.createStatement()

        val result = passwordStatement
            .executeQuery(SQL.getMatchingRow(account.username, password.value))

        when {
            newPassword.value != retypePassword.value -> {
                return "New passwords do not match" to false
            }
            account.username == "guest" -> {
                return "Access denied for guest account" to false
            }
            result.next() -> {
                successStatement.executeUpdate(
                    SQL.changePassword(
                        account.username,
                        newPassword.value
                    )
                )

                account.password = newPassword.value
                return "Successfully changed password" to true
            }
            else -> {
                return "FATAL ERROR: Could not add user" to false
            }
        }
    }
}