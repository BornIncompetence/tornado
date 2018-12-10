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
                (if (account.phone == null) "NULL" else "'${account.phone}'") +
                ");"
    }

    fun changeUsername(user: String, id: Int): String {
        return "UPDATE Users SET username = '$user' WHERE user_id = $id;"
    }

    fun changePassword(pass: String, id: Int): String {
        return "UPDATE Users SET password = '$pass' WHERE user_id = $id;"
    }

    fun changeEmail(email: String, id: Int): String {
        return "UPDATE Users SET email = '$email' WHERE user_id = $id;"
    }

    fun changePhoneNumber(phone: String, id: Int): String {
        return "UPDATE Users SET phone = '$phone' WHERE user_id = $id;"
    }

    fun createAppointment(name: String, startDate: String, endDate: String, userID: Int, appID: Int, reminder: Int?): String {
        return "INSERT INTO scheduler.appointments(appointment_id, user_id , title, start_date, end_date, reminder) VALUES(\n" +
                "'$appID', '$userID', '$name' , '$startDate', '$endDate', ${reminder ?: "NULL"}) "
    }

    fun getAppointments(username: String): String {
        return "SELECT a.title, a.start_date, a.end_date, a.appointment_id, a.reminder " +
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

    fun changeReminder(reminder: Int?, appID: Int): String {
        return "UPDATE Appointments SET reminder = ${reminder ?: "NULL"} WHERE appointment_id = $appID;"
    }

    fun removeAppointment(appID: Int): String {
        return "DELETE FROM Appointments WHERE appointment_id = $appID;"
    }

    fun checkForExistingAppt(appID: Int): String {
        return "SELECT COUNT(*) FROM Appointments WHERE appointment_id = $appID;"
    }
}
