create table if not exists count_statistic_abbreviated
(
	id bigint not null
		primary key,
	key_abbreviated_input_id bigint not null
)
engine=InnoDb;

create index if not exists FK79v9mevbwjgwaat6fsj2twoe8
	on count_statistic_abbreviated (key_abbreviated_input_id);

