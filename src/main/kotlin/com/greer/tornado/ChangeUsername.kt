package com.greer.tornado

import javafx.geometry.Pos
import tornadofx.*

class ChangeUsername : View("Change Username") {
    private val ctrlAccount: AccountController by inject()
    private var accountModel = AccountModel(ctrlAccount.account)

    override val root = vbox {
        paddingAll = 20

        form().fieldset("Change Username").field("Username").textfield(accountModel.username)
        hbox {
            alignment = Pos.CENTER
            spacing = 20.0

            button("Update").action {
                val result = update()

                find<Popup>(mapOf(Popup::message to result.first))
                    .openModal(resizable = false)

                if (result.second) {
                    accountModel.commit()
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
            .executeQuery(SQL.checkForExistingUsername(accountModel.username.value))

        usernameResult.next()
        val usernameCount = usernameResult.getInt(1)

        if (usernameCount > 0) {
            return "Username taken" to false
        }

        if (accountModel.username.value == "guest") {
            return "Access denied for guest account" to false
        }

        val successStatement = connection.createStatement()
        try {
            successStatement.executeUpdate(
                SQL.changeUsername(
                    accountModel.username.value,
                    accountModel.id.value.toInt()
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return "FATAL ERROR: Could not update username" to false
        }

        return "Registration successful!" to true
    }
}