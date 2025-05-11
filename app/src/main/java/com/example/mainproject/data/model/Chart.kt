package com.example.mainproject.data.model

data class ChartData(
    val daily: TimeSeriesData? = null,
    val weekly: TimeSeriesData? = null,
    val monthly: TimeSeriesData? = null,
    val year: TimeSeriesData? = null
)

data class TimeSeriesData(
    val income: List<Double> = emptyList(),
    val expense: List<Double> = emptyList(),
    val labels: List<String> = emptyList()
)