# Выполняется автоматически при загрузке датапака (старт сервера, /reload)
scoreboard objectives add skipnight.enabled dummy

# Если значение ещё не задано (новый мир) — включаем пропуск по умолчанию (как в ванили)
execute unless score #skipnight skipnight.enabled matches -2147483648..2147483647 run scoreboard players set #skipnight skipnight.enabled 1

# Применяем текущее сохранённое состояние к gamerule
execute if score #skipnight skipnight.enabled matches 1 run gamerule playersSleepingPercentage 50
execute if score #skipnight skipnight.enabled matches 0 run gamerule playersSleepingPercentage 101
