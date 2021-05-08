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
    case "set_premium":
        $scr = '/process/set_premium.php';
        break;
    case "set_rescheduling":
        $scr = '/process/set_rescheduling.php';
        break;
    case "get_rescheduling":
        $scr = '/process/get_rescheduling.php';
        break;
    case "set_setting":
        $scr = '/process/set_setting.php';
        break;
    case "change_pass":
        $scr = '/process/change_pass.php';
        break;
    case "test":
        $scr = '/test.php';
        break;
    default:
        echo "Неверный адрес страницы";
        break;
}

require_once $service_root_path.$scr;