# 🌙 Vexono (وکسونو) — تقویم هوشمند شمسی با نمایش میلادی و قمری

<p align="center">
  <img src="docs/vexono_logo.svg" alt="Vexono Logo" width="140" height="140" />
</p>

<p align="center">
  <strong>یک اپلیکیشن بومی، فوق‌العاده سریع، مدرن و کاملاً آفلاین برای مدیریت زمان و تقویم سه‌گانه ایران</strong>
  <br />
  Native Android • 100% Kotlin • Jetpack Compose • Material 3 • Clean Architecture
</p>

<p align="center">
  <a href="https://github.com/itsradmanm/Vexono/releases/latest"><img src="https://img.shields.io/badge/Download-APK%20(Latest)-7C4DFF?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" /></a>
  <a href="https://github.com/itsradmanm/Vexono/actions"><img src="https://img.shields.io/badge/GitHub%20Actions-CI%2FCD%20Build-00E5C7?style=for-the-badge&logo=githubactions&logoColor=black" alt="CI/CD Build" /></a>
</p>

<p align="center">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-blue.svg" alt="License" /></a>
  <a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-2.0+-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin" /></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?logo=jetpackcompose&logoColor=white" alt="Compose" /></a>
  <img src="https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-brightgreen.svg" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-blue.svg" alt="Target SDK" />
</p>

---

## 📥 دانلود و نصب مستقیم فایل APK

شما می‌توانید بدون نیاز به هیچ ابزار توسعه‌ای، فایل نصبی برنامه را مستقیماً دانلود و روی گوشی خود نصب کنید:

| روش دانلود | لینک دریافت | توضیحات |
| :--- | :--- | :--- |
| **دانلود نسخه Release (پیشنهادی)** | [**📦 دانلود مستقیم از بخش Releases**](https://github.com/itsradmanm/Vexono/releases) | آخرین فایل پایدار نصبی (`Vexono-latest.apk`) |
| **دانلود از GitHub Actions** | [**⚡ مشاهده بیلدها و Artifacts**](https://github.com/itsradmanm/Vexono/actions) | فایل APK ساخته‌شده در هر کامیت و بیلد خودکار |

> **راهنمای نصب:** پس از دانلود فایل `.apk` روی گوشی اندرویدی خود، آن را باز کرده و در صورت درخواست، گزینه *Allow from this source (اجازه نصب از این منبع)* را فعال کنید.

---

## 📑 فهرست مطالب
- [🌟 ویژگی‌های کلیدی اپلیکیشن](#-ویژگیهای-کلیدی-اپلیکیشن)
- [🎨 رابط کاربری و طراحی (UI/UX)](#-رابط-کاربری-و-طراحی-uiux)
- [📅 پایگاه داده مناسبت‌ها و تعطیلات ۱۰ ساله](#-پایگاه-داده-مناسبتها-و-تعطیلات-۱۰-ساله)
- [🏗 معماری نرم‌افزار (Clean Architecture)](#-معماری-نرم‌افزار-clean-architecture)
- [📱 ویجت صفحه اصلی (AppWidget)](#-ویجت-صفحه-اصلی-appwidget)
- [🛠 نحوه کامپایل و اجرای سورس‌کد](#-نحوه-کامپایل-و-اجرای-سورس‌کد)
- [🤝 مشارکت و توسعه](#-مشارکت-و-توسعه)
- [📜 لایسنس](#-لایسنس)

---

## 🌟 ویژگی‌های کلیدی اپلیکیشن

1. **🗓 تقویم سه‌گانه دقیق و ۱۰۰٪ آفلاین:**
   - نمایش اصلی روزهای شمسی (جلالی) با اعداد فارسی خوانا.
   - نمایش ظریف و خوانای معادل تاریخ میلادی و قمری در همان خانه تقویم.
   - الگوریتم دقیق ریاضی خیام-بیرشک/بورکوفسکی بدون خطا در سال‌های کبیسه.

2. **🎉 مناسبت‌ها و تعطیلات رسمی ۱۰ ساله (۱۳۹۴ تا ۱۴۰۶+):**
   - بیش از صدها مناسبت ملی، باستانی و مذهبی ایران.
   - هایلایت قرمز رنگ تعطیلات رسمی و روزهای جمعه در تقویم.
   - جستجوی سریع مناسبت‌ها بر اساس عنوان و تاریخ.

3. **⏰ مدیریت هوشمند رویدادها و یادآورها:**
   - ثبت رویدادها با عنوان، زمان، برچسب رنگی و یادآوری با نوتیفیکیشن.
   - پشتیبانی از تکرار رویدادها (روزانه، هفتگی، ماهانه، سالانه).
   - زمان‌بندی دقیق با `WorkManager` بدون مصرف اضافه باتری.

4. **✅ چک‌لیست کارهای روزمره (To-Do List):**
   - ثبت سریع وظایف روزانه و علامت‌گذاری انجام‌شده‌ها.
   - اولویت‌بندی تسک‌ها (بالا، متوسط، پایین).

5. **⚙️ شخصی‌سازی گسترده و تم نئونی:**
   - تم تاریک اختصاصی نئونی (`#0F0F14`) و تم روشن.
   - انتخاب رنگ اصلی برنامه از میان ۶ رنگ جذاب (بنفش نئونی، فیروزه‌ای، زمردی، طلایی، سرخ و نیلی).
   - امکان فعال/غیرفعال کردن نمایش تاریخ‌های میلادی و قمری.

6. **📱 ویجت هوم‌اسکرین (Home Screen Widget):**
   - نمایش زنده و همیشه به‌روز تاریخ شمسی، میلادی و مناسبت روز جاری روی صفحه اصلی گوشی.

---

## 🎨 رابط کاربری و طراحی (UI/UX)

- **طراحی Dark-First نئونی:** مناسب برای استفاده در شب، کاهش خستگی چشم و مصرف باتری در صفحات AMOLED.
- **انیمیشن‌های نرم Compose:** سوایپ آسان بین ماه‌ها با ترنزیشن‌های انیمیشنی روان.
- **پشتیبانی کامل از RTL:** طراحی کامپوننت‌ها به صورت کاملاً بهینه‌شده برای زبان فارسی.

---

## 📅 پایگاه داده مناسبت‌ها و تعطیلات ۱۰ ساله

دیتابیس آفلاین مناسبت‌های تقویم رسمی ایران برای سال‌های **۱۳۹۴ تا ۱۴۰۶ شمسی** به صورت پیش‌فرض در اپلیکیشن تعبیه شده است:
- تمامی تعطیلات رسمی شمسی (نوروز، سیزده‌بدر، سالروز پیروزی انقلاب، یلدا و ...).
- تمامی مناسبت‌های مذهبی با تاریخ متغیر قمری که با دقت بالا به تاریخ شمسی متناظر نگاشت شده‌اند (عید فطر، قربان، غدیر، عاشورا، تاسوعا، مبعث و ...).
- بارگذاری ۱۰۰٪ آفلاین در پایگاه داده پرسرعت **Room**.

---

## 🏗 معماری نرم‌افزار (Clean Architecture)

ساختار کد بر اساس اصول **Clean Architecture**، الگوی **MVVM** و کتابخانه‌های استاندارد **Android Jetpack** سازمان‌دهی شده است:

```
com.vexono.app/
├── data/
│   ├── calendar/          # محاسبات الگوریتم ریاضی تقویم و تبدیل تاریخ‌ها
│   ├── local/             # دیتابیس Room (Occasions, Events, Tasks)
│   ├── datastore/         # مدیریت تنظیمات کاربر با Jetpack DataStore
│   ├── notification/      # زمان‌بندی نوتیفیکیشن‌ها و WorkManager
│   ├── repository/        # پیاده‌سازی مخازن داده
│   └── widget/            # کدهای مربوط به ویجت صفحه اصلی
├── domain/
│   ├── model/             # مدل‌های پایه (JalaliDate, Event, Task, Occasion)
│   ├── repository/        # اینترفیس‌های لایه دامنه
│   └── usecase/           # موارد کاربرد تقویم، رویدادها، تسک‌ها و تنظیمات
├── presentation/
│   ├── components/        # کامپوننت‌های ماژولار رابط کاربری (Jetpack Compose)
│   ├── navigation/        # سیستم مسیریابی Compose Navigation
│   ├── screens/           # صفحات اصلی (Calendar, DayDetail, EventEditor, Tasks, Occasions, Settings)
│   ├── theme/             # سیستم تایپوگرافی، پالت‌های رنگی و تم Material 3
│   └── viewmodel/         # مدیریت State و جریان‌های Flow
└── di/                    # مدیریت وابستگی‌ها (Dependency Injection)
```

---

## 📱 ویجت صفحه اصلی (AppWidget)

ویجت استاندارد ۲×۲ برای صفحه اصلی اندروید:
- نمایش عدد درشت و زیبای روز جاری شمسی.
- نمایش ماه و سال شمسی و نام روز هفته به همراه وضعیت تعطیلی.
- تاریخ میلادی و عنوان مناسبت روز به صورت خودکار.

---

## 🛠 نحوه کامپایل و اجرای سورس‌کد

اگر برنامه‌نویس هستید و می‌خواهید سورس‌کد را تغییر دهید یا به صورت لوکال بیلد بگیرید:

### پیش‌نیازها:
- **Android Studio Ladybug (2024.2+)** یا جدیدتر
- **JDK 17**
- **Android SDK Platform 35**

### دستورات ترمینال:
```bash
# ۱. کلون کردن مخزن
git clone https://github.com/itsradmanm/Vexono.git
cd Vexono

# ۲. اجرای تست‌های خودکار
./gradlew test

# ۳. بیلد خروجی APK (نسخه Debug)
./gradlew assembleDebug

# ۴. نصب مستقیم روی گوشی متصل به کامپیوتر
./gradlew installDebug
```

---

## 🤝 مشارکت و توسعه (Contributing)

از هرگونه مشارکت، پیشنهاد قابلیت جدید یا گزارش مشکل (Issue) صمیمانه استقبال می‌کنیم!
- برای گزارش باگ یا پیشنهاد قابلیت: [بخش Issues](https://github.com/itsradmanm/Vexono/issues)
- راهنمای ارسال مشارکت: [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📜 لایسنس

این پروژه تحت مجوز متن‌باز **[MIT License](LICENSE)** منتشر شده است و استفاده از آن آزاد می‌باشد.

<p align="center">
  ساخته شده با ❤️ برای جامعه فارسی‌زبان
</p>
