create table if not exists reference_settings_reference_inputs
(
	reference_settings_id bigint not null,
	reference_inputs_input_id bigint not null,
	constraint UK_g9vro6n99mcndhwxxd1hrcwqg
		unique (reference_inputs_input_id)
)
engine=InnoDb;

create index if not exists FKsedmw9waycaob46coyuhuh87j
	on reference_settings_reference_inputs (reference_settings_id);

