<?php
/*
 * Обработка регистрации
 * Создаются новые записи в таблицах users, numbers, user_device и devices. В последнюю таблицу записываются стандартные настроки
 * Приходят данные:
 * email - почта пользователя
 * phone - номер телефона
 * pass - пароль пользвателя
 * android_id - идшник андроида. Уникален и генерируется на стороне клиента.
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
//var_dump($data);

if(R::count('users', "e_mail = ?", array($data['email'])) > 0) $errors[] = 'Пользователь с таким email уже существует';
if(R::count('numbers', "phone_number = ?", array($data['phone'])) > 0) $errors[] = 'Пользователь с таким номером телефона уже существует';

if(empty($errors))
{
    //echo "всё ок";
    $user = R::dispense('users');
    $user -> e_mail = $data['email'];
    $user -> u_password = $data['pass'];
    $user -> premium = 0;
    R::store($user);

    $number = R::dispense("numbers");
    $number -> phone_number = $data['phone'];
    $number -> user_id = $user -> id;
    R::store($number);

    $device = R::dispense("devices");
    $device -> device_name = $data['android_id'];
    $device -> color_logo = "Shark";
    $device -> rescheduling_in_event = 1;
    $device -> rescheduling_out_event = 1;
    R::store($device);

    $u_d = R::dispense("userdevice");
    $u_d -> user_id = $user -> id;
    $u_d -> device_id = $device -> id;
    R::store($u_d);

    $json_array["error"] = 0;
    $json_array["error_text"] = "";
}
else
{
    $json_array["error"] = 1;
    $json_array["error_text"] = $errors[0];
}
echo json_encode($json_array);