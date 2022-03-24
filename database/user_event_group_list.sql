create table if not exists user_event_group_list
(
	user_id bigint not null,
	event_group_list_id bigint not null,
	constraint UK_mvnq5vew63mrtcejc1ejecmhs
		unique (event_group_list_id)
)
engine=InnoDb;

create index if not exists FK62nnawo4m2at8mfgy6l2mdgp6
	on user_event_group_list (user_id);

alter table user_event_group_list drop key UK_mvnq5vew63mrtcejc1ejecmhs;

alter table user_event_group_list
    add constraint user_event_group_list_pk
        unique (user_id, event_group_list_id);
