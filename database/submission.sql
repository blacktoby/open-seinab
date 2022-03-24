create table if not exists submission
(
	id bigint auto_increment
		primary key,
	date_submitted datetime null,
	form_id bigint null
)
engine=InnoDb;

create index if not exists FKfurrdrod4e0f3e1dcjevcudri
	on submission (form_id);

