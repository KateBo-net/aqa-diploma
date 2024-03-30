# Документация
[План тестирования](https://github.com/KateBo-net/aqa-diploma/blob/master/documents/Plan.md)

[Отчет по итогам тестирования](https://github.com/KateBo-net/aqa-diploma/blob/master/documents/Report.md)

[Отчет по итогам автоматизации](https://github.com/KateBo-net/aqa-diploma/blob/master/documents/Summary.md)

# Инструкция для запуска автотестов
## Установить приложения
1. Intelij IDEA
2. Браузер: Chrome Версия 123.0.6312.60
3. Docker Desktop

## Процедура запуска тестов
1. Проверить, что порты 8080, 9999, 5432 или 3306 (в зависимости от выбранной СУБД) свободны
2. Склонировать репозиторий https://github.com/KateBo-net/aqa-diploma
3. Открыть его в Intelij IDEA
4. В файлах `build.gradle` и `application.properties` раскомментировать нужную СУБД, закомментировать вторую
5. Ввести в терминале команду `docker-compose up`
6. Ввести в терминале команду `java -jar ./artifacts/aqa-shop.jar`
7. Нажать `ctrl` дважды - открыть окно Run anything. Ввести команду `gradlew clean test`
8. Для генерации Allure отчета нажать: gradle-verification-allureServe
