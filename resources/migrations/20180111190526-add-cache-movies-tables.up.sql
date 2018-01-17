create table cache (
  url varchar(300) primary key,
  content clob not null,
  modified_at timestamp not null default current_timestamp
);

create table movies (
  id integer auto_increment primary key,
  title varchar(200) not null,
  year smallint not null,
  directed_by varchar(200),
  cast varchar(300),
  country varchar(30),
  lang varchar(30),
  summary clob,
  alt_title varchar(200),
  rating tinyint,
  trailer_link varchar(300),
  created_at timestamp not null default current_timestamp,
  constraint uniq_title_year unique(title, year)
);
