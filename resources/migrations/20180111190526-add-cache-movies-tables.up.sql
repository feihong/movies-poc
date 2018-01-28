create table cache (
  key varchar(300) primary key,
  content jsonb not null,
  expires_at timestamp not null
);

create table movies (
  id serial primary key,
  title varchar(200) not null,
  year smallint not null,
  director varchar(200),
  actors varchar(300),
  country varchar(30),
  language varchar(30),
  plot text,
  poster varchar(300),
  created_at timestamp not null default current_timestamp,
  constraint uniq_title_year unique(title, year)
);
