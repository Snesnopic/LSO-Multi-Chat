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
