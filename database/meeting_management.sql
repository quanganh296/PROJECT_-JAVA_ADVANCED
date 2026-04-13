DROP DATABASE IF EXISTS meeting_management;
CREATE DATABASE meeting_management;
USE meeting_management;

DROP TABLE IF EXISTS booking_equipments;
DROP TABLE IF EXISTS booking_services;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS equipments;
DROP TABLE IF EXISTS room;
DROP TABLE IF EXISTS users;


CREATE TABLE users (
user_id INT AUTO_INCREMENT PRIMARY KEY,
user_name VARCHAR(50) NOT NULL UNIQUE,
password VARCHAR(255) NOT NULL,
role ENUM('EMPLOYEE','SUPPORT','ADMIN') NOT NULL,
department VARCHAR(100),
full_name VARCHAR(255) NOT NULL,
email VARCHAR(100),
phone VARCHAR(20)
);

USE meeting_management;

-- 1. Thêm 1 nhân viên mẫu (Pass: 123456, Role: EMPLOYEE)

ALTER TABLE users AUTO_INCREMENT = 1; -- Reset lại bộ đếm ID về 1


-- 2. TẠO TÀI KHOẢN NHÂN VIÊN (Sẽ tự động nhận ID = 2)
INSERT INTO users (user_id, user_name, password, role, department, full_name, email, phone)
VALUES (3, 'nvTest', '123', 'EMPLOYEE', 'Phòng Marketing', 'Nguyễn Văn X', 'test@congty.com', '0123456789');

-- 3. TẠO TÀI KHOẢN SUPPORT (Sẽ tự động nhận ID = 3 và 4)
INSERT INTO users (user_id, user_name, password, role, department, full_name, email, phone)
VALUES 
(4, 'sp1', '123', 'SUPPORT', 'Phòng Kỹ Thuật', 'Nhân viên Hỗ trợ 1', 'sp1@congty.com', '0111'),
(5, 'sp2', '123', 'SUPPORT', 'Phòng Kỹ Thuật', 'Nhân viên Hỗ trợ 2', 'sp2@congty.com', '0222');


-- 2. Lấy ID của nhân viên vừa tạo (Giả sử là ID = 1, nếu bạn đã có user khác thì hãy đổi số 1 thành ID tương ứng)
-- Thêm 2 lịch họp mẫu ĐÃ ĐƯỢC DUYỆT (APPROVED) và thời gian SẮP TỚI (Tương lai)

CREATE TABLE room (
room_id INT AUTO_INCREMENT PRIMARY KEY,
room_name VARCHAR(100) NOT NULL UNIQUE,
capacity INT NOT NULL,
location VARCHAR(255),
fixed_equipment TEXT
);

INSERT INTO room (room_name, capacity, location, fixed_equipment) VALUES
('Phòng Họp A (Lớn)', 50, 'Tầng 1 - Tòa A', 'Máy chiếu trần, Bảng trắng, Hệ thống âm thanh, 2 Mic không dây'),
('Phòng Họp B (Vừa)', 20, 'Tầng 2 - Tòa A', 'Tivi 65 inch, Bảng trắng, Camera họp trực tuyến'),
('Phòng Họp C (Nhỏ)', 10, 'Tầng 2 - Tòa B', 'Tivi 50 inch, Bảng trắng'),
('Phòng Họp VIP', 15, 'Tầng 3 - Tòa A', 'Màn hình LED, Hệ thống họp trực tuyến Polycom, Loa âm trần'),
('Phòng Hội Trường', 200, 'Tầng 1 - Tòa C', 'Màn hình LED siêu lớn, Hệ thống âm thanh hội trường, Bục phát biểu');

ALTER TABLE room ADD COLUMN status VARCHAR(50) DEFAULT 'Sẵn sàng';

CREATE TABLE equipments (
equipment_id INT AUTO_INCREMENT PRIMARY KEY,
equipment_name VARCHAR(100) NOT NULL UNIQUE,
total_quantity INT NOT NULL,
available_quantity INT NOT NULL,
status VARCHAR(50) 	
);

INSERT INTO equipments (equipment_name, total_quantity, available_quantity, status) VALUES
('Máy chiếu di động', 5, 5, 'GOOD'),
('Laptop dùng chung', 10, 10, 'GOOD'),
('Micro không dây (Loại rời)', 8, 8, 'GOOD'),
('Bút chỉ Laser (Presenter)', 15, 15, 'GOOD'),
('Bảng Flipchart di động', 4, 4, 'GOOD'),
('Dây chuyển đổi HDMI sang Type-C', 20, 20, 'GOOD');

CREATE TABLE services (
service_id INT AUTO_INCREMENT PRIMARY KEY,
service_name VARCHAR(100) NOT NULL UNIQUE,
unit_price DECIMAL(10,2) NOT NULL
);

INSERT INTO services (service_name, unit_price) VALUES
('Nước suối (Chai)', 10000.00),
('Cà phê (Ly)', 25000.00),
('Trà túi lọc (Phần)', 15000.00),
('Bánh ngọt Teabreak (Phần)', 45000.00),
('Trái cây dĩa (Combo)', 60000.00),
('Hoa tươi trang trí bàn VIP', 150000.00);

CREATE TABLE bookings (
booking_id INT AUTO_INCREMENT PRIMARY KEY,
room_id INT,
employee_id INT,
support_staff_id INT,
start_time DATETIME NOT NULL,
end_time DATETIME NOT NULL,
participants_count INT NOT NULL,
booking_status ENUM('PENDING','APPROVED','REJECTED', 'CANCELLED') DEFAULT 'PENDING',
prep_status ENUM('PREPARING', 'READY', 'MISSING_EQUIPMENT'),
FOREIGN KEY (employee_id) REFERENCES users(user_id),
FOREIGN KEY (support_staff_id) REFERENCES users(user_id),
CONSTRAINT fk_room_booking FOREIGN KEY (room_id) REFERENCES room(room_id) ON DELETE SET NULL
);

INSERT INTO bookings (room_id, employee_id, support_staff_id, start_time, end_time, participants_count, booking_status, prep_status)
VALUES 
(1, 3, 4, DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL '1 2' DAY_HOUR), 15, 'APPROVED', 'PREPARING'),
(2, 3, 5, DATE_ADD(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL '3 2' DAY_HOUR), 10, 'APPROVED', 'PREPARING');


ALTER TABLE bookings 
MODIFY COLUMN booking_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELED') DEFAULT 'PENDING';

-- Chạy đoạn này để tạo bảng Thiết bị và Dịch vụ đi kèm đơn:
CREATE TABLE booking_equipments (
    booking_id INT,
    equipment_id INT,
    quantity INT,
    PRIMARY KEY (booking_id, equipment_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

CREATE TABLE booking_services (
    booking_id INT,
    service_id INT,
    quantity INT,
    PRIMARY KEY (booking_id, service_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);
