<?php
/*
 * Обработка авторизации
 * Приходят данные:
 * email - почта пользователя
 * pass - пароль пользвателя
 * android_id - идшник андроида
 *
 * Возвращает:
 * JSON с полями:
 * * error: 1 или 0
 * * error_text: пустая строка или текст ошибки
 * * user_premium: 1 или 0 - есть премиум у пользователя или нет
 */
$data = $_POST;
$errors = array();
$json_array = array(
    "error" => 1,
    "error_text" => "Неотлавливаемая ошибка",
    "user_premium" => 0
);

$user = R::findOne('users', 'e_mail = ?', array($data['email']));
if ($user)
{
    if ($data['pass'] == $user -> u_password)
    {
        $json_array["error"] = 0;
        $json_array["error_text"] = "";
        $json_array["user_premium"] = $user->premium;

    }
    else $errors[] = 'Неверно введён пароль';

    $device = R::findOne('devices', 'device_name = ?', array($data['android_id']));
    if (!$device)
    {
        $device = R::dispense('devices');
        $device -> device_name = $data['android_id'];
        $device -> color_logo = "Shark";
        $device -> rescheduling_in_event = 1;
        $device -> rescheduling_out_event = 1;
        R::store($device);
    }
}
else $errors[] = 'Пользователь с такой почтой не найден ╮(._.)╭';

if(!empty($errors))
{
    $json_array["error"] = 1;
    $json_array["error_text"] = $errors[0];
}

echo json_encode($json_array);