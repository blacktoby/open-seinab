create table if not exists form_permission
(
	id bigint auto_increment
		primary key,
	write_permitted bit not null,
	form_id bigint not null,
	user_id bigint not null
)
engine=InnoDb;

create index if not exists FK34penybqbfddleqwioqq6h0tb
	on form_permission (user_id);

create index if not exists FKbunwelf5ahfgo9cdv4fsnct0w
	on form_permission (form_id);

