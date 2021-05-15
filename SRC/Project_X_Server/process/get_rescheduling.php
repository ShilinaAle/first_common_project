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
    $calls = R::find('calls', 'user_id = ? AND callback_date_time >= ?', array($user_id, time()));
    if ($calls)
    {
        $i = 1;
        foreach ($calls as $call)
        {
            $json_array["calls"][$i]["id"] = $call->id;
            $json_array["calls"][$i]["phone"] = $call -> recipient_number;
            $json_array["calls"][$i]["call_date_time"] = date("d.m.Y H:i", $call -> call_date_time);
            $json_array["calls"][$i]["callback_date_time"] = date("d.m.Y H:i", $call -> callback_date_time);
            $i ++;
        }

        $json_array["error"] = 0;
        $json_array["error_text"] = "";
    }
    else $errors[] = 'Нет запланированных звонков';
}
else $errors[] = 'Пользователь с такой почтой не найден ╮(._.)╭';

if(!empty($errors))
{
    $json_array["error"] = 1;
    $json_array["error_text"] = $errors[0];
}

echo json_encode($json_array);