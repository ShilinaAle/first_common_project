CREATE DATABASE Caller;
USE Caller;

CREATE TABLE Users
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    e_mail VARCHAR(50) NOT NULL UNIQUE,
    u_password VARCHAR(50) NOT NULL,
    premium BOOL NOT NULL
);

CREATE TABLE Numbers
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id)
);

CREATE TABLE Devices
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    device_name NVARCHAR (50) NOT NULL,
    color_logo NVARCHAR (20) NOT NULL DEFAULT 'Shark',
    rescheduling_in_event	NVARCHAR (50) NOT NULL DEFAULT 'Auto',
    rescheduling_out_event NVARCHAR (50) NOT NULL DEFAULT 'Auto'
);

CREATE TABLE UserDevice
(
	id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    device_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id),
    FOREIGN KEY (device_id) REFERENCES Devices (id)
);

CREATE TABLE Calls
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    recipient_number VARCHAR(20) NOT NULL,
    call_date_time DATETIME NOT NULL,
	callback_date_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id)
);

CREATE TABLE Payments
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    summ DECIMAL(5,2) NOT NULL,
    pay_date_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id)
);
