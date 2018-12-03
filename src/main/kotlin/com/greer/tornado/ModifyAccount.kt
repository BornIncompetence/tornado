package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*

class ModifyAccount : View() {
    private val email = SimpleStringProperty("")
    private val phone = SimpleStringProperty("")

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Modify Account") {
                field("New User Email").textfield(email)
                field("Phone Number").textfield(phone)
            }
        }
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0

            button("Modify").action {
                val result = modify()

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

    private fun modify(): Pair<String, Boolean> {
        val emailStatement = connection.createStatement()
        val emailResult = emailStatement.executeQuery(SQL.checkForExistingEmail(email.value))
        emailResult.next()
        val emailCount = emailResult.getInt(1)

        // No collisions with other email addresses exist
        if (emailCount > 0) {
            return "This email is already registered" to false
        }

        if (email.value != "") {
            val successEmailStatement = connection.createStatement()
            successEmailStatement.executeUpdate(
                SQL.changeEmail(
                    account.username,
                    email.value
                )
            )
            account.email = email.value
        }

        // The same phone number can apply to other users however
        if (phone.value != "") {
            val successPhoneStatement = connection.createStatement()
            successPhoneStatement.executeUpdate(
                SQL.changePhoneNumber(
                    account.username,
                    phone.value
                )
            )
            account.phone = phone.value
        }

        return "Value(s) successfully updated" to true
    }
}