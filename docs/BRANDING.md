# راهنمای هویت بصری و برندینگ Vexono (Brand Style Guide)

## ۱. فلسفه و مفهوم برند Vexono
نام **Vexono** از تلفیق حس مدرنیته، آینده‌نگری و سادگی گرفته شده است. این اپلیکیشن با هدف ارائه یک تجربه کاربری تمیز، لوکس و بدون حاشیه در مشاهده تقویم شمسی و همراهی تقویم‌های میلادی و قمری طراحی شده است.

---

## ۲. پالت رنگی (Color Palette)

### رنگ‌های اصلی (Core Colors)
| نام رنگ | مقدار هگز (HEX) | نمونه | کاربرد |
| :--- | :--- | :--- | :--- |
| **Dark Background** | `#0F0F14` | ![#0F0F14](https://via.placeholder.com/15/0F0F14/000000?text=+) | پس‌زمینه اصلی دارک مود (مشکی بنفش عمیق) |
| **Dark Surface** | `#1B1B24` | ![#1B1B24](https://via.placeholder.com/15/1B1B24/000000?text=+) | پس‌زمینه کارت‌ها، باتم‌شیت‌ها و ویجت |
| **Surface Elevated**| `#242430` | ![#242430](https://via.placeholder.com/15/242430/000000?text=+) | لایه‌های برجسته، پاپ‌آپ‌ها و دیالوگ‌ها |
| **Brand Primary (Neon Violet)** | `#7C4DFF` | ![#7C4DFF](https://via.placeholder.com/15/7C4DFF/000000?text=+) | رنگ برند، دکمه‌های اصلی و روز انتخاب‌شده |
| **Accent Cyan** | `#00E5C7` | ![#00E5C7](https://via.placeholder.com/15/00E5C7/000000?text=+) | هایلایت‌ها، روز امروز، مناسبت‌های خاص |
| **Holiday / Alert Red** | `#FF5C7A` | ![#FF5C7A](https://via.placeholder.com/15/FF5C7A/000000?text=+) | تعطیلات رسمی، جمعه‌ها و آلارم‌ها |
| **Text Primary** | `#F5F5F7` | ![#F5F5F7](https://via.placeholder.com/15/F5F5F7/000000?text=+) | متون اصلی و اعداد بزرگ شمسی |
| **Text Secondary / Subtle** | `#6E6E7A` | ![#6E6E7A](https://via.placeholder.com/15/6E6E7A/000000?text=+) | متون کم‌رنگ، تاریخ میلادی و زیرنویس‌ها |

---

## ۳. تایپوگرافی (Typography)
- **فونت فارسی:** استاندارد وزیرمتن (Vazirmatn) با وزن‌های Regular, Medium, SemiBold و ExtraBold.
- **نمایش اعداد:** استفاده از ارقام فارسی (Persian Digits: `۰۱۲۳۴۵۶۷۸۹`) برای تقویم شمسی و ارقام انگلیسی برای تقویم میلادی.

---

## ۴. لوگو و نشان تجاری (Logo & Icons)
- **لوگوی اصلی:** فایل وکتور `docs/vexono_logo.svg` با ترکیب حرف «V» نئونی و برگه تقویم مینیمال.
- **آیکون اندروید:** پشتیبانی از Adaptive Icon با دو لایه `foreground` و `background` به همراه لایه `monochrome` برای اندروید ۱۳ و بالاتر (Themed Icons).

---

## ۵. راهنمای به‌روزرسانی سالانه مناسبت‌ها
داده‌های مناسبت‌ها در فایل `app/src/main/assets/occasions_data.json` ذخیره شده‌اند. این فایل دارای ساختار زیر است:
- `solar_occasions`: مناسبت‌های خورشیدی ثابت با فیلدهای `month`, `day`, `title`, `is_holiday`, `category`.
- `yearly_mapped_religious_holidays`: تعطیلات متغیر قمری که برای هر سال شمسی (۱۳۹۴ تا ۱۴۰۶+) بر اساس تقویم رسمی ژئوفیزیک دانشگاه تهران نگاشت شده‌اند.
برای اضافه کردن سال‌های جدید، کلید سال (مانند `"1407"`) را به آبجکت مربوطه اضافه نمایید.
