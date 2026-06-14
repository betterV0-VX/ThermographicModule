package com.example.thermographicmodule.data

import androidx.annotation.DrawableRes


data class SectionCardData(
    val sectionName: String,
    @DrawableRes val sectionIconResId: Int,
    val sectionType: SectionType,
    val sectionDescription: String? = null,
)

enum class SectionType {
    REQUEST,
    ROTATION,
    ANALYSIS_AREA,
    ZOOM_AREA,
    USER_PARAMETER
}

data class SectionIsChosen(
    val requestIsChosen: Boolean = true,
    val rotationIsChosen: Boolean = false,
    val analysisAreaIsChosen: Boolean = false,
    val zoomAreaIsChosen: Boolean = false,
    val userParameterIsChosen: Boolean = false,
)