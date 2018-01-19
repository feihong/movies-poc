create table cache (
  key varchar(300) primary key,
  content jsonb not null,
  expires_at timestamp not null
);
