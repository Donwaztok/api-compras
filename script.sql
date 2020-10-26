CREATE DATABASE apicompras;

CREATE SCHEMA apicompras_db;
set search_path to apicompras_db, public, pg_catalog;

CREATE TABLE pedido(
	id serial,
	total numeric NOT null,
	data timestamp NOT NULL,
	PRIMARY KEY (id)
)

CREATE TABLE produto(
	id serial,
	nome varchar(256),
	quantidade integer,
	preco NUMERIC,
	PRIMARY KEY (id)
)

CREATE TABLE pedido_item(
	id serial,
	produto_id integer,
	pedido_id integer,
	total NUMERIC,
	quantidade integer,
	PRIMARY KEY (id),
	FOREIGN KEY (produto_id) REFERENCES produto(id),
	FOREIGN KEY (pedido_id) REFERENCES pedido(id)
)