package com.greer.tornado

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class Login : View("Login") {
    private val ctrl: AccountController by inject()
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
        return ctrl.useAccount(name, pass)
    }
}