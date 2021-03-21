CREATE DATABASE CALLER;
USE CALLER;

CREATE TABLE Users
(
    phone_number INT PRIMARY KEY,
    e_mail VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    u_password VARCHAR(50) NOT NULL,
    premium BOOL NOT NULL
);

CREATE TABLE Devices
(
    id_device INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    device_name NVARCHAR (50) NOT NULL,
    color_logo NVARCHAR (20) NOT NULL DEFAULT 'Тема 1',
    time_choice	NVARCHAR (50) NOT NULL DEFAULT 'Только вручную',
    work_type NVARCHAR (50) NOT NULL DEFAULT 'Всегда',
    auto_silent	BOOL DEFAULT 0,
    no_call_beg TIME NULL,
    no_call_end TIME NULL,
    synchronized_id INT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (phone_number),
    FOREIGN KEY (synchronized_id) REFERENCES Devices (id_device)
);

CREATE TABLE Calls
(
    id_call INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    recipient_nuber INT NOT NULL,
    call_date_time DATETIME NOT NULL,
    note NVARCHAR (255) NULL,
    FOREIGN KEY (user_id) REFERENCES Users (phone_number)
);