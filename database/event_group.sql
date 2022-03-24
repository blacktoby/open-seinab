create table if not exists event_group
(
	id bigint auto_increment
		primary key,
	name varchar(255) not null
)
engine=InnoDb;

