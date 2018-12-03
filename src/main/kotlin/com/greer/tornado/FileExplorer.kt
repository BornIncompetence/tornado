package com.greer.tornado

import java.io.File
import kotlin.random.Random

object FileExplorer {
    fun overwrite(appointments: MutableList<Appointment>) {
        appointments.forEach {
            val idStatement = connection.createStatement()
            val idResult = idStatement.executeQuery(SQL.checkForExistingAppt(it.id))

            // Modify if the ID already exists, else create a new one with a newly hashed ID
            if (idResult.next()) {
                val titleStatement = connection.createStatement()
                val startStatement = connection.createStatement()
                val endStatement = connection.createStatement()

                titleStatement.executeUpdate(SQL.changeTitle(it.title, it.id))
                startStatement.executeUpdate(SQL.changeStart(it.startDate, it.id))
                endStatement.executeUpdate(SQL.changeEnd(it.endDate, it.id))
            } else {
                val creationStatement = connection.createStatement()
                val aptID = it.title.hashCode() + (account.id + Random.nextInt(100))
                creationStatement.executeUpdate(
                    SQL.createAppointment(
                        it.title,
                        it.startDate,
                        it.endDate,
                        account.id,
                        aptID
                    )
                )
            }
        }
    }

    fun save(file: File) {
        try {
            val successGetAptStatement = connection.createStatement()
            val aptResult = successGetAptStatement.executeQuery(SQL.getAppointments(account.username))
            file.printWriter().use { out ->
                while (aptResult.next()) {
                    val title = aptResult.getString("title")
                    val start = aptResult.getString("start_date")
                    val end = aptResult.getString("end_date")
                    val id = aptResult.getInt("appointment_id")

                    out.println("$title,$start,$end,$id")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun open(file: File): MutableList<Appointment> {
        val appointments = mutableListOf<Appointment>()

        file.forEachLine { line ->
            val tokens = line.split(",")
            appointments.add(Appointment(tokens[0], tokens[1], tokens[2], tokens[3].toInt()))
        }

        return appointments
    }
}