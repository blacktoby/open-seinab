create table if not exists user
(
	id bigint auto_increment
		primary key,
	email varchar(255) not null,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	password varchar(255) not null,
	constraint UK_ob8kqyqqgmefl0aco34akdtpe
		unique (email) using hash
)
engine=InnoDb;

