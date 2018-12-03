package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*

class ChangeUsername : View("Change Username") {
    private val username = SimpleStringProperty("")

    override val root = vbox {
        paddingAll = 20

        form().fieldset("Change Username").field("Username").textfield(username)
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0

            button("Update").action {
                val result = update()

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

    private fun update(): Pair<String, Boolean> {
        val usernameStatement = connection.createStatement()
        val usernameResult = usernameStatement
            .executeQuery(SQL.checkForExistingUsername(username.value))

        usernameResult.next()
        val usernameCount = usernameResult.getInt(1)

        if (usernameCount > 0) {
            return "Username taken" to false
        }

        if (account.username == "guest") {
            return "Access denied for guest account" to false
        }

        val successStatement = connection.createStatement()
        try {
            successStatement.executeUpdate(
                SQL.changeUsername(
                    account.username,
                    username.value
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return "FATAL ERROR: Could not update username" to false
        }

        return "Registration successful!" to true
    }
}