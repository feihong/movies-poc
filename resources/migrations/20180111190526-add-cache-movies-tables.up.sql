create table cache (
  url varchar(300) primary key,
  content text not null,
  modified_at timestamp not null default current_timestamp
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
  alt_title varchar(200),
  rating smallint,
  trailer_link varchar(300),
  created_at timestamp not null default current_timestamp,
  constraint uniq_title_year unique(title, year)
);
