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
    device_name INT (50) NOT NULL,
    color_logo NVARCHAR (20) NOT NULL DEFAULT 'Shark',
    rescheduling_in_event	TINYINT (2) NOT NULL DEFAULT 1,
    rescheduling_out_event TINYINT (2) NOT NULL DEFAULT 1
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



#Тестовые данные
INSERT INTO Users (id, e_mail, u_password, premium)
VALUES
(1, 'ABC@mail.ru', 'AbCd', true),
(2, '123@gmail.com', '18475', false),
(3, 'a3!-k6@yandex.ru', '-49-vrf3#', true)
#,(4, 'АБВ@mail.ru', 'ВБА456', true) #строка не пройдет, так как поля e_mail, u_password принимают только латиницу
#,(5, 'ABC@mail.ru', 'AAAA', false) #строка не пройдет, так как такой адрес почты уже есть
#,(6, null, null, null) #строка не пройдет, так как поля не могут принимать значение null
;
SELECT * FROM Users;

INSERT INTO Numbers (id, user_id, phone_number)
VALUES
(1, 1, '83456790920'),
(2, 2, '+79771112233'),
(3, 1, '333-333-333')
#,(2, null) #срока не пройдет, так как номер телефона не может принимать значение null
#,(6, '970-584-34') #строка не пройдет, так как нет пользователя с id = 6
;
SELECT * FROM Numbers;

INSERT INTO Devices (id, device_name, color_logo, rescheduling_in_event, rescheduling_out_event)
VALUES
(1, '6633737373', 'Shark', 1, 1),
(2, '12345', 'Shark', 1, 2),
(3, '7777777777', 'Raccoon', 2, 2)
;
SELECT * FROM Devices;

INSERT INTO UserDevice (user_id, device_id)
VALUES
(1,2),
(2, 1),
(1, 3),
(3, 1)
#,(7, 4) #строка не пройдет, так как нет ни устройства с id_device = 4, ни пользователя с id = 7
;
SELECT * FROM UserDevice;

INSERT INTO Calls (id, user_id, recipient_number, call_date_time)
VALUES
(1, 1, '88001001010', '2022-01-01 12:00'),
(2, 1, '8-800-200-20-20', '2022-02-02 14:00')
#,(3, 5, '111222', '2005-05-05 12:55') #строка не пройдет,так как нет пользователя с id = 5
#,(4, null, null, null) #строка не пройдет, так как поял не могут принимать значение null
;
SELECT * FROM Calls;

INSERT INTO Payments (id, user_id, summ, pay_date_time)
VALUES
(1, 1, '150.00', '2021-06-06'),
(2, 1, '110.00', '2021-07-06')
#,(3, 88, '45.0', '2051-06-04') #строка не пройдет,так как нет пользователя с id = 88
#,(4, null, null, null) #строка не пройдет, так как поял не могут принимать значение null
;
SELECT * FROM Payments;