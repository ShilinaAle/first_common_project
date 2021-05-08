<?php
/*
 * Обработка изменения настройки устройства
 * Приходят данные:
 * email - почта пользователя
 * device_name - идшник андроида
 * setting_name - название настройки. Могут быть: color_logo, rescheduling_in_event, rescheduling_out_event.
 * value - значение настройки, которое необходимо установить
 *
 * Возвращает:
 * JSON с полями:
 * * error: 1 или 0
 * * error_text: пустая строка или текст ошибки
 */
$data = $_POST;
$errors = array();
$json_array = array(
    "error" => 1,
    "error_text" => "Неотлавливаемая ошибка",
);

$user = R::findOne('users', 'e_mail = ?', array($data['email']));
if ($user)
{
    $device = R::findOne('devices', 'user_id = ? AND device_name = ?', array($user -> id, $data["device_name"]));
    if ($device)
    {
        switch ($data["setting_name"])
        {
            case "color_logo":
                $device -> color_logo = $data["value"];
                break;
            case "rescheduling_in_event":
                $device -> rescheduling_in_event = $data["value"];
                break;
            case "rescheduling_out_event":
                $device -> rescheduling_out_event = $data["value"];
                break;
        }
    }
    else $errors[] = 'У данного пользователя нет устройства с этим AndroidId ╮(._.)╭';

    R::store($device);

    $json_array["error"] = 0;
    $json_array["error_text"] = "";
}
else $errors[] = 'Пользователь с такой почтой не найден ╮(._.)╭';

if(!empty($errors))
{
    $json_array["error"] = 1;
    $json_array["error_text"] = $errors[0];
}

echo json_encode($json_array);
