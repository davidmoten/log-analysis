create table Entry (entry_id varchar(400) primary key, text varchar(1000) not null,entry_time timestamp not null);
create table Property (property_id long auto_increment primary key, entry_id long not null, name varchar(1000) not null, val varchar(1000) not null, foreign key (entry_id) references Entry(entry_id));


