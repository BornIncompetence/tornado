package com.greer.tornado

import javafx.geometry.Pos
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class Welcome : View("Welcome") {
    private val ctrlAccount: AccountController by inject()
    private var accountModel = AccountModel(ctrlAccount.account)
    private val ctrlImport: ImportController by inject()

    override val root = borderpane {
        top = menubar {
            useMaxWidth = true

            menu("Account") {
                item("Create Account").action {
                    find<CreateAccount>().openModal()
                }
                item("Change Username").action {
                    find<ChangeUsername>().openModal()
                }
                item("Change Password").action {
                    find<ChangePassword>().openModal()
                }
                item("Modify Account").action {
                    find<ModifyAccount>().openModal()
                }
            }
            menu("Appointment") {
                item("Create Appointment").action {
                    find<CreateAppointment>().openModal()
                }
                item("Change / Cancel Appointment").action {
                    find<ChangeCancelAppointment>().openModal()
                }
            }
            menu("Settings") {
                menu("Set calendar type") {
                    togglegroup {
                        radiomenuitem("Week", this).isSelected = true
                        radiomenuitem("Month", this)
                        radiomenuitem("Day", this)
                    }
                }
                item("Import schedule").action {
                    var file: File? = null
                    val result: Pair<String, Boolean> = try {
                        file = open()
                        if (file != null) {
                            try {
                                ctrlImport.read(file)
                                "" to true
                            } catch (e: ImportController.ProjectException) {
                                find<Popup>(mapOf(Popup::message to e.message))
                                    .openModal(resizable = false)
                                "There was an error opening the csv file, make sure it's a valid database file." to false
                            }
                        } else {
                            "No file was selected." to false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "FATAL ERROR: Something went wrong." to false
                    }

                    if (result.second) {
                        find<FileExplorer>(
                            mapOf(
                                FileExplorer::file to file
                            )
                        ).openModal()
                    } else if (result.first != "No file was selected") {
                        find<Popup>(mapOf(Popup::message to result.first))
                            .openModal(resizable = false)
                    }

                }
                item("Export schedule").action {
                    val file: File?
                    val result: Pair<String, Boolean> = try {
                        file = save()
                        if (file != null) {
                            try {
                                export(file)
                                "" to true
                            } catch (e: ImportController.ProjectException) {
                                find<Popup>(mapOf(Popup::message to e.message))
                                    .openModal(resizable = false)
                                "There was an error opening the csv file, make sure it's a valid database file." to false
                            }
                        } else {
                            "No file was selected." to false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "FATAL ERROR: Something went wrong." to false
                    }

                    if (result.second) {

                    }
                }
            }
            menu("Help")
        }
        center = hbox {
            paddingAll = 20.0
            alignment = Pos.CENTER

            label("Logged in as ")
            label(accountModel.username)
        }
    }

    private fun open(): File? {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("CSV file(*.csv)", "*.csv")
        )
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        fileChooser.initialFileName = "test.csv"

        return fileChooser.showOpenDialog(null)
    }

    private fun save(): File? {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("CSV file(*.csv)", "*.csv")
        )
        fileChooser.initialDirectory = File(System.getProperty("user.home"))
        fileChooser.initialFileName = "test.csv"

        return fileChooser.showSaveDialog(null)
    }

    private fun export(file: File) {
        try {
            val exportStatement = connection.createStatement()
            val exportResult = exportStatement.executeQuery(SQL.getAppointments(ctrlAccount.account.username))
            file.printWriter().use { out ->
                while (exportResult.next()) {
                    val title = exportResult.getString("title")
                    val start = exportResult.getString("start_date")
                    val end = exportResult.getString("end_date")
                    val id = exportResult.getString("appointment_id")

                    out.println("$title,$start,$end,$id")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}