# MechMatch

Это telegram-бот для знакомств.
Пользователь заполняет анкету и бот волшебными образом подбирает ему собеседника!
Умеет подбирать собеседника по полу, возрасту и городу, а так же сохранять обложки для профилей.
Теперь еще и сохраняет всех пользователей в PostgreSQL базу данных.

## Описание команд:

`/start` - начало работы с ботом

`/help` - вывод описания всех команд

`/myProfile` - бот отправляет пользователю его анкету

`/match` - рекомендация собеседника

`/myMatches` - посмотреть профили лайкнутых или дизлайкнутых пользователей

`/pending` - посмотреть пользователей, которые лайкнули пользователя, но не получили ответа

`/changeProfile` - бот сбрасывает текущую анкету пользователя и создает новую, начинает заново процедуру заполнения анкеты

`/editProfile` - бот предлагает изменить одно из полей анкеты пользователя

`/deleteProfile` - полное удаление профиля

## Запуск бота

### Локально
#### Настройка окружения
- Создать файл `.env` в корне проекта.
- Заполнить по образцу `.env-example`
- Чтобы использовать свою базу данных нужно заполнить PG_DEFAULT HOST и PORT

#### Настройка базы данных
Создать базу `mechmatchdb` данных PostgreSQL и необходимые таблицы(в `src/main/java/database/init` файлы `matches_init.sql` и `clients_init.sql`)


### Docker

#### Настройка окружения
- Создать файл `.env` в корне проекта.
- Заполнить по образцу `.env-example`
- PG_DEFAULT HOST и PORT можно оставить пустыми, они используются для локального запуска

#### Настройка базы данных
Инициализация таблиц происходит автоматически при первой сборке

#### Docker Compose
1. Настроить networks и volumes
    ```shell
    docker network create localnet  
    ```
    ```shell
    docker volume create bot_db
    ```
2. Запустить сборку
    ```shell
    docker compose up -d
    ```
