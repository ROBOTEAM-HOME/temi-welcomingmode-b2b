package com.robotemi.welcomingbtob.settings

data class SingleSelectionModel(
    var name: String,
    var description: String,
    var selected: Boolean = false
)