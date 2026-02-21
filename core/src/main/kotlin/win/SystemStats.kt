package org.example.win

data class SystemStats(
    val cpu: Int = 0,
    val ram: Int = 0,
    val battery: Int = 0,
    val time: String = "",
    val date: String = "",
    val temp: String = "N/A"
)
