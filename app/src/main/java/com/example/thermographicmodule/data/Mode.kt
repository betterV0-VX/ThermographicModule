package com.example.thermographicmodule.data

data class ModeUiState(
    val waiting: Boolean = false,
    val binning: Boolean = false,
    val autoBinning: Boolean = false,
    val alc: Boolean = false,
    val alcBorder: Boolean = false,
    val fpsSlowdown: Boolean = false,
    val autoFpsSlowdown: Boolean = false,
    val polarityInversion: Boolean = false,
    val correction: Boolean = false
)