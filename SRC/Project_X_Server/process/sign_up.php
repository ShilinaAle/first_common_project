<?php
$data = $_POST;
$errors = array();

// TODO: проверка, что почта не дублируется

if(R::count('users', "email = ?", array($data['email'])) > 0) $errors[] = 'Пользователь с таким email уже существует';

if(empty($errors))
{
    //echo "всё ок";
    $user = R::dispense('users'); //создание таблицы users
    $user -> phone_number = $data['phone'];
    $user -> e_mail = $data['email'];
    $user -> password = $data['password'];
    R::store($user);
    echo "success";
}
else
{
    echo $errors[0];
}