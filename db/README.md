ERMES DB (LSO)
--------


DBMS: PostgreSQL 15


Codice PLPGSQL dei vari trigger:

--------------------------------------------------------------------------------------------

```SQL
CREATE OR REPLACE FUNCTION aggiungi_creatoreroom() RETURNS TRIGGER AS $aggiungi_creatoreroom$
DECLARE
userid USERDATA.USERID%TYPE;
roomid ROOM.ROOMID%TYPE;
BEGIN
SELECT ROOM.CREATORUSERID, ROOM.ROOMID INTO userid, roomid
FROM ROOM
WHERE ROOM.CREATORUSERID = NEW.CREATORUSERID AND ROOM.ROOMID = NEW.ROOMID;

INSERT INTO ROOMUSERS VALUES
(userid, roomid);
RETURN NULL;
END;	

$aggiungi_creatoreroom$ LANGUAGE PLPGSQL;

CREATE TRIGGER aggiungi_creatoreroom AFTER INSERT ON ROOM
FOR EACH ROW EXECUTE PROCEDURE aggiungi_creatoreroom();
```

Il trigger aggiunge automaticamente il creatore della stanza nella stanza stessa.

--------------------------------------------------------------------------------------------

```SQL
CREATE OR REPLACE FUNCTION verify_userinroom() RETURNS TRIGGER AS $verify_userinroom$
DECLARE
userid USERDATA.USERID%TYPE;
usernamee USERDATA.USERNAME%TYPE;
roomid ROOM.ROOMID%TYPE;
BEGIN
	IF EXISTS (SELECT* FROM ROOMUSERS WHERE ROOMUSERS.USERID = NEW.USERID AND ROOMUSERS.ROOMID = NEW.ROOMID) THEN
	SELECT USERNAME INTO usernamee FROM USERDATA WHERE USERDATA.USERID = NEW.USERID;
	RAISE EXCEPTION '% è già presente nella stanza!', usernamee;
	DELETE FROM JOINREQUEST WHERE JOINREQUEST.USERID = NEW.USERID AND JOINREQUEST.ROOMID = NEW.ROOMID;
	END IF;
	
	RETURN NULL;
END;

$verify_userinroom$ LANGUAGE PLPGSQL;

CREATE TRIGGER VERIFY_USERINROOM AFTER INSERT ON JOINREQUEST
FOR EACH ROW EXECUTE PROCEDURE verify_userinroom();
```

Il trigger verifica se l'utente che viene inserito nella tabella Join Request è già presente nella stanza: in caso 
affermativo l'utente viene eliminato dalla tabella.


--------------------------------------------------------------------------------------------


```SQL
CREATE OR REPLACE FUNCTION existsUser() RETURNS TRIGGER AS $existsUser$
DECLARE
useriid USERDATA.USERID%TYPE;
roomiid ROOM.ROOMID%TYPE;
usernamee USERDATA.USERNAME%TYPE;
BEGIN
SELECT MESSAGEDATA.USERID, MESSAGEDATA.ROOMID INTO useriid, roomiid
FROM MESSAGEDATA
WHERE MESSAGEDATA.USERID = NEW.USERID AND MESSAGEDATA.ROOMID = NEW.ROOMID;

IF NOT EXISTS(SELECT* FROM ROOMUSERS WHERE ROOMUSERS.USERID = useriid AND ROOMUSERS.roomid = roomid) THEN
SELECT USERNAME INTO usernamee FROM USERDATA WHERE USERID = useriid;
RAISE EXCEPTION '% non è presente nella stanza!', usernamee;
DELETE FROM MESSAGEDATA WHERE MESSAGEDATA.USERID = useriid AND MESSAGEDATA.ROOMID = NEW.ROOMID;
END IF;

RETURN NULL;
END;	

$existsUser$ LANGUAGE PLPGSQL;

CREATE TRIGGER existsUser AFTER INSERT ON MESSAGEDATA
FOR EACH ROW EXECUTE PROCEDURE existsUser();
```

Il trigger verifica se l'utente che viene inserito nella tabella MessageData appartiene effettivamente alla stanza.


--------------------------------------------------------------------------------------------


```SQL
CREATE OR REPLACE FUNCTION checkEmptyStringUser() RETURNS TRIGGER AS $checkEmptyStringUser$
DECLARE
checkusername USERDATA.USERNAME%TYPE;
checkpassword USERDATA.USERPASSWORD%TYPE;
cont INTEGER := 0;
substr TEXT;
BEGIN
SELECT USERDATA.USERNAME, USERDATA.USERPASSWORD INTO checkusername, checkpassword
FROM USERDATA
WHERE USERDATA.USERNAME = NEW.USERNAME AND USERDATA.USERPASSWORD = NEW.USERPASSWORD;

IF(LENGTH(checkusername) = 0) THEN RAISE EXCEPTION 'Username non può essere vuoto!'; END IF;
IF(LENGTH(checkpassword) = 0) THEN RAISE EXCEPTION 'La password non può essere vuota!'; END IF;

FOR i IN 0 .. LENGTH(checkusername) LOOP
substr = right(checkusername, length(checkusername)-i);
IF (ascii(substr) = 32 OR ascii(substr) = 9) THEN cont = cont + 1; END IF;
END LOOP;

IF(cont = LENGTH(checkusername)) THEN RAISE EXCEPTION 'Username non può essere vuoto!'; END IF;
cont = 0;

FOR i IN 0 .. LENGTH(checkpassword) LOOP
substr = right(checkpassword, length(checkpassword)-i);
IF (ascii(substr) = 32 OR ascii(substr) = 9) THEN cont = cont + 1; END IF;
END LOOP;

IF(cont = LENGTH(checkusername)) THEN RAISE EXCEPTION 'La password non può essere vuota!'; END IF;


RETURN NULL;
END;	

$checkEmptyStringUser$ LANGUAGE PLPGSQL;

CREATE TRIGGER checkEmptyStringUser AFTER INSERT ON USERDATA
FOR EACH ROW EXECUTE PROCEDURE checkEmptyStringUser();
```

Il trigger verifica se vengano inserite password e username di soli spazi o tab; alza un'eccezione in caso affermativo.


--------------------------------------------------------------------------------------------


```SQL
CREATE OR REPLACE FUNCTION checkEmptyStringMessage() RETURNS TRIGGER AS $checkEmptyStringMessage$
DECLARE
checkmessage MESSAGEDATA.MESSAGETEXT%TYPE;
cont INTEGER := 0;
substr TEXT;
BEGIN
SELECT MESSAGEDATA.MESSAGETEXT INTO checkmessage
FROM MESSAGEDATA
WHERE MESSAGEDATA.MESSAGETEXT = NEW.MESSAGETEXT;

IF(LENGTH(checkmessage) = 0) THEN RAISE EXCEPTION 'Il messaggio non può essere vuoto!'; END IF;

FOR i IN 0 .. LENGTH(checkmessage) LOOP
substr = right(checkmessage, length(checkmessage)-i);
IF (ascii(substr) = 32 OR ascii(substr) = 9) THEN cont = cont + 1; END IF;
END LOOP;

IF(cont = LENGTH(checkmessage)) THEN RAISE EXCEPTION 'Il messaggio non può essere vuoto!'; END IF;
cont = 0;

RETURN NULL;
END;	

$checkEmptyStringMessage$ LANGUAGE PLPGSQL;

CREATE TRIGGER checkEmptyStringMessage AFTER INSERT ON MESSAGEDATA
FOR EACH ROW EXECUTE PROCEDURE checkEmptyStringMessage();
```

Il trigger verifica se vengano inseriti messaggi vuoti (spazi e tab); alza un'eccezione in caso affermativo.


--------------------------------------------------------------------------------------------


```SQL
CREATE OR REPLACE FUNCTION checkEmptyStringRoom() RETURNS TRIGGER AS $checkEmptyStringRoom$
DECLARE
checkroom ROOM.ROOMNAME%TYPE;
cont INTEGER := 0;
substr TEXT;
BEGIN
SELECT ROOM.ROOMNAME INTO checkroom
FROM ROOM
WHERE ROOM.ROOMNAME = NEW.ROOMNAME;

IF(LENGTH(checkroom) = 0) THEN RAISE EXCEPTION 'Il nome della stanza non può essere vuota!'; END IF;

FOR i IN 0 .. LENGTH(checkroom) LOOP
substr = right(checkroom, length(checkroom)-i);
IF (ascii(substr) = 32 OR ascii(substr) = 9) THEN cont = cont + 1; END IF;
END LOOP;

IF(cont = LENGTH(checkroom)) THEN RAISE EXCEPTION 'Il nome della stanza non può essere vuota!'; END IF;

RETURN NULL;
END;	

$checkEmptyStringRoom$ LANGUAGE PLPGSQL;

CREATE TRIGGER checkEmptyStringRoom AFTER INSERT ON ROOM
FOR EACH ROW EXECUTE PROCEDURE checkEmptyStringRoom();
```

Il trigger verifica se vengano inseriti nomi di stanze vuote (spazi e tab); alza un'eccezione in caso affermativo.


--------------------------------------------------------------------------------------------


```
CREATE OR REPLACE FUNCTION checkUserdataLength() RETURNS TRIGGER AS $checkUserdataLength$
DECLARE
usernamee USERDATA.USERNAME%TYPE;
userpass USERDATA.USERPASSWORD%TYPE;
BEGIN
IF(LENGTH(NEW.USERNAME) < 5) THEN
RAISE EXCEPTION 'Username inserito non valido.';
END IF;
IF(LENGTH(NEW.USERPASSWORD) < 5) THEN
RAISE EXCEPTION 'Password inserita non valida.';
END IF;
RETURN NULL;
END;	

$checkUserdataLength$ LANGUAGE PLPGSQL;

CREATE TRIGGER checkUserdataLength AFTER INSERT ON USERDATA
FOR EACH ROW EXECUTE PROCEDURE checkUserdataLength();
```

Trigger che controlla la lunghezza minima delle credenziali di un utente.

--------------------------------------------------------------------------------------------


```
CREATE OR REPLACE FUNCTION checkRoomNameLength() RETURNS TRIGGER AS $checkRoomNameLength$
BEGIN
IF(LENGTH(NEW.ROOMNAME) < 5) THEN
RAISE EXCEPTION 'Nome della stanza inserita non valida.';
END IF;

RETURN NULL;
END;	

$checkRoomNameLength$ LANGUAGE PLPGSQL;

CREATE TRIGGER checkRoomNameLength AFTER INSERT ON ROOM
FOR EACH ROW EXECUTE PROCEDURE checkRoomNameLength();
```


Trigger che controlla la lunghezza minima del nome di un gruppo.
