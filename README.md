# Skip Night Toggle (skipnight)

Fabric-мод для Minecraft 1.21.11, который кладёт в каждый мир датапак с
переключателем "пропуск ночи/грозы".

## Что делает датапак

- При входе игрока в кровать (срабатывание через advancement-триггер
  `minecraft:slept_in_bed`) датапак сам ставит:
  - `gamerule playersSleepingPercentage 50` - если переключатель **включён**
    (стандартное поведение, ночь/гроза пропускается).
  - `gamerule playersSleepingPercentage 101` - если переключатель **отключён**
    (игроки могут лежать в кровати, но ночь/грозу никогда не пропустить).
- Состояние переключателя хранится в scoreboard (`#skipnight skipnight.enabled`),
  переживает /reload и перезапуски сервера.
- По умолчанию для нового мира переключатель включён (= ванильное поведение).

## Команды для переключения (нужны права оператора)

```
/function skipnight:enable
/function skipnight:disable
```

Можно повесить на кнопку/рычаг через command block:
`/function skipnight:disable`

## Что делает сам мод

Мод не содержит никакой игровой логики - вся механика в датапаке.
Java-код только:

1. При старте сервера (`ServerLifecycleEvents.SERVER_STARTING`) копирует
   встроенный `skipnight.zip` в `<мир>/datapacks/skipnight.zip`,
   перезаписывая старую версию при каждом запуске (так обновление мода
   подтягивает обновление датапака даже в старых мирах).
2. После старта сервера (`SERVER_STARTED`) выполняет
   `/datapack enable "file/skipnight.zip"`, чтобы не включать его руками.

Это работает и для абсолютно новых миров, и для уже существующих, в которые
мод поставили задним числом.

## Сборка

Нужны: JDK 21, интернет-доступ к `maven.fabricmc.net` (для Fabric Loom).

```
./gradlew build
```

(на Windows - `gradlew.bat build`)

Готовый jar появится в `build/libs/skipnight-1.0.0.jar`. Файл `gradlew` /
`gradlew.bat` и папку `gradle/wrapper` в этом архиве нет - проще всего открыть
проект в IntelliJ IDEA с плагином Minecraft Development или сгенерировать
wrapper командой `gradle wrapper` (любая локально установленная Gradle 8.x).

Если что-то не компилируется из-за изменившихся названий методов в Yarn/Loom
для 1.21.11 (Fabric в этой версии активно мигрирует на маппинги Mojang) -
дай знать, какая именно строка не собирается, и я подправлю под актуальный API.

## Структура

```
src/main/java/com/example/skipnight/SkipNightMod.java   - сам мод
src/main/resources/fabric.mod.json                       - манифест мода
src/main/resources/skipnight_datapack.zip                 - встроенный датапак
datapack_src/skipnight/...                                - исходники датапака
                                                             (на случай, если хочешь
                                                             поправить функции и
                                                             пересобрать zip)
```

Чтобы пересобрать `skipnight_datapack.zip` после правок в `datapack_src/`:

```
cd datapack_src/skipnight
zip -r ../../src/main/resources/skipnight_datapack.zip pack.mcmeta data
```
