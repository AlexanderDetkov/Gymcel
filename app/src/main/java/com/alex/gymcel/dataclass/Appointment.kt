package com.alex.gymcel.dataclass

data class Appointment(
    val date: String,
    val time: String,
    val cancelLink: String,
    val location: String
)