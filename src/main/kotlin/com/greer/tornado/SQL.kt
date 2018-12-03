package com.greer.tornado

import com.mysql.cj.jdbc.MysqlDataSource
import java.sql.Connection

object SQL {
    fun tryConnection(): Connection {
        val dataSource = MysqlDataSource()
        dataSource.user = "java"
        dataSource.setPassword("coffee")
        dataSource.serverName = "localhost"
        dataSource.databaseName = "scheduler"
        dataSource.port = 3306
        dataSource.serverTimezone = "UTC"
        dataSource.useSSL = false

        return dataSource.connection
    }

    fun getMatchingRow(name: String, pass: String): String {
        return "SELECT * FROM Users WHERE username LIKE '$name' AND password = '$pass';"
    }

    fun getMaxID(): String {
        return "SELECT MAX(user_id) FROM Users;"
    }

    fun checkForExistingUsername(name: String): String {
        return "SELECT COUNT(*) FROM Users WHERE username = '$name';"
    }

    fun checkForExistingEmail(email: String): String {
        return "SELECT COUNT(*) FROM Users WHERE email = '$email';"
    }

    fun createAccount(account: Account): String {
        return "INSERT INTO scheduler.users (user_id, username, password, email, phone) VALUES(\n" +
                "${account.id}, " +
                "'${account.username}', " +
                "'${account.password}', " +
                "'${account.email}', " +
                "${if (account.phone == null) "NULL" else account.phone}" +
                ");"
    }

    fun changeUsername(old: String, new: String): String {
        return "UPDATE Users SET username = '$new' WHERE username = '$old';"
    }

    fun changePassword(user: String, pass: String): String {
        return "UPDATE Users SET password = '$pass' WHERE username = '$user';"
    }

    fun changeEmail(name: String, address: String): String {
        return "UPDATE Users SET email = '$address' WHERE username = '$name';"
    }

    fun changePhoneNumber(name: String, number: String): String {
        return "UPDATE Users SET phone = '$number' WHERE username = '$name';"
    }

    fun createAppointment(name: String, startDate: String, endDate: String, userID: Int, appID: Int): String {
        return "INSERT INTO scheduler.appointments(appointment_id, user_id , title, start_date, end_date) VALUES(\n" +
                "'$appID', '$userID', '$name' , '$startDate', '$endDate') "
    }

    fun getAppointments(username: String): String {
        return "SELECT a.title, a.start_date, a.end_date, a.appointment_id " +
                "FROM Appointments a " +
                "INNER JOIN Users u ON a.user_id = u.user_id " +
                "WHERE username LIKE '$username';"
    }

    fun changeTitle(title: String, appID: Int): String {
        return "UPDATE Appointments SET title = '$title' WHERE appointment_id = $appID;"
    }

    fun changeStart(startDate: String, appID: Int): String {
        return "UPDATE Appointments SET start_date = '$startDate' WHERE appointment_id = $appID;"
    }

    fun changeEnd(endDate: String, appID: Int): String {
        return "UPDATE Appointments SET end_date = '$endDate' WHERE appointment_id = $appID;"
    }

    fun removeAppointment(appID: Int): String {
        return "DELETE FROM Appointments WHERE appointment_id = $appID;"
    }

    fun checkForExistingAppt(appID: Int): String {
        return "SELECT COUNT(*) FROM Appointments WHERE appointment_id = $appID;"
    }
}
