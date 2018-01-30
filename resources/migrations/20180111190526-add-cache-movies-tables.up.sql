create table cache (
  id serial primary key,
  key varchar(300),
  content jsonb not null,
  created_at timestamp not null default current_timestamp,
  expires_at timestamp not null,
  constraint uniq_key unique(key)
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
  url varchar(300),
  created_at timestamp not null default current_timestamp,
  constraint uniq_title_year unique(title, year)
);
