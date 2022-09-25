
-- 默认情况下, springboot启动时自动执行sql
CREATE TABLE master_order_info
(
    id        INT  AUTO_INCREMENT  PRIMARY KEY,
    user_name VARCHAR(30) comment 'user name'
);
CREATE TABLE order_address_info
(
    id                   INT  AUTO_INCREMENT  PRIMARY KEY,
    master_order_info_id INT  comment 'master_order_info.id',
    address           VARCHAR(30) comment 'address'
);
CREATE TABLE slave_order_info
(
    id                   INT  AUTO_INCREMENT  PRIMARY KEY,
    master_order_info_id INT  comment 'master_order_info.id',
    store_name           VARCHAR(30) comment 'store name'
);
CREATE TABLE order_service_info
(
    id                  INT  AUTO_INCREMENT  PRIMARY KEY,
    slave_order_info_id INT  comment 'slave_order_info.id',
    service_name           VARCHAR(30) comment 'service name'
);
CREATE TABLE order_service_price_info
(
    id                  INT  AUTO_INCREMENT  PRIMARY KEY,
    order_service_info_id INT  comment 'order_service_info.id',
    price           DECIMAL(10,2) comment 'price'
);
CREATE TABLE order_good_info
(
    id                  INT  AUTO_INCREMENT  PRIMARY KEY,
    slave_order_info_id INT  comment 'slave_order_info.id',
    good_name           VARCHAR(30) comment 'good name'
);
CREATE TABLE order_good_remark_info
(
    id                  INT  AUTO_INCREMENT  PRIMARY KEY,
    order_good_info_id INT  comment 'order_good_info.id',
    remark           VARCHAR(30) comment 'remark'
);
CREATE TABLE order_good_discount_info
(
    id                  INT AUTO_INCREMENT  PRIMARY KEY,
    slave_order_info_id INT  comment 'slave_order_info.id',
    discount            DECIMAL(10,2) comment 'discount'
);

INSERT INTO master_order_info (id, user_name) values (1, '老王');
INSERT INTO order_address_info (id, master_order_info_id, address) values (1, 1, '上海');

INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (1, 1, '麦当劳');
INSERT INTO order_service_info (id, slave_order_info_id, service_name) values (1, 1, '保价险');
INSERT INTO order_service_price_info (id, order_service_info_id, price) values (1, 1, 99);
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (1, 1, '香辣鸡腿堡');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (2, 1, '可乐');
INSERT INTO order_good_remark_info (id, order_good_info_id, remark) values (1, 2, '加冰');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (1, 1, 0.8);

INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (2, 1, '肯德基');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (3, 2, '蛋挞');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (2, 2, 0.9);


INSERT INTO master_order_info (id, user_name) values (2, '小李');

INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (3, 2, '老乡鸡');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (4, 3, '鸡汤');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (3, 3, 0.7);


INSERT INTO master_order_info (id, user_name) values (3, '小李-update');

INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (4, 4, '老乡鸡-update');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (5, 4, '鸡汤-update');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (4, 4, 0.7);
