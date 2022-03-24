create table if not exists reference_settings
(
	id bigint auto_increment
		primary key,
	seperator varchar(255) not null,
	form_id bigint not null,
	constraint UK_6jsl5wdfuoi6gdg1cq5k3j02p
		unique (form_id)
)
engine=InnoDb;

alter table reference_settings
    add event_name varchar(255) null;

