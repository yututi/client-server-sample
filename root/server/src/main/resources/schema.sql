create table if not exists USER (
    id long primary key auto_increment,
    name character NOT NULL,
    gender integer NOT NULL,
    age integer NOT NULL,
);
