create table if not exists input_filter
(
	id bigint auto_increment
		primary key,
	data varchar(255) not null,
	form_permission_id bigint not null,
	input_id bigint not null
)
engine=InnoDb;

create index if not exists FK5olmdlvjrettbd7unpo8c9htq
	on input_filter (input_id);

create index if not exists FKa9kl9kcxpiov1l7qq25d6mkr1
	on input_filter (form_permission_id);

