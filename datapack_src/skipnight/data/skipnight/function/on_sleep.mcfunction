# Срабатывает каждый раз, когда игрок ложится в кровать
# Сбрасываем триггер, чтобы он мог сработать повторно
advancement revoke @s only skipnight:trigger

# Ставим gamerule в зависимости от текущего состояния переключателя
execute if score #skipnight skipnight.enabled matches 1 run gamerule playersSleepingPercentage 50
execute if score #skipnight skipnight.enabled matches 0 run gamerule playersSleepingPercentage 101
