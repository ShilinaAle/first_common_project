<?php
/*
 * Получение данных о перенесённых звонках пользователя с сейчас и дальше
 * Приходят данные:
 * email - почта пользователя
 *
 * Возвращает:
 * JSON с полями:
 * * error: 1 или 0
 * * error_text: пустая строка или текст ошибки
 * * calls: {phone: date_time}, где calls - звонки, phone - номер на который нужно перезвонить, call_date_time - время звонка, callback_date_time - вреемя, когда нужно перезвонить
 */
$data = $_POST;
$errors = array();
$json_array = array(
    "error" => 1,
    "error_text" => "Неотлавливаемая ошибка",
    "calls" => array()
);

$user = R::findOne('users', 'e_mail = ?', array($data['email']));
$user_id = $user->id;
if ($user)
{
    $calls = R::findOne('calls', 'user_id = ? AND call_date_time >= ?', array($user_id, time()));
    foreach ($calls as $call)
    {
        $json_array["calls"]["phone"] = $call -> recipient_number;
        $json_array["calls"]["call_date_time"] = date("d.m.Y H:i", $call -> call_date_time);
        $json_array["calls"]["callback_date_time"] = date("d.m.Y H:i", $call -> callback_date_time);
    }

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