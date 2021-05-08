<?php
/*
 * Обработка смены пароля
 * Приходят данные:
 * email - почта пользователя
 * new_pass - новый пароль пользвателя
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
    $user -> u_password = $data["new_pass"];

    R::store($user);

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