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
4. Ввести в терминале команду `docker-compose up`
5. Для запуска с поддержкой
   - *СУБД MySQL*:
       - Ввести в терминале команду `java -jar ./artifacts/aqa-shop.jar`
       - Нажать `ctrl` дважды - открыть окно Run anything. Ввести команду `gradlew clean test`
   - *СУБД PostgreSQL*:
       - Ввести в терминале команду ` java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar`
       - Нажать `ctrl` дважды - открыть окно Run anything. Ввести команду `gradlew clean test "-Ddb.url=jdbc:postgresql://localhost:5432/app"`
9. Для генерации Allure отчета нажать: gradle-verification-allureServe
