create table if not exists consent
(
	text varchar(255) null,
	form_id bigint not null
		primary key
)
engine=InnoDb;

alter table consent
    add form_pdf_file varchar(255) null;

