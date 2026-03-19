MyFinance: Приложение для учёта личных финансов
=======
MyFinance: Приложение для учёта личных финансов


***Проект на данный момент находится на стадии перехода от:***
* Java -> XML(Activity&Fragment)-> LiveData
на:
* Kotlin -> Jetpack Compose -> StateFlow -> Coroutines -> Hilt


***О проекте***

MyFinance — это современное и интуитивно понятное Android-приложение, разработанное для эффективного управления личными финансами. Проект позволяет пользователям легко отслеживать доходы и расходы, категоризировать транзакции и надёжно хранить данные в облаке, обеспечивая доступ с любого устройства.

Это приложение было создано как pet-проект с целью демонстрации навыков в разработке на Android с использованием современных архитектурных подходов, лучших практик и передовых технологий.

---
**Ключевые функции**
Управление транзакциями: Удобное добавление и просмотр всех финансовых операций.

Настраиваемый интерфейс: Возможность переключаться между светлой и тёмной темой.

---
**Используемые технологии и архитектура**
Проект разработан с использованием архитектуры MVVM (Model-View-ViewModel), что обеспечивает чистоту кода, его тестируемость и удобство поддержки.

Язык: Kotlin

Мобильная платформа: Android SDK

Архитектура: MVVM (ViewModel, StateFlow, Repository)

Локальное хранилище: Room

Облачные сервисы: Firebase (Authentication, Firestore)

Внедрение зависимостей: Hilt 

Потоки: Kotlin Coroutines

📱 Скриншоты

<div align="center">
<table>
<tr>
<td align="center"><b>Главная</b></td>
<td align="center"><b>Анализ</b></td>
<td align="center"><b>Профиль</b></td>

</tr>
<tr>
<td><img src="screenshots/main.jpg" width="220" /></td>
<td><img src="screenshots/analiz.jpg" width="220" /></td>
<td><img src="screenshots/profile.jpg" width="220" /></td>

</tr>

<tr align="center">
<td align="center"><b>Авторизация</b></td>
<td align="center"><b>Регистрация</b></td>

</tr>
<tr>
<td><img src="screenshots/auth.jpg" width="220" /></td>
<td><img src="screenshots/registration.jpg" width="220" /></td>

</tr>

</table>
</div>

---
**Установка и запуск**
Чтобы запустить проект локально, следуйте этим простым шагам:

1. Клонируйте репозиторий:

    git clone [https://github.com/Orazjan/MyFinance/tree/features/compose-migrate](https://github.com/Orazjan/MyFinance/tree/features/compose-migrate)

2. Откройте проект в Android Studio.

3. Настройте Firebase: создайте проект в Firebase Console, добавьте приложение Android и скопируйте файл google-services.json в директорию app/.

4. Синхронизируйте проект с Gradle-файлами.

5. Запустите приложение на эмуляторе или физическом устройстве.

---
**Контакты**

GitHub: [https://github.com/Orazjan](https://github.com/Orazjan)

Email: [orazjanov11@gmail.com](orazjanov11@gmail.com)
