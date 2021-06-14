<?php
/*
 * Обработка получения премиума
 * Приходят данные:
 * email - почта пользователя
 * summ - сумма платежа
 * pay_datetime - дата и время платежа в UNIX-формате
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
    $user->premium = 1;
    R::store($user);

    $pay = R::dispense('payments');
    $pay -> user_id = $user->id;
    $pay -> summ = (int)$data['summ'];
    $pay -> pay_date_time = (int)$data["pay_datetime"];
    R::store($pay);


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

