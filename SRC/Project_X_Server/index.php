<?php
require_once 'initialization.php';
$data = $_POST;
$service_root_path = "";
initialize($data['host'], $data['db_login'], $data['db_pass'], $service_root_path);
$action = $_GET['action'];
//echo "action: ".$action."\n";

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
    case "delete_call":
        $scr = '/process/delete_call.php';
        break;
    case "test":
        $scr = '/test.php';
        break;
    default:
        echo "Неверный адрес страницы";
        break;
}

require_once $service_root_path.$scr;