package com.lmt.global.base.model

enum class Premium(val type: Int, val id: String) {
    Weekly(1, "weekly"),
    Monthly(2, "monthly"),
    Yearly(3, "yearly");

    fun isWeekly() = this == Weekly
    fun isMonthly() = this == Monthly
    fun isYearly() = this == Yearly
}
