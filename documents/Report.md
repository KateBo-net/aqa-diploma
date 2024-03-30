# Отчет по итогам тестирования

## Описание
Реализовано автоматизированное тестирование функциональности "покупка тура", в соответствии с [планом тестирования](https://github.com/KateBo-net/aqa-diploma/blob/master/documents/Plan.md).

***Общее количество тестов:*** 80.

## Итоги тестирования
### При подключении к СУБД PostgreSQL
  - Passed test 77.5%
  - Failed test 22.5%

![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/cab7bc05-6938-48df-b5ba-b16c4af0eef6)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/e6db89c2-8eb6-4baf-b2b8-30e0d1167a62)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/f314236e-9a2c-4189-af1f-ded0dad36756)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/41f4c626-3898-4f3c-9bd1-61f14b480845)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/193b9cc1-1897-4c9c-985b-6ebacc91ff85)

### При подключении к СУБД MySQL
  - Passed test 77.5%
  - Failed test 22.5%

![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/0577d3eb-c184-4c4c-854f-8558c1fe1d25)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/ddc3cb54-40c6-4522-8ca7-38db6f7b3e6b)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/333a827e-4668-41d9-83f2-c9c1850a1b8e)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/760de17a-ec8e-4c76-8567-345a562fb13a)

На все обнаруженные дефекты заведены [баг-репорты](https://github.com/KateBo-net/aqa-diploma/issues)

## Общие рекомендации

1. Добавить тестовые метки к элементам страницы, это позволит сделать тесты более стабильными
2. Исправить найденные дефекты
3. Выбрать только 1 СУБД для работы, так как на данный момент работа с 2 СУБД выполняет одинаковую задачу.
4. Добавить больше конкретизации в описании к требованиям по функционалу
