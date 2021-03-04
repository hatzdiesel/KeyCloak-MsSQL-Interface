create database hds_auth;

use hds_auth;

DELIMITER //

create table UserEntity (
        id bigint not null auto_increment,
        username varchar(255),
        phone varchar(20),
        email varchar(100),
        password varchar(50),
        fullname varchar(30)
        primary key (id)
    ) ENGINE=InnoDB; //

CREATE PROCEDURE GetUserByName(user_name varchar(20))
BEGIN
   select id,username,phone,email,fullname from UserEntity where username = user_name;
   END //

DELIMITER ;
    
insert into UserEntity (username, phone, email,password, fullname) values('Bardh1', '3458738735', 'bala@test.com','Bardh1', 'Bardh Abd');
insert into UserEntity (username, phone, email,password, fullname) values('Bardh2', '538764874', 'kris@test.com','Bardh2', 'Bardh abdylii');
