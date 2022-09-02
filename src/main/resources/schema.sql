
-- Ĭ�������, springboot����ʱ�Զ�ִ��sql
CREATE TABLE master_order_info
(
    id        INT  AUTO_INCREMENT  PRIMARY KEY,
    user_name VARCHAR(30) comment 'user name'
);
CREATE TABLE slave_order_info
(
    id                   INT  AUTO_INCREMENT  PRIMARY KEY,
    master_order_info_id INT  comment 'master_order_info.id',
    store_name           VARCHAR(30) comment 'store name'
);
CREATE TABLE order_good_info
(
    id                  INT  AUTO_INCREMENT  PRIMARY KEY,
    slave_order_info_id INT  comment 'slave_order_info.id',
    good_name           VARCHAR(30) comment 'good name'
);
CREATE TABLE order_good_discount_info
(
    id                  INT AUTO_INCREMENT  PRIMARY KEY,
    slave_order_info_id INT  comment 'slave_order_info.id',
    discount            DECIMAL(10,2) comment 'discount'
);

INSERT INTO master_order_info (id, user_name) values (1, '����');
INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (2, 1, '����');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (3, 2, '�������ȱ�');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (4, 2, '����');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (5, 2, 0.8);
INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (3, 1, '�ϵ»�');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (5, 3, '��̢');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (6, 3, 0.9);

INSERT INTO master_order_info (id, user_name) values (2, 'С��');
INSERT INTO slave_order_info (id, master_order_info_id, store_name) values (4, 2, '���缦');
INSERT INTO order_good_info (id, slave_order_info_id, good_name) values (6, 4, '����');
INSERT INTO order_good_discount_info (id, slave_order_info_id, discount) values (7, 4, 0.8);
