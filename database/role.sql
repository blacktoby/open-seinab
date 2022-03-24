create table if not exists role
(
	id bigint auto_increment
		primary key,
	name varchar(255) not null,
	user_id bigint not null
)
engine=InnoDb;

create index if not exists FK61g3ambult7v7nh59xirgd9nf
	on role (user_id);

