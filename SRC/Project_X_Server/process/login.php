<?php
$errors = array();
$user = R::findOne('users', 'email = ?', array($data['email']));
if ($user)
{
    if ($data['password'] == $user -> password)
    {
        $_SESSION['logged_user'] = $user;
        //echo '<div style="color: cornflowerblue;">Вы успешно вошли! Теперь вы можете вернуться на <a href="/">главную</a> страницу!</div><hr>';
        echo "<html><head><meta http-equiv='Refresh' content='0; URL=calendar.php'></head><body></body></html>";//перенаправляем

    }
    else $errors[] = 'Неверно введён пароль';
}
else $errors[] = 'Пользователь с таким логином не найден ╮(._.)╭';

if(!empty($errors)) echo '<div style="color: red;">'.array_shift($errors).'</div><hr>';