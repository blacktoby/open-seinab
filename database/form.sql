create table if not exists form
(
	id bigint auto_increment
		primary key,
	display_name varchar(255) not null,
	email_message text null,
	name varchar(255) not null,
	password varchar(255) null,
	event_group_id bigint not null,
	constraint UK5k8agowivhlstayt0wuw19ahe
		unique (id, event_group_id)
)
engine=InnoDb;

create index if not exists FKrvksdla097v16bm53m3clw8hk
	on form (event_group_id);

