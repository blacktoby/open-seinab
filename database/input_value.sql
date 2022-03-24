create table if not exists input_value
(
	id bigint auto_increment
		primary key,
	data varchar(255) not null,
	date_edited datetime null,
	date_inserted datetime not null,
	form_id bigint not null,
	input_id bigint not null,
	submission_id bigint not null
)
engine=InnoDb;

create index if not exists FKckuc71ay4seyahsb7jnj97owd
	on input_value (input_id);

create index if not exists FKd4g9wpom7g2l2tdv4lrayyrp6
	on input_value (submission_id);

create index if not exists FKew316fxj2spuvl8laixocn986
	on input_value (form_id);

