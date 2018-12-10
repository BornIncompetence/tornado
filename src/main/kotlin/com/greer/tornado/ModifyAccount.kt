package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import tornadofx.*

class ModifyAccount : View() {
    private val ctrlAccount: AccountController by inject()
    private var accountModel = AccountModel(ctrlAccount.account)
    // private val email = SimpleStringProperty(account.email)
    // private val phone = SimpleStringProperty(account.phone)

    override val root = vbox {
        paddingAll = 20

        form {
            fieldset("Modify Account") {
                field("New User Email").textfield(accountModel.email)
                field("Phone Number").textfield(accountModel.phone)
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
        val emailResult = emailStatement.executeQuery(SQL.checkForExistingEmail(accountModel.email.value))
        emailResult.next()
        val emailCount = emailResult.getInt(1)

        // No collisions with other email addresses exist
        if (emailCount > 0) {
            return "This email is already registered" to false
        }

        if (accountModel.email.value != "") {
            val successEmailStatement = connection.createStatement()
            successEmailStatement.executeUpdate(
                SQL.changeEmail(
                    accountModel.username.value,
                    accountModel.id.value.toInt()
                )
            )
        }

        // The same phone number can apply to other users however
        if (accountModel.phone.value != "") {
            val successPhoneStatement = connection.createStatement()
            successPhoneStatement.executeUpdate(
                SQL.changePhoneNumber(
                    accountModel.phone.value,
                    accountModel.id.value.toInt()
                )
            )
        }

        accountModel.commit()

        return "Value(s) successfully updated" to true
    }
}