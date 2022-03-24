create table if not exists count_statistic
(
	id bigint auto_increment
		primary key,
	key_data varchar(255) null,
	name varchar(255) null,
	form_id bigint not null,
	key_input_id bigint null
)
engine=InnoDb;

create index if not exists FKf3wm586ymkx8bd0hbv55v3tpa
	on count_statistic (key_input_id);

create index if not exists FKf5c6l1ix0xvonhpm2xs3sq887
	on count_statistic (form_id);

