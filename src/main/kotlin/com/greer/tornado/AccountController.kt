package com.greer.tornado

import tornadofx.Controller

// Controller for logged-in user
class AccountController : Controller() {
    lateinit var account: Account

    fun useAccount(name: String, pass: String): Pair<String, Boolean> {
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