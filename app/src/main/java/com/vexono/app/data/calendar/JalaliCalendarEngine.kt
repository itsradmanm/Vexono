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
        val epbase = year - if (year >= 0) 474 else 473
        val epyear = 474 + (epbase % 2820 + 2820) % 2820
        return ((epyear + 38) * 682) % 2816 < 682
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
     * Converts Jalali Date to Julian Day Number (JDN).
     */
    fun jalaliToJdn(year: Int, month: Int, day: Int): Long {
        val epbase = year - if (year >= 0) 474 else 473
        val epyear = 474 + (epbase % 2820 + 2820) % 2820
        val md = if (month <= 7) (month - 1) * 31 else (month - 1) * 30 + 6
        return day + md + (epyear * 682 - 110) / 2816 + (epyear - 1) * 365 + (epbase / 2820) * 1029983L + 1948320L
    }

    /**
     * Converts Julian Day Number (JDN) to Jalali Date.
     */
    fun jdnToJalali(jdn: Long): JalaliDate {
        val dep = jdn - jalaliToJdn(475, 1, 1)
        val ccycle = dep / 1029983L
        val cday = (dep % 1029983L + 1029983L) % 1029983L
        val ycycle = if (cday == 1029982L) 2820L else ((cday * 2816L + 1031337L) / 1029983L)
        val year = (475 + ccycle * 2820 + ycycle).toInt()
        val yday = jdn - jalaliToJdn(year, 1, 1) + 1
        val month = if (yday <= 186) {
            Math.ceil(yday.toDouble() / 31.0).toInt()
        } else {
            Math.ceil((yday - 6).toDouble() / 30.0).toInt()
        }
        val day = (jdn - jalaliToJdn(year, month, 1) + 1).toInt()
        return JalaliDate(year, month, day)
    }

    /**
     * Converts Gregorian Date to Julian Day Number (JDN).
     */
    fun gregorianToJdn(year: Int, month: Int, day: Int): Long {
        val a = (14 - month) / 12
        val y = year + 4800 - a
        val m = month + 12 * a - 3
        return day + (153 * m + 2) / 5 + 365L * y + y / 4 - y / 100 + y / 400 - 32045L
    }

    /**
     * Converts Julian Day Number (JDN) to Gregorian Date.
     */
    fun jdnToGregorian(jdn: Long): GregorianDate {
        val a = jdn + 32044L
        val b = (4 * a + 3) / 146097L
        val c = a - (146097L * b) / 4L
        val d = (4 * c + 3) / 1461L
        val e = c - (1461L * d) / 4L
        val m = (5 * e + 2) / 153L
        val day = (e - (153 * m + 2) / 5 + 1).toInt()
        val month = (m + 3 - 12 * (m / 10)).toInt()
        val year = (100 * b + d - 4800 + (m / 10)).toInt()
        return GregorianDate(year, month, day)
    }

    /**
     * Converts Julian Day Number (JDN) to Tabular Islamic (Hijri) Date.
     */
    fun jdnToIslamic(jdn: Long): IslamicDate {
        val l = jdn - 1948440L + 10632L
        val n = (l - 1) / 10631L
        val l1 = l - 10631L * n + 354
        val j = ((10985L - l1) / 5316L) * ((50L * l1) / 17719L) + (l1 / 5670L) * ((43L * l1) / 15238L)
        val l2 = l1 - ((30L - j) / 15L) * ((17719L * j) / 50L) - (j / 16L) * ((15238L * j) / 43L) + 29
        val month = ((24L * l2) / 709L).toInt()
        val day = (l2 - (709L * month) / 24L).toInt()
        val year = (30L * n + j - 30L).toInt()
        return IslamicDate(year, month, day)
    }

    /**
     * Converts Jalali Date directly to Gregorian Date.
     */
    fun jalaliToGregorian(jalaliDate: JalaliDate): GregorianDate {
        val jdn = jalaliToJdn(jalaliDate.year, jalaliDate.month, jalaliDate.day)
        return jdnToGregorian(jdn)
    }

    /**
     * Converts Gregorian Date directly to Jalali Date.
     */
    fun gregorianToJalali(gregorianDate: GregorianDate): JalaliDate {
        val jdn = gregorianToJdn(gregorianDate.year, gregorianDate.month, gregorianDate.day)
        return jdnToJalali(jdn)
    }

    /**
     * Converts Jalali Date to Islamic (Hijri) Date.
     */
    fun jalaliToIslamic(jalaliDate: JalaliDate): IslamicDate {
        val jdn = jalaliToJdn(jalaliDate.year, jalaliDate.month, jalaliDate.day)
        return jdnToIslamic(jdn)
    }

    /**
     * Returns the day of week index where 0 = Saturday (شنبه) to 6 = Friday (جمعه).
     */
    fun getDayOfWeek(jalaliDate: JalaliDate): Int {
        val jdn = jalaliToJdn(jalaliDate.year, jalaliDate.month, jalaliDate.day)
        // JDN modulo 7 gives: 0=Mon, 1=Tue, 2=Wed, 3=Thu, 4=Fri, 5=Sat, 6=Sun
        val standardDay = ((jdn + 1) % 7).toInt() // 0=Sunday, 1=Monday, ..., 6=Saturday
        // We want Saturday to be 0:
        return (standardDay + 1) % 7
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
