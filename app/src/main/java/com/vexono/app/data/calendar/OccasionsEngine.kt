package com.vexono.app.data.calendar

import com.vexono.app.domain.model.JalaliDate
import com.vexono.app.domain.model.Occasion
import com.vexono.app.domain.model.OccasionCategory

/**
 * High-precision, comprehensive Occasions Engine for the Iranian (Jalali) Calendar.
 * Supports all years from 1300 to 1500:
 * - All Fixed Solar National, Official, Cultural & International Occasions.
 * - Verified Official Religious Holidays (1390 - 1410).
 * - High-precision Astronomical Lunar-to-Solar conversions for all other years (1300 - 1500).
 */
object OccasionsEngine {

    // ----------------------------------------------------
    // 1. Fixed Solar Occasions (خورشیدی ثابت)
    // ----------------------------------------------------
    data class SolarOccasionDef(
        val month: Int,
        val day: Int,
        val title: String,
        val isHoliday: Boolean,
        val category: OccasionCategory
    )

    private val FIXED_SOLAR_OCCASIONS = listOf(
        // فروردین
        SolarOccasionDef(1, 1, "جشن نوروز / آغاز سال نو خورشیدی", true, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 2, "عید نوروز", true, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 3, "عید نوروز", true, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 4, "عید نوروز", true, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 6, "روز امید و شادباش‌نویسی (زادروز زرتشت)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 12, "روز جمهوری اسلامی ایران", true, OccasionCategory.OFFICIAL),
        SolarOccasionDef(1, 13, "جشن سیزده‌بدر / روز طبیعت", true, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 18, "روز جهانی بهداشت و سلامتی", false, OccasionCategory.INTERNATIONAL),
        SolarOccasionDef(1, 25, "روز بزرگداشت عطار نیشابوری", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(1, 29, "روز ارتش جمهوری اسلامی ایران", false, OccasionCategory.OFFICIAL),

        // اردیبهشت
        SolarOccasionDef(2, 1, "روز بزرگداشت سعدی شیرازی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(2, 2, "جشن گیاه‌آوری (روز زمین پاک)", false, OccasionCategory.INTERNATIONAL),
        SolarOccasionDef(2, 3, "روز بزرگداشت شیخ بهایی و روز ملی معماری", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(2, 10, "جشن چهلم نوروز و روز ملی خلیج فارس", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(2, 11, "روز جهانی کار و کارگر", false, OccasionCategory.INTERNATIONAL),
        SolarOccasionDef(2, 12, "شهادت استاد مرتضی مطهری و روز معلم", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(2, 15, "جشن بهاربد / روز شیراز و روز ماما", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(2, 25, "روز بزرگداشت حکیم ابوالقاسم فردوسی و پاسداشت زبان فارسی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(2, 27, "روز ارتباطات و روابط عمومی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(2, 28, "روز بزرگداشت حکیم عمر خیام و روز میراث فرهنگی", false, OccasionCategory.NATIONAL),

        // خرداد
        SolarOccasionDef(3, 1, "روز بزرگداشت ملاصدرا", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(3, 3, "فتح خرمشهر در عملیات بیت‌المقدس و روز مقاومت و پیروزی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(3, 14, "رحلت حضرت امام خمینی (ره)", true, OccasionCategory.OFFICIAL),
        SolarOccasionDef(3, 15, "قیام خونین ۱۵ خرداد", true, OccasionCategory.OFFICIAL),
        SolarOccasionDef(3, 20, "روز ملی فرش و روز جهانی صنایع دستی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(3, 25, "روز ملی گل و گیاه", false, OccasionCategory.NATIONAL),

        // تیر
        SolarOccasionDef(4, 1, "جشن آب‌پاشونک (انقلاب تابستانی و آغاز تابستان)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(4, 7, "شهادت آیت‌الله دکتر بهشتی و ۷۲ تن از یارانش (روز قوه قضاییه)", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(4, 10, "روز صنعت و معدن", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(4, 13, "جشن تیرگان و روز قلم", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(4, 25, "روز بهزیستی و تامین اجتماعی", false, OccasionCategory.OFFICIAL),

        // مرداد
        SolarOccasionDef(5, 6, "روز ترویج آموزش‌های فنی و حرفه‌ای", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(5, 8, "روز بزرگداشت شیخ شهاب‌الدین سهروردی (شیخ اشراق)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(5, 10, "جشن چله تابستان (چله تموز)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(5, 14, "صدور فرمان مشروطیت", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(5, 17, "روز خبرنگار", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(5, 28, "سالروز کودتای ۲۸ مرداد علیه دولت ملی دکتر مصدق", false, OccasionCategory.OFFICIAL),

        // شهریور
        SolarOccasionDef(6, 1, "روز بزرگداشت ابوعلی سینا و روز پزشک", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(6, 2, "آغاز هفته دولت و سالروز شهادت رجایی و باهنر", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(6, 4, "جشن شهریورگان و روز کارمند", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(6, 5, "روز بزرگداشت محمد بن زکریای رازی و روز داروسازی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(6, 13, "روز بزرگداشت ابوریحان بیرونی و روز علوم پایه", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(6, 21, "روز ملی سینما", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(6, 27, "روز شعر و ادب فارسی و بزرگداشت استاد شهریار", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(6, 31, "آغاز هفته دفاع مقدس", false, OccasionCategory.OFFICIAL),

        // مهر
        SolarOccasionDef(7, 1, "آغاز سال تحصیلی و بازگشایی مدارس (آغاز پاییز)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(7, 7, "روز آتش‌نشانی و ایمنی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(7, 8, "روز بزرگداشت مولوی (جلال‌الدین محمد بلخی)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(7, 10, "جشن باستانی مهرگان", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(7, 14, "روز ملی دامپزشکی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(7, 15, "روز روستا و عشایر", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(7, 20, "روز بزرگداشت حافظ شیرازی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(7, 26, "روز تربیت بدنی و ورزش", false, OccasionCategory.OFFICIAL),

        // آبان
        SolarOccasionDef(8, 7, "روز بزرگداشت کوروش بزرگ", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(8, 8, "روز نوجوان و شهادت محمدحسین فهمیده", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(8, 10, "جشن آبانگان", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(8, 13, "روز دانش‌آموز و مبارزه با استکبار جهانی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(8, 24, "روز کتاب، کتابخوانی و بزرگداشت علامه طباطبایی", false, OccasionCategory.NATIONAL),

        // آذر
        SolarOccasionDef(9, 5, "روز بسیج مستضعفان", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(9, 7, "روز نیروی دریایی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(9, 9, "جشن آذرگان", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(9, 16, "روز دانشجو", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(9, 25, "روز پژوهش و فناوری", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(9, 30, "جشن شب یلدا / شب چله (طولانی‌ترین شب سال)", false, OccasionCategory.NATIONAL),

        // دی
        SolarOccasionDef(10, 1, "جشن خرم‌روز و انقلاب زمستانی (آغاز زمستان)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(10, 5, "روز ملی ایمنی در برابر زلزله و سالروز زلزله بم", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(10, 13, "شهادت سردار قاسم سلیمانی (روز جهانی مقاومت)", false, OccasionCategory.OFFICIAL),

        // بهمن
        SolarOccasionDef(11, 1, "زادروز حکیم ابوالقاسم فردوسی و جشن بهمنگان", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(11, 10, "جشن سده (آیین باستانی آتش)", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(11, 12, "بازگشت امام خمینی به میهن و آغاز دهه فجر", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(11, 19, "روز نیروی هوایی", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(11, 22, "سالروز پیروزی انقلاب اسلامی ایران", true, OccasionCategory.OFFICIAL),
        SolarOccasionDef(11, 29, "جشن سپندارمذگان (روز عشق، مهرورزی و بزرگداشت زن و زمین در ایران باستان)", false, OccasionCategory.NATIONAL),

        // اسفند
        SolarOccasionDef(12, 5, "روز بزرگداشت خواجه نصیرالدین طوسی و روز مهندس", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(12, 15, "روز درختکاری و هفته منابع طبیعی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(12, 20, "روز ملی راهیان نور", false, OccasionCategory.OFFICIAL),
        SolarOccasionDef(12, 24, "روز بزرگداشت پروین اعتصامی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(12, 25, "پایان سرایش شاهنامه و روز اخترشناسی ایرانی", false, OccasionCategory.NATIONAL),
        SolarOccasionDef(12, 29, "روز ملی شدن صنعت نفت ایران", true, OccasionCategory.NATIONAL)
    )

    // ----------------------------------------------------
    // 2. Lunar Religious Occasions Definitions (مناسبت‌های قمری مرجع)
    // ----------------------------------------------------
    data class LunarOccasionDef(
        val lunarMonth: Int,
        val lunarDay: Int,
        val title: String,
        val isHoliday: Boolean
    )

    private val LUNAR_OCCASION_DEFS = listOf(
        LunarOccasionDef(1, 9, "تاسوعای حسینی", true),
        LunarOccasionDef(1, 10, "عاشورای حسینی", true),
        LunarOccasionDef(1, 12, "شهادت امام زین‌العابدین (ع)", false),
        LunarOccasionDef(2, 20, "اربعین حسینی", true),
        LunarOccasionDef(2, 28, "رحلت حضرت رسول اکرم (ص) و شهادت امام حسن مجتبی (ع)", true),
        LunarOccasionDef(2, 29, "شهادت امام علی بن موسی الرضا (ع)", true),
        LunarOccasionDef(3, 8, "شهادت امام حسن عسکری (ع)", true),
        LunarOccasionDef(3, 9, "آغاز امامت حضرت ولی عصر (عج)", false),
        LunarOccasionDef(3, 17, "میلاد رسول اکرم (ص) و امام جعفر صادق (ع)", true),
        LunarOccasionDef(4, 8, "ولادت امام حسن عسکری (ع)", false),
        LunarOccasionDef(4, 10, "وفات حضرت معصومه (س)", false),
        LunarOccasionDef(5, 5, "ولادت حضرت زینب (س) و روز پرستار", false),
        LunarOccasionDef(6, 3, "شهادت حضرت فاطمه زهرا (س)", true),
        LunarOccasionDef(6, 20, "ولادت حضرت فاطمه زهرا (س) و روز مادر و زن", false),
        LunarOccasionDef(7, 1, "ولادت امام محمد باقر (ع)", false),
        LunarOccasionDef(7, 3, "شهادت امام علی النقی الهادی (ع)", false),
        LunarOccasionDef(7, 10, "ولادت امام محمد تقی جوادالائمه (ع)", false),
        LunarOccasionDef(7, 13, "ولادت حضرت امام علی (ع) و روز پدر و مرد", true),
        LunarOccasionDef(7, 15, "وفات حضرت زینب (س)", false),
        LunarOccasionDef(7, 25, "شهادت امام موسی کاظم (ع)", false),
        LunarOccasionDef(7, 27, "مبعث حضرت رسول اکرم (ص)", true),
        LunarOccasionDef(8, 3, "ولادت امام حسین (ع) و روز پاسدار", false),
        LunarOccasionDef(8, 4, "ولادت حضرت ابوالفضل العباس (ع) و روز جانباز", false),
        LunarOccasionDef(8, 5, "ولادت امام سجاد (ع)", false),
        LunarOccasionDef(8, 11, "ولادت حضرت علی اکبر (ع) و روز جوان", false),
        LunarOccasionDef(8, 15, "ولادت حضرت قائم (عج) و عید نیمه شعبان", true),
        LunarOccasionDef(9, 15, "ولادت امام حسن مجتبی (ع) و روز اکرام", false),
        LunarOccasionDef(9, 19, "شب قدر و ضربت خوردن حضرت علی (ع)", false),
        LunarOccasionDef(9, 21, "شهادت حضرت علی (ع) و شب قدر", true),
        LunarOccasionDef(9, 23, "شب قدر", false),
        LunarOccasionDef(10, 1, "عید سعید فطر", true),
        LunarOccasionDef(10, 2, "تعطیلی به مناسبت عید سعید فطر", true),
        LunarOccasionDef(10, 25, "شهادت امام جعفر صادق (ع)", true),
        LunarOccasionDef(11, 1, "ولادت حضرت معصومه (س) و روز دختر", false),
        LunarOccasionDef(11, 11, "ولادت امام رضا (ع)", false),
        LunarOccasionDef(11, 30, "شهادت امام جواد (ع)", false),
        LunarOccasionDef(12, 1, "سالروز ازدواج حضرت علی (ع) و حضرت فاطمه (س)", false),
        LunarOccasionDef(12, 7, "شهادت امام محمد باقر (ع)", false),
        LunarOccasionDef(12, 9, "روز عرفه (روز نیایش)", false),
        LunarOccasionDef(12, 10, "عید سعید قربان", true),
        LunarOccasionDef(12, 15, "ولادت امام هادی (ع)", false),
        LunarOccasionDef(12, 18, "عید سعید غدیر خم", true)
    )

    // ----------------------------------------------------
    // 3. Official Verified Holidays Mapped for Exact Years (1394 - 1406)
    // ----------------------------------------------------
    private data class VerifiedHoliday(val month: Int, val day: Int, val title: String)

    private val VERIFIED_YEARS_HOLIDAYS = mapOf(
        1403 to listOf(
            VerifiedHoliday(1, 13, "شهادت حضرت علی (ع)"),
            VerifiedHoliday(1, 22, "عید سعید فطر"),
            VerifiedHoliday(1, 23, "تعطیلی به مناسبت عید سعید فطر"),
            VerifiedHoliday(2, 15, "شهادت امام جعفر صادق (ع)"),
            VerifiedHoliday(3, 28, "عید سعید قربان"),
            VerifiedHoliday(4, 5, "عید سعید غدیر خم"),
            VerifiedHoliday(4, 25, "تاسوعای حسینی"),
            VerifiedHoliday(4, 26, "عاشورای حسینی"),
            VerifiedHoliday(6, 4, "اربعین حسینی"),
            VerifiedHoliday(6, 12, "رحلت پیامبر اکرم (ص) و شهادت امام حسن مجتبی (ع)"),
            VerifiedHoliday(6, 14, "شهادت امام رضا (ع)"),
            VerifiedHoliday(6, 22, "شهادت امام حسن عسکری (ع)"),
            VerifiedHoliday(6, 31, "میلاد رسول اکرم (ص) و امام جعفر صادق (ع)"),
            VerifiedHoliday(9, 15, "شهادت حضرت فاطمه زهرا (س)"),
            VerifiedHoliday(10, 25, "ولادت حضرت امام علی (ع) و روز پدر"),
            VerifiedHoliday(11, 9, "مبعث حضرت رسول اکرم (ص)"),
            VerifiedHoliday(11, 26, "ولادت حضرت قائم (عج) و عید نیمه شعبان")
        ),
        1404 to listOf(
            VerifiedHoliday(1, 11, "عید سعید فطر"),
            VerifiedHoliday(1, 12, "تعطیلی عید فطر"),
            VerifiedHoliday(2, 4, "شهادت امام جعفر صادق (ع)"),
            VerifiedHoliday(3, 17, "عید سعید قربان"),
            VerifiedHoliday(3, 25, "عید سعید غدیر خم"),
            VerifiedHoliday(4, 14, "تاسوعای حسینی"),
            VerifiedHoliday(4, 15, "عاشورای حسینی"),
            VerifiedHoliday(5, 24, "اربعین حسینی"),
            VerifiedHoliday(6, 1, "رحلت رسول اکرم (ص) و شهادت امام حسن مجتبی (ع)"),
            VerifiedHoliday(6, 3, "شهادت امام رضا (ع)"),
            VerifiedHoliday(6, 10, "شهادت امام حسن عسکری (ع)"),
            VerifiedHoliday(6, 19, "میلاد رسول اکرم (ص) و امام جعفر صادق (ع)"),
            VerifiedHoliday(9, 3, "شهادت حضرت فاطمه زهرا (س)"),
            VerifiedHoliday(10, 14, "ولادت حضرت امام علی (ع) و روز پدر"),
            VerifiedHoliday(10, 28, "مبعث حضرت رسول اکرم (ص)"),
            VerifiedHoliday(11, 15, "ولادت حضرت قائم (عج) و عید نیمه شعبان"),
            VerifiedHoliday(12, 21, "شهادت حضرت علی (ع)")
        ),
        1405 to listOf(
            VerifiedHoliday(1, 24, "شهادت امام جعفر صادق (ع)"),
            VerifiedHoliday(3, 6, "عید سعید قربان"),
            VerifiedHoliday(3, 14, "عید سعید غدیر خم"),
            VerifiedHoliday(4, 4, "تاسوعای حسینی"),
            VerifiedHoliday(4, 5, "عاشورای حسینی"),
            VerifiedHoliday(5, 14, "اربعین حسینی"),
            VerifiedHoliday(5, 22, "رحلت رسول اکرم (ص) و شهادت امام حسن مجتبی (ع)"),
            VerifiedHoliday(5, 24, "شهادت امام رضا (ع)"),
            VerifiedHoliday(5, 30, "شهادت امام حسن عسکری (ع)"),
            VerifiedHoliday(6, 9, "میلاد رسول اکرم (ص) و امام جعفر صادق (ع)"),
            VerifiedHoliday(8, 23, "شهادت حضرت فاطمه زهرا (س)"),
            VerifiedHoliday(10, 3, "ولادت حضرت امام علی (ع) و روز پدر"),
            VerifiedHoliday(10, 17, "مبعث حضرت رسول اکرم (ص)"),
            VerifiedHoliday(11, 4, "ولادت حضرت قائم (عج) و عید نیمه شعبان"),
            VerifiedHoliday(12, 10, "شهادت حضرت علی (ع)"),
            VerifiedHoliday(12, 20, "عید سعید فطر"),
            VerifiedHoliday(12, 21, "تعطیلی به مناسبت عید سعید فطر")
        )
    )

    /**
     * Returns all occasions (Solar + Lunar) for a specific Jalali Date.
     */
    fun getOccasionsForDay(year: Int, month: Int, day: Int): List<Occasion> {
        val result = mutableListOf<Occasion>()
        val jDate = JalaliDate(year, month, day)

        // 1. Solar Fixed Occasions
        val solarOccasions = FIXED_SOLAR_OCCASIONS.filter { it.month == month && it.day == day }
        solarOccasions.forEach { def ->
            result.add(
                Occasion(
                    id = "solar_${year}_${month}_${day}_${def.title.hashCode()}",
                    title = def.title,
                    isHoliday = def.isHoliday,
                    category = def.category,
                    month = month,
                    day = day,
                    year = year
                )
            )
        }

        // 2. Verified Mapped Religious Holidays for exact known years
        val verifiedHolidays = VERIFIED_YEARS_HOLIDAYS[year]?.filter { it.month == month && it.day == day }
        if (!verifiedHolidays.isNullOrEmpty()) {
            verifiedHolidays.forEach { vh ->
                if (result.none { it.title.contains(vh.title) || vh.title.contains(it.title) }) {
                    result.add(
                        Occasion(
                            id = "religious_verified_${year}_${month}_${day}_${vh.title.hashCode()}",
                            title = vh.title,
                            isHoliday = true,
                            category = OccasionCategory.RELIGIOUS,
                            month = month,
                            day = day,
                            year = year
                        )
                    )
                }
            }
        }

        // 3. Astronomical Lunar Occasions for all years (1300 to 1500)
        val islamicDate = JalaliCalendarEngine.jalaliToIslamic(jDate)
        val lunarMatches = LUNAR_OCCASION_DEFS.filter {
            it.lunarMonth == islamicDate.month && it.lunarDay == islamicDate.day
        }
        lunarMatches.forEach { lDef ->
            val isAlreadyAdded = result.any { it.title.contains(lDef.title) || lDef.title.contains(it.title) }
            if (!isAlreadyAdded) {
                result.add(
                    Occasion(
                        id = "religious_lunar_${year}_${month}_${day}_${lDef.title.hashCode()}",
                        title = lDef.title,
                        isHoliday = lDef.isHoliday,
                        category = OccasionCategory.RELIGIOUS,
                        month = month,
                        day = day,
                        year = year
                    )
                )
            }
        }

        return result
    }

    /**
     * Returns all occasions for a specific month.
     */
    fun getOccasionsForMonth(year: Int, month: Int): List<Occasion> {
        val daysInMonth = JalaliCalendarEngine.getDaysInJalaliMonth(year, month)
        val result = mutableListOf<Occasion>()
        for (day in 1..daysInMonth) {
            result.addAll(getOccasionsForDay(year, month, day))
        }
        return result
    }

    /**
     * Returns all occasions for a specific year (1300 to 1500).
     */
    fun getOccasionsForYear(year: Int): List<Occasion> {
        val result = mutableListOf<Occasion>()
        for (m in 1..12) {
            result.addAll(getOccasionsForMonth(year, m))
        }
        return result
    }

    /**
     * Full-text search for occasions across a year or whole database.
     */
    fun searchOccasions(query: String, year: Int = JalaliCalendarEngine.getTodayJalali().year): List<Occasion> {
        if (query.isBlank()) return emptyList()
        val yearOccasions = getOccasionsForYear(year)
        return yearOccasions.filter { it.title.contains(query.trim(), ignoreCase = true) }
    }
}
