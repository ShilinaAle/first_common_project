CREATE DATABASE Caller;
USE CALLER;

CREATE TABLE Users
(
    id_user INT PRIMARY KEY AUTO_INCREMENT,
    e_mail VARCHAR(50) NOT NULL UNIQUE,
    u_password VARCHAR(50) NOT NULL,
    premium BOOL NOT NULL
);

CREATE TABLE Numbers
(
    id_number INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id_user)
);

CREATE TABLE Devices
(
    id_device INT PRIMARY KEY AUTO_INCREMENT,
    device_name NVARCHAR (50) NOT NULL,
    color_logo NVARCHAR (20) NOT NULL DEFAULT 'Тема 1',
    time_choice	NVARCHAR (50) NOT NULL DEFAULT 'Только вручную',
    work_type NVARCHAR (50) NOT NULL DEFAULT 'Всегда',
    auto_silent	BOOL DEFAULT 0,
    no_call_beg TIME NULL,
    no_call_end TIME NULL,
    synchronized_id INT NULL,
    FOREIGN KEY (synchronized_id) REFERENCES Devices (id_device)
);

CREATE TABLE Number_Device
(
    number_id INT NOT NULL,
    device_id INT NOT NULL,
    PRIMARY KEY(number_id, device_id),
    FOREIGN KEY (number_id) REFERENCES Numbers (id_number),
    FOREIGN KEY (device_id) REFERENCES Devices (id_device)
);

CREATE TABLE Calls
(
    id_call INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    recipient_number VARCHAR(20) NOT NULL,
    call_date_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id_user)
);

CREATE TABLE Payments
(
    id_payment INT PRIMARY KEY AUTO_INCREMENT,
    user_id	INT NOT NULL,
    summ DECIMAL(5,2) NOT NULL,
    pay_date_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users (id_user)
);



#Тестовые данные
INSERT INTO Users (id_user, e_mail, u_password, premium)
VALUES
(1, 'ABC@mail.ru', 'AbCd', true),
(2, '123@gmail.com', '18475', false),
(3, 'a3!-k6@yandex.ru', '-49-vrf3#', true)
#,(4, 'АБВ@mail.ru', 'ВБА456', true) #строка не пройдет, так как поля e_mail, u_password принимают только латиницу
#,(5, 'ABC@mail.ru', 'AAAA', false) #строка не пройдет, так как такой адрес почты уже есть
#,(6, null, null, null) #строка не пройдет, так как поля не могут принимать значение null
;
SELECT * FROM Users;

INSERT INTO Numbers (id_number, user_id, phone_number)
VALUES
(1, 1, '83456790920'),
(2, 2, '+79771112233'),
(3, 1, '333-333-333')
#,(2, null) #срока не пройдет, так как номер телефона не может принимать значение null
#,(6, '970-584-34') #строка не пройдет, так как нет пользователя с id = 6
;
SELECT * FROM Numbers;

INSERT INTO Devices (id_device, device_name, color_logo, time_choice, work_type, auto_silent, no_call_beg, no_call_end, synchronized_id)
VALUES
(1, 'Device 1', 'Тема 1', 'Автоматически', 'Всегда', true, '18:00', '09:00', null),
(2, 'Device 2', 'Тема 2', 'Только вручную', 'Только во время мероприятий', true, '19:00', '10:00', 1),
(3, 'Device 3', 'Тема 3', 'Только вручную', 'Всегда', false, null, null, null)
#,(4, 'Device 4', 'Тема 4', 'Выбирать тип переноса во время звонка', 'Всегда', false, null, null, 10) #строка не пройдет, так как нет устройства с id_device = 10
#,(5, null, null, null, null, null, null, null, null) #строка не пройдет, так как не все поля могут принимать значение null
;
SELECT * FROM Devices;

INSERT INTO Number_Device (number_id, device_id)
VALUES
(1,2),
(2, 1),
(1, 3),
(3, 1)
#,(4, 4) #строка не пройдет, так как нет ни устройства с id_device = 4, ни номера с id = 4
;
SELECT * FROM Number_Device;

INSERT INTO Calls (id_call, user_id, recipient_number, call_date_time)
VALUES
(1, 1, '88001001010', '2022-01-01 12:00'),
(2, 1, '8-800-200-20-20', '2022-02-02 14:00')
#,(3, 5, '111222', '2005-05-05 12:55') #строка не пройдет,так как нет пользователя с id = 5
#,(4, null, null, null) #строка не пройдет, так как поял не могут принимать значение null
;
SELECT * FROM Calls;

INSERT INTO Payments (id_payment, user_id, summ, pay_date_time)
VALUES
(1, 1, '150.00', '2021-06-06'),
(2, 1, '110.00', '2021-07-06')
#,(3, 88, '45.0', '2051-06-04') #строка не пройдет,так как нет пользователя с id = 88
#,(4, null, null, null) #строка не пройдет, так как поял не могут принимать значение null
;
SELECT * FROM Payments;