create table if not exists input_group
(
	id bigint auto_increment
		primary key,
	position int not null,
	title varchar(255) null
)
engine=InnoDb;

