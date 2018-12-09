CREATE TABLE account (
	account_pk 			SERIAL UNIQUE,
	username 			VARCHAR(40) NOT NULL,
	passwd 				VARCHAR(256) NOT NULL,
	email 				VARCHAR(50) NOT NULL UNIQUE,
	dos 				DATE not null default CURRENT_DATE,
	last_time_logged 	TIMESTAMP default CURRENT_DATE
);

CREATE TABLE conversation (
	conversation_pk 	SERIAL UNIQUE
);

CREATE TABLE mass_conversation (
	conversation_pk 	SERIAL UNIQUE,
	finished			BOOL NOT NULL DEFAULT FALSE
);

CREATE TABLE accounts_in_mass_conversation (
	conversation_fk_id   INTEGER REFERENCES conversation(conversation_pk) NOT NULL,
	user_fk_id 			 INTEGER REFERENCES account(account_pk) NOT NULL
);

CREATE TABLE contact (
	contact_pk 			SERIAL UNIQUE,
	account_fk_id 		INTEGER REFERENCES account(account_pk) NOT NULL,
	conversation_fk_id	INTEGER REFERENCES conversation(conversation_pk) NOT NULL,
	contact_name	  	VARCHAR(20) DEFAULT 'null',
	status 			  	BOOL default true
);

CREATE TABLE contact_list (
	account_fk_id INTEGER REFERENCES account(account_pk),
	contact_fk_id INTEGER REFERENCES contact(contact_pk)
);

CREATE TABLE message (
	message_pk 			SERIAL UNIQUE,
	sender_fk_id 		INTEGER REFERENCES account(account_pk) NOT NULL,
	conversation_fk_id 	INTEGER REFERENCES conversation(conversation_pk),
	content_of_msg 		VARCHAR(250) NOT NULL,
	send_time 			TIMESTAMP
);

CREATE TABLE messages_in_conversation (
	contact_fk_id INTEGER REFERENCES contact(contact_pk),
	message_fk_id INTEGER REFERENCES message(message_pk)
);

CREATE TABLE contact_requests (
	account_fk_id				INTEGER REFERENCES account(account_pk),
	requesting_account_fk_id 	INTEGER REFERENCES account(account_pk),
	request_message   			VARCHAR(250) NOT NULL
);