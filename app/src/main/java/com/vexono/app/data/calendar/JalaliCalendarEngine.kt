package com.vexono.app.data.calendar

import com.vexono.app.domain.model.GregorianDate
import com.vexono.app.domain.model.IslamicDate
import com.vexono.app.domain.model.JalaliDate
import java.util.Calendar
import java.util.TimeZone

object JalaliCalendarEngine {

    val PERSIAN_MONTH_NAMES = listOf(
        "فروردین", "اردیبهشت", "خرداد",
        "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر",
        "دی", "بهمن", "اسفند"
    )

    val GREGORIAN_MONTH_NAMES = listOf(
        "ژانویه", "فوریه", "مارس",
        "آوریل", "مه", "ژوئن",
        "ژوئیه", "اوت", "سپتامبر",
        "اکتبر", "نوامبر", "دسامبر"
    )

    val GREGORIAN_MONTH_NAMES_EN = listOf(
        "Jan", "Feb", "Mar",
        "Apr", "May", "Jun",
        "Jul", "Aug", "Sep",
        "Oct", "Nov", "Dec"
    )

    val ISLAMIC_MONTH_NAMES = listOf(
        "محرم", "صفر", "ربیع‌الاول",
        "ربیع‌الثانی", "جمادی‌الاول", "جمادی‌الثانی",
        "رجب", "شعبان", "رمضان",
        "شوال", "ذی‌القعده", "ذی‌الحجه"
    )

    val PERSIAN_WEEKDAY_NAMES = listOf(
        "شنبه", "یک‌شنبه", "دوشنبه",
        "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    val PERSIAN_WEEKDAY_NAMES_SHORT = listOf(
        "ش", "ی", "د", "س", "چ", "پ", "ج"
    )

    /**
     * Checks whether the specified Jalali year is a leap year.
     */
    fun isJalaliLeap(year: Int): Boolean {
        val g1 = jalaliToGregorian(JalaliDate(year, 1, 1))
        val g2 = jalaliToGregorian(JalaliDate(year + 1, 1, 1))
        val jdn1 = gregorianToJdn(g1.year, g1.month, g1.day)
        val jdn2 = gregorianToJdn(g2.year, g2.month, g2.day)
        return (jdn2 - jdn1) == 366L
    }

    fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day + (153 * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045L
    }

    fun isGregorianLeap(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Returns the number of days in a given Jalali month (1-12).
     */
    fun getDaysInJalaliMonth(year: Int, month: Int): Int {
        return when {
            month in 1..6 -> 31
            month in 7..11 -> 30
            month == 12 -> if (isJalaliLeap(year)) 30 else 29
            else -> throw IllegalArgumentException("Invalid month: $month. Month must be between 1 and 12.")
        }
    }

    /**
     * Converts Jalali Date directly to Gregorian Date.
     */
    fun jalaliToGregorian(jalaliDate: JalaliDate): GregorianDate {
        val jy = jalaliDate.year - 979
        val jm = jalaliDate.month - 1
        val jd = jalaliDate.day - 1

        var jDayNo = 365L * jy + (jy / 33) * 8 + ((jy % 33 + 3) / 4)
        for (i in 0 until jm) {
            jDayNo += if (i < 6) 31 else 30
        }
        jDayNo += jd

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524

            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        val gDaysInMonths = intArrayOf(
            31, if (isGregorianLeap(gy.toInt())) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )

        var gm = 0
        while (gm < 12 && gDayNo >= gDaysInMonths[gm]) {
            gDayNo -= gDaysInMonths[gm]
            gm++
        }

        return GregorianDate(gy.toInt(), gm + 1, (gDayNo + 1).toInt())
    }

    /**
     * Converts Gregorian Date directly to Jalali Date.
     */
    fun gregorianToJalali(gregorianDate: GregorianDate): JalaliDate {
        val gy = gregorianDate.year - 1600
        val gm = gregorianDate.month - 1
        val gd = gregorianDate.day - 1

        val gDaysInMonths = intArrayOf(
            31,
            if (isGregorianLeap(gregorianDate.year)) 29 else 28,
            31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        )

        var gDayNo = 365L * gy + ((gy + 3) / 4) - ((gy + 99) / 100) + ((gy + 399) / 400)
        for (i in 0 until gm) {
            gDayNo += gDaysInMonths[i]
        }
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        val jDaysInMonths = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        while (jm < 11 && jDayNo >= jDaysInMonths[jm]) {
            jDayNo -= jDaysInMonths[jm]
            jm++
        }

        return JalaliDate(jy.toInt(), jm + 1, (jDayNo + 1).toInt())
    }

    /**
     * Converts Jalali Date to Islamic (Hijri) Date.
     */
    fun jalaliToIslamic(jalaliDate: JalaliDate): IslamicDate {
        val gDate = jalaliToGregorian(jalaliDate)
        val a = (14 - gDate.month) / 12
        val y = gDate.year + 4800 - a
        val m = gDate.month + 12 * a - 3
        val jdn = gDate.day + (153 * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045L

        val l = jdn - 1948440L + 10632L
        val n = (l - 1) / 10631L
        val l1 = l - 10631L * n + 354
        val j = ((10985L - l1) / 5316L) * ((50L * l1) / 17719L) + (l1 / 5670L) * ((43L * l1) / 15238L)
        val l2 = l1 - ((30L - j) / 15L) * ((17719L * j) / 50L) - (j / 16L) * ((15238L * j) / 43L) + 29
        val iMonth = ((24L * l2) / 709L).toInt()
        val iDay = (l2 - (709L * iMonth) / 24L).toInt()
        val iYear = (30L * n + j - 30L).toInt()
        return IslamicDate(iYear, iMonth, iDay)
    }

    /**
     * Returns the day of week index where 0 = Saturday (شنبه) to 6 = Friday (جمعه).
     */
    fun getDayOfWeek(jalaliDate: JalaliDate): Int {
        val gDate = jalaliToGregorian(jalaliDate)
        val cal = Calendar.getInstance()
        cal.set(gDate.year, gDate.month - 1, gDate.day)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> 0
            Calendar.SUNDAY -> 1
            Calendar.MONDAY -> 2
            Calendar.TUESDAY -> 3
            Calendar.WEDNESDAY -> 4
            Calendar.THURSDAY -> 5
            Calendar.FRIDAY -> 6
            else -> 0
        }
    }

    /**
     * Returns today's Jalali Date considering device's local timezone.
     */
    fun getTodayJalali(timeZone: TimeZone = TimeZone.getDefault()): JalaliDate {
        val calendar = Calendar.getInstance(timeZone)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return gregorianToJalali(GregorianDate(year, month, day))
    }

    /**
     * Returns today's Gregorian Date.
     */
    fun getTodayGregorian(timeZone: TimeZone = TimeZone.getDefault()): GregorianDate {
        val calendar = Calendar.getInstance(timeZone)
        return GregorianDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Converts English numbers in a string to Persian digits (e.g., 1403 -> ۱۴۰۳).
     */
    fun toPersianDigits(number: Any): String {
        val str = number.toString()
        val builder = StringBuilder()
        for (ch in str) {
            when (ch) {
                '0' -> builder.append('۰')
                '1' -> builder.append('۱')
                '2' -> builder.append('۲')
                '3' -> builder.append('۳')
                '4' -> builder.append('۴')
                '5' -> builder.append('۵')
                '6' -> builder.append('۶')
                '7' -> builder.append('۷')
                '8' -> builder.append('۸')
                '9' -> builder.append('۹')
                else -> builder.append(ch)
            }
        }
        return builder.toString()
    }

    /**
     * Returns full formatted Persian date string, e.g. "چهارشنبه ۱۵ اسفند ۱۴۰۳"
     */
    fun getFullPersianDateString(jalaliDate: JalaliDate): String {
        val dayOfWeekIndex = getDayOfWeek(jalaliDate)
        val dayName = PERSIAN_WEEKDAY_NAMES.getOrElse(dayOfWeekIndex) { "" }
        val monthName = PERSIAN_MONTH_NAMES.getOrElse(jalaliDate.month - 1) { "" }
        return "$dayName ${toPersianDigits(jalaliDate.day)} $monthName ${toPersianDigits(jalaliDate.year)}"
    }

    /**
     * Returns formatted Gregorian companion string, e.g. "5 March 2025"
     */
    fun getFullGregorianDateString(gregorianDate: GregorianDate): String {
        val monthName = GREGORIAN_MONTH_NAMES_EN.getOrElse(gregorianDate.month - 1) { "" }
        return "${gregorianDate.day} $monthName ${gregorianDate.year}"
    }

    /**
     * Returns formatted Islamic date string, e.g. "۵ رمضان ۱۴۴۶"
     */
    fun getFullIslamicDateString(islamicDate: IslamicDate): String {
        val monthName = ISLAMIC_MONTH_NAMES.getOrElse(islamicDate.month - 1) { "" }
        return "${toPersianDigits(islamicDate.day)} $monthName ${toPersianDigits(islamicDate.year)}"
    }
}
