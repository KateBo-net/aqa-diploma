# Отчет по итогам тестирования

## Описание
Реализовано автоматизированное тестирование функциональности "покупка тура", в соответствии с [планом тестирования](https://github.com/KateBo-net/aqa-diploma/blob/master/documents/Plan.md).

***Общее количество тестов:*** 80.

## Итоги тестирования
### При подключении к СУБД PostgreSQL
  - Passed test 80%
  - Failed test 20%

![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/76e5daa2-a827-43ae-bc48-a159696ce842)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/de294ecd-64a1-40dc-8c56-5e44fa8c998a)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/32641559-eaa2-48b5-8b1a-983ebb79ade6)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/bdf06622-ca60-41d8-a8d0-913234e37b49)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/64549300-ff36-439d-aca3-6efafa0e9ed4)

### При подключении к СУБД MySQL
  - Passed test 80%
  - Failed test 20%

![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/49034adc-7b9c-4c20-832f-a3dd772db559)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/5acd1a90-d7a4-480f-b495-e2c34618da10)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/7e1a26a1-df33-4601-86a1-44c42e6bdcb1)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/555afc1f-43c6-4ba1-a17b-29229af0b923)
![image](https://github.com/KateBo-net/aqa-diploma/assets/92302507/dac31ebc-4053-43a0-88a0-c5c3aad0832a)

На все обнаруженные дефекты заведены [баг-репорты](https://github.com/KateBo-net/aqa-diploma/issues)

## Общие рекомендации

1. Добавить тестовые метки к элементам страницы, это позволит сделать тесты более стабильными
2. Исправить найденные дефекты
3. Выбрать только 1 СУБД для работы, так как на данный момент работа с 2 СУБД выполняет одинаковую задачу.
4. Добавить больше конкретизации в описании к требованиям по функционалу
