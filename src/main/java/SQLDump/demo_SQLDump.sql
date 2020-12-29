create table persons (name varchar, personid int, PRIMARY KEY (personid));
create table orders (orderid int, name varchar, personid int, PRIMARY KEY (orderid), FOREIGN KEY (personid) REFERENCES persons (personid));
create table customers (customerid int, name varchar, personid int, PRIMARY KEY (customerid), FOREIGN KEY (personid) REFERENCES persons (personid));
