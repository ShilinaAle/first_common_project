<?php
/*
 * Обработка переноса звонка
 * Приходят данные:
 * email - почта пользователя
 * recipient_number - номер входящего звонка
 * call_date - дата звонка в формате дд.мм.гггг
 * call_time - время звонка в формате чч:мм
 * callback_date - дата, на которую переносится звонок в формате дд.мм.гггг
 * callback_time - время, на которое переносится звонок в формате чч:мм
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
    $call_date_time = $data["call_date"]." ".$data["call_time"];
    $callback_date_time = $data["callback_date"]." ".$data["callback_time"];

    $call = R::dispense('calls');
    $call -> user_id = $user->id;
    $call -> recipient_number = $data['recipient_number'];
    $call -> call_date_time = strtotime($call_date_time);
    $call -> callback_date_time = strtotime($callback_date_time);
    R::store($call);


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