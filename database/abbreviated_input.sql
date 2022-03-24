create table if not exists abbreviated_input
(
	id bigint auto_increment
		primary key,
	name varchar(255) not null,
	form_id bigint not null,
	input_id bigint not null
)
engine=InnoDb;

create index if not exists FKevr3n7xxam2stibym76yanahs
	on abbreviated_input (form_id);

create index if not exists FKoswd5unhb37axh71mq1mbdfqc
	on abbreviated_input (input_id);

