<?php
require_once 'initialization.php';
$data = $_POST;
$action = $_GET['action'];
//echo $action;

switch ($action)
{
    case "login":
        $scr = '/process/login.php';
        break;
    case "singup":
        $scr = '/process/sign_up.php';
        break;
    case "test":
        $scr = '/test.php';
        break;
    default:
        echo "Неверный адрес страницы";
        break;
}

require_once $service_root_path.$scr;