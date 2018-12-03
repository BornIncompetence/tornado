package com.greer.tornado

import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

class Welcome : View("Welcome") {
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
                    val result = try {
                        val file = open()
                        if (file != null) {
                            val appointments = FileExplorer.open(file)
                            FileExplorer.overwrite(appointments)
                            "Appointments updated!"
                        } else {
                            "File not found!"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "There was an error opening the csv file, make sure it's a valid database file."
                    }

                    find<Popup>(mapOf(Popup::message to result))
                        .openModal(resizable = false)
                }
                item("Export schedule")
            }
            menu("Help")
        }
        center = label("Logged in as username") {
            paddingAll = 20.0
        }
    }

    private fun open(): File? {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(
            FileChooser.ExtensionFilter("CSV file(*.csv)", "*.csv")
        )

        return fileChooser.showOpenDialog(null)
    }
}