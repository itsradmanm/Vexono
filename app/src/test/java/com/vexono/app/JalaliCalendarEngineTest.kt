package com.vexono.app

import com.vexono.app.data.calendar.JalaliCalendarEngine
import com.vexono.app.domain.model.GregorianDate
import com.vexono.app.domain.model.JalaliDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JalaliCalendarEngineTest {

    @Test
    fun testJalaliToGregorianConversion() {
        // Test 1: 1404/01/01 -> 2025-03-21
        val g1 = JalaliCalendarEngine.jalaliToGregorian(JalaliDate(1404, 1, 1))
        assertEquals(GregorianDate(2025, 3, 21), g1)

        // Test 2: 1403/12/30 (Leap Year) -> 2025-03-20
        val g2 = JalaliCalendarEngine.jalaliToGregorian(JalaliDate(1403, 12, 30))
        assertEquals(GregorianDate(2025, 3, 20), g2)

        // Test 3: 1399/01/01 -> 2020-03-20
        val g3 = JalaliCalendarEngine.jalaliToGregorian(JalaliDate(1399, 1, 1))
        assertEquals(GregorianDate(2020, 3, 20), g3)

        // Test 4: 1370/01/01 -> 1991-03-21
        val g4 = JalaliCalendarEngine.jalaliToGregorian(JalaliDate(1370, 1, 1))
        assertEquals(GregorianDate(1991, 3, 21), g4)
    }

    @Test
    fun testGregorianToJalaliConversion() {
        // Test 1: 2025-03-21 -> 1404/01/01
        val j1 = JalaliCalendarEngine.gregorianToJalali(GregorianDate(2025, 3, 21))
        assertEquals(JalaliDate(1404, 1, 1), j1)

        // Test 2: 2025-03-20 -> 1403/12/30
        val j2 = JalaliCalendarEngine.gregorianToJalali(GregorianDate(2025, 3, 20))
        assertEquals(JalaliDate(1403, 12, 30), j2)

        // Test 3: 2020-03-20 -> 1399/01/01
        val j3 = JalaliCalendarEngine.gregorianToJalali(GregorianDate(2020, 3, 20))
        assertEquals(JalaliDate(1399, 1, 1), j3)
    }

    @Test
    fun testLeapYears() {
        // Known Persian Leap Years
        assertTrue(JalaliCalendarEngine.isJalaliLeap(1395))
        assertTrue(JalaliCalendarEngine.isJalaliLeap(1399))
        assertTrue(JalaliCalendarEngine.isJalaliLeap(1403))
        assertTrue(JalaliCalendarEngine.isJalaliLeap(1408))

        // Known Non-Leap Years
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1396))
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1397))
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1398))
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1400))
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1401))
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1402))
        assertFalse(JalaliCalendarEngine.isJalaliLeap(1404))
    }

    @Test
    fun testDaysInMonths() {
        // Months 1 to 6 should have 31 days
        for (m in 1..6) {
            assertEquals(31, JalaliCalendarEngine.getDaysInJalaliMonth(1403, m))
        }

        // Months 7 to 11 should have 30 days
        for (m in 7..11) {
            assertEquals(30, JalaliCalendarEngine.getDaysInJalaliMonth(1403, m))
        }

        // Month 12 (Esfand)
        // 1403 is a leap year -> 30 days
        assertEquals(30, JalaliCalendarEngine.getDaysInJalaliMonth(1403, 12))
        // 1404 is not a leap year -> 29 days
        assertEquals(29, JalaliCalendarEngine.getDaysInJalaliMonth(1404, 12))
    }

    @Test
    fun testPersianDigitsFormatting() {
        assertEquals("۱۴۰۳", JalaliCalendarEngine.toPersianDigits(1403))
        assertEquals("۰۵:۳۰", JalaliCalendarEngine.toPersianDigits("05:30"))
        assertEquals("۲۹ اسفند", JalaliCalendarEngine.toPersianDigits("29 اسفند"))
    }

    @Test
    fun testDayOfWeekCalculation() {
        // 1403/01/01 was Wednesday (چهارشنبه -> index 4)
        // Saturday=0, Sunday=1, Monday=2, Tuesday=3, Wednesday=4, Thursday=5, Friday=6
        val dow = JalaliCalendarEngine.getDayOfWeek(JalaliDate(1403, 1, 1))
        assertEquals(4, dow) // Wednesday
        assertEquals("چهارشنبه", JalaliCalendarEngine.PERSIAN_WEEKDAY_NAMES[dow])
    }

    @Test
    fun testBiDirectionalRoundTrip() {
        // Test 100 consecutive years from 1350 to 1450
        for (year in 1350..1450) {
            for (month in 1..12) {
                val daysInMonth = JalaliCalendarEngine.getDaysInJalaliMonth(year, month)
                val sampleDays = listOf(1, 15, daysInMonth)
                for (day in sampleDays) {
                    val original = JalaliDate(year, month, day)
                    val gDate = JalaliCalendarEngine.jalaliToGregorian(original)
                    val roundTrip = JalaliCalendarEngine.gregorianToJalali(gDate)
                    assertEquals("Mismatch on date: $original", original, roundTrip)
                }
            }
        }
    }
}
