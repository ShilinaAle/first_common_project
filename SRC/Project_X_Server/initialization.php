<?php
// Здесь процессы, которые необходимо выполнять при каждом обращении к серверу
require "libs/rb-mysql.php";
R::setup( 'mysql:host=localhost;dbname=project_x', 'root', '' );

session_start();

function vardump($var) {
    echo '<pre>';
    var_dump($var);
    echo '</pre>';
}

// Задаём корневую директорию для подключения файлов
$service_root_path = dirname(__FILE__)."/..";
$service_root_path = realpath($service_root_path);
$service_root_path = str_replace("\\","/",$service_root_path);
$service_root_path = $service_root_path."/Project_X_Server";

// Задаём, где искать файлы при подключении
$include_path_separator = PATH_SEPARATOR;
$include_path = $service_root_path;
$include_path_dirs = array(
    "process",
);
foreach ($include_path_dirs as $dirname) {
    $include_path .= "{$include_path_separator}{$service_root_path}/{$dirname}";
}
set_include_path($include_path);