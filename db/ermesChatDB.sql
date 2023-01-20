PGDMP         ;                 {            ErmesDB    14.5 (Ubuntu 14.5-1ubuntu1)    14.5 (Ubuntu 14.5-1ubuntu1) /    \           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            ]           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            ^           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            _           1262    24657    ErmesDB    DATABASE     ^   CREATE DATABASE "ErmesDB" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'it_IT.UTF-8';
    DROP DATABASE "ErmesDB";
                postgres    false            �            1255    24658    aggiungi_creatoreroom()    FUNCTION     y  CREATE FUNCTION public.aggiungi_creatoreroom() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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

$$;
 .   DROP FUNCTION public.aggiungi_creatoreroom();
       public          postgres    false            �            1255    24659    checkemptystringmessage()    FUNCTION     �  CREATE FUNCTION public.checkemptystringmessage() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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

$$;
 0   DROP FUNCTION public.checkemptystringmessage();
       public          postgres    false            �            1255    24660    checkemptystringroom()    FUNCTION     �  CREATE FUNCTION public.checkemptystringroom() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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

$$;
 -   DROP FUNCTION public.checkemptystringroom();
       public          postgres    false            �            1255    24661    checkemptystringuser()    FUNCTION     �  CREATE FUNCTION public.checkemptystringuser() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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

$$;
 -   DROP FUNCTION public.checkemptystringuser();
       public          postgres    false            �            1255    24742    checkroomnamelength()    FUNCTION     �   CREATE FUNCTION public.checkroomnamelength() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
IF(LENGTH(NEW.ROOMNAME) < 5) THEN
RAISE EXCEPTION 'Nome della stanza inserita non valida.';
END IF;

RETURN NULL;
END;	

$$;
 ,   DROP FUNCTION public.checkroomnamelength();
       public          postgres    false            �            1255    24739    checkuserdatalength()    FUNCTION     �  CREATE FUNCTION public.checkuserdatalength() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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

$$;
 ,   DROP FUNCTION public.checkuserdatalength();
       public          postgres    false            �            1255    24662    existsuser()    FUNCTION     �  CREATE FUNCTION public.existsuser() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
useriid USERDATA.USERID%TYPE;
roomiid ROOM.ROOMID%TYPE;
usernamee USERDATA.USERNAME%TYPE;
BEGIN
SELECT MESSAGEDATA.USERID, MESSAGEDATA.ROOMID INTO useriid, roomiid
FROM MESSAGEDATA
WHERE MESSAGEDATA.USERID = NEW.USERID AND MESSAGEDATA.ROOMID = NEW.ROOMID;

IF NOT EXISTS(SELECT* FROM ROOMUSERS WHERE ROOMUSERS.USERID = useriid AND ROOMUSERS.roomid = roomiid) THEN
SELECT USERNAME INTO usernamee FROM USERDATA WHERE USERID = useriid;
RAISE EXCEPTION '% non è presente nella stanza!', usernamee;
DELETE FROM MESSAGEDATA WHERE MESSAGEDATA.USERID = useriid AND MESSAGEDATA.ROOMID = NEW.ROOMID;
END IF;

RETURN NULL;
END;	

$$;
 #   DROP FUNCTION public.existsuser();
       public          postgres    false            �            1255    24663    verify_userinroom()    FUNCTION     G  CREATE FUNCTION public.verify_userinroom() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
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
$$;
 *   DROP FUNCTION public.verify_userinroom();
       public          postgres    false            �            1259    24664    joinrequest    TABLE     ^   CREATE TABLE public.joinrequest (
    userid integer NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.joinrequest;
       public         heap    postgres    false            �            1259    24667    messagedata    TABLE     �   CREATE TABLE public.messagedata (
    messagetext character varying(100000) NOT NULL,
    timestampdata timestamp without time zone NOT NULL,
    userid integer NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.messagedata;
       public         heap    postgres    false            �            1259    24672    room    TABLE     �   CREATE TABLE public.room (
    creatoruserid integer NOT NULL,
    roomname character varying(200) NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.room;
       public         heap    postgres    false            �            1259    24738    room_roomid_seq    SEQUENCE     �   ALTER TABLE public.room ALTER COLUMN roomid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.room_roomid_seq
    START WITH 51
    INCREMENT BY 1
    MINVALUE 51
    MAXVALUE 50000
    CACHE 1
);
            public          postgres    false    211            �            1259    24675 	   roomusers    TABLE     \   CREATE TABLE public.roomusers (
    userid integer NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.roomusers;
       public         heap    postgres    false            �            1259    24678    userdata    TABLE     �   CREATE TABLE public.userdata (
    userid integer NOT NULL,
    username character varying(1000) NOT NULL,
    userpassword character varying(1000) NOT NULL
);
    DROP TABLE public.userdata;
       public         heap    postgres    false            �            1259    24737    userdata_userid_seq    SEQUENCE     �   ALTER TABLE public.userdata ALTER COLUMN userid ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.userdata_userid_seq
    START WITH 34
    INCREMENT BY 1
    MINVALUE 34
    MAXVALUE 50000
    CACHE 1
);
            public          postgres    false    213            S          0    24664    joinrequest 
   TABLE DATA           5   COPY public.joinrequest (userid, roomid) FROM stdin;
    public          postgres    false    209   �G       T          0    24667    messagedata 
   TABLE DATA           Q   COPY public.messagedata (messagetext, timestampdata, userid, roomid) FROM stdin;
    public          postgres    false    210   H       U          0    24672    room 
   TABLE DATA           ?   COPY public.room (creatoruserid, roomname, roomid) FROM stdin;
    public          postgres    false    211   �P       V          0    24675 	   roomusers 
   TABLE DATA           3   COPY public.roomusers (userid, roomid) FROM stdin;
    public          postgres    false    212   uR       W          0    24678    userdata 
   TABLE DATA           B   COPY public.userdata (userid, username, userpassword) FROM stdin;
    public          postgres    false    213   &S       `           0    0    room_roomid_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.room_roomid_seq', 52, true);
          public          postgres    false    215            a           0    0    userdata_userid_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.userdata_userid_seq', 50, true);
          public          postgres    false    214            �           2606    24684    joinrequest joinrequest_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_pkey PRIMARY KEY (userid, roomid);
 F   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_pkey;
       public            postgres    false    209    209            �           2606    24686    messagedata messagedata_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_pkey PRIMARY KEY (userid, roomid, timestampdata);
 F   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_pkey;
       public            postgres    false    210    210    210            �           2606    24688    room room_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (roomid);
 8   ALTER TABLE ONLY public.room DROP CONSTRAINT room_pkey;
       public            postgres    false    211            �           2606    24690    roomusers roomusers_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_pkey PRIMARY KEY (userid, roomid);
 B   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_pkey;
       public            postgres    false    212    212            �           2606    24692    userdata userdata_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.userdata
    ADD CONSTRAINT userdata_pkey PRIMARY KEY (userid);
 @   ALTER TABLE ONLY public.userdata DROP CONSTRAINT userdata_pkey;
       public            postgres    false    213            �           2606    24694    userdata userdata_username_key 
   CONSTRAINT     ]   ALTER TABLE ONLY public.userdata
    ADD CONSTRAINT userdata_username_key UNIQUE (username);
 H   ALTER TABLE ONLY public.userdata DROP CONSTRAINT userdata_username_key;
       public            postgres    false    213            �           2620    24695    room aggiungi_creatoreroom    TRIGGER        CREATE TRIGGER aggiungi_creatoreroom AFTER INSERT ON public.room FOR EACH ROW EXECUTE FUNCTION public.aggiungi_creatoreroom();
 3   DROP TRIGGER aggiungi_creatoreroom ON public.room;
       public          postgres    false    216    211            �           2620    24696 #   messagedata checkemptystringmessage    TRIGGER     �   CREATE TRIGGER checkemptystringmessage AFTER INSERT ON public.messagedata FOR EACH ROW EXECUTE FUNCTION public.checkemptystringmessage();
 <   DROP TRIGGER checkemptystringmessage ON public.messagedata;
       public          postgres    false    219    210            �           2620    24697    room checkemptystringroom    TRIGGER     }   CREATE TRIGGER checkemptystringroom AFTER INSERT ON public.room FOR EACH ROW EXECUTE FUNCTION public.checkemptystringroom();
 2   DROP TRIGGER checkemptystringroom ON public.room;
       public          postgres    false    220    211            �           2620    24698    userdata checkemptystringuser    TRIGGER     �   CREATE TRIGGER checkemptystringuser AFTER INSERT ON public.userdata FOR EACH ROW EXECUTE FUNCTION public.checkemptystringuser();
 6   DROP TRIGGER checkemptystringuser ON public.userdata;
       public          postgres    false    213    224            �           2620    24743    room checkroomnamelength    TRIGGER     {   CREATE TRIGGER checkroomnamelength AFTER INSERT ON public.room FOR EACH ROW EXECUTE FUNCTION public.checkroomnamelength();
 1   DROP TRIGGER checkroomnamelength ON public.room;
       public          postgres    false    217    211            �           2620    24740    userdata checkuserdatalength    TRIGGER        CREATE TRIGGER checkuserdatalength AFTER INSERT ON public.userdata FOR EACH ROW EXECUTE FUNCTION public.checkuserdatalength();
 5   DROP TRIGGER checkuserdatalength ON public.userdata;
       public          postgres    false    213    218            �           2620    24699    messagedata existsuser    TRIGGER     p   CREATE TRIGGER existsuser AFTER INSERT ON public.messagedata FOR EACH ROW EXECUTE FUNCTION public.existsuser();
 /   DROP TRIGGER existsuser ON public.messagedata;
       public          postgres    false    210    233            �           2620    24700    joinrequest verify_userinroom    TRIGGER     ~   CREATE TRIGGER verify_userinroom AFTER INSERT ON public.joinrequest FOR EACH ROW EXECUTE FUNCTION public.verify_userinroom();
 6   DROP TRIGGER verify_userinroom ON public.joinrequest;
       public          postgres    false    234    209            �           2606    24701 #   joinrequest joinrequest_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_roomid_fkey;
       public          postgres    false    209    3250    211            �           2606    24706 #   joinrequest joinrequest_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_userid_fkey;
       public          postgres    false    3254    209    213            �           2606    24711 #   messagedata messagedata_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_roomid_fkey;
       public          postgres    false    3250    211    210            �           2606    24716 #   messagedata messagedata_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_userid_fkey;
       public          postgres    false    3254    213    210            �           2606    24721    room room_creatoruserid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_creatoruserid_fkey FOREIGN KEY (creatoruserid) REFERENCES public.userdata(userid);
 F   ALTER TABLE ONLY public.room DROP CONSTRAINT room_creatoruserid_fkey;
       public          postgres    false    3254    213    211            �           2606    24726    roomusers roomusers_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_roomid_fkey;
       public          postgres    false    3250    212    211            �           2606    24731    roomusers roomusers_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_userid_fkey;
       public          postgres    false    213    212    3254            S   ?   x�L��0z�0�M������q��fË7�Y.l��XCkJ5�0��=�ȳQ>���	�      T   �  x�uX�r��>O1vUR�U�(�����"�2�Ȉ�\q�2��T ?t��<E�9�U~��7�����Y�-a��AOw?�F$+L����N��a���t��DB���=OOfQ�o?I&"�p��Ԭ5�8j�(��>�zg��>��T������F�O}��N�f���0Ͷ���Z�7���s:��>��_�};�(Ng�����h��QժѲ2�F��6��V���tĳ��<�'�$�
��N��^">�z�>�fQ�;���kO
����#;��钕z���AT���p���e����� ����8v��#�L-�N*O��˩��l����;\�����%�f;U���Q������~���Q3ދc;?	��$�j��S��+�t�:��h���>�8���r7�B?A��I�!�Ux�-⾑�ł���Ӊ�>�f]�.�e��M��sP]g�����|�C�b��O�%��{����u}�)�G�Qv��p~�NBd1���%;4��)V**��B<|�$L%jz0�FNʁ�����[�9'1�~ȳI:�S�O/h��i�>B%�0���Z�� I95?�)�}�ғDӨ�8D�|�F�؏.Xަo��߳+SH];�ǳ �#�)!]���?��@L��H����7�ڂT�L�N
U�o�����ܝE��(=vg̗˜=.�s�����V����]/���=�����9[,�U���+w�8��(�����p�f[�//KՎ�&3�����0�� z{0�12x0�&��<����EP^��8,{D�8'�֪)��?X�4�=�-�S#��Pa�\�q�X����������z~Ǯ������F>�`��ӹ89K&_Ƌ ���T9�"�����҄�}o�a����NAB�(�i ��T(=��fX��[�G�Y�M W��(ނ�Vz�YL�,F��']x&X�em�I+���s^���~'��K�k��̺�Ќ0�a��@��ZR^(�	5U�µn`<�S������R@h�\�A�H6�Aڞ}��/�u����B���c��xƧ�T����~�wf!�ԃ)5�$,0tJ���IAt�J�Μee١o�&$H1���:b�{�{G��p7&|cx!�<ّJ�Y��!sf.�Ieq�(���?.0�	,WM{�V2!1�w�b�b[AD�������=d�����q�=��I�V�<�5��Zb��:�"�n�~g�@�-�@_���ȘA,�\C�h��0J}��T���Ţ��_�!��BU0�8�x���,��6M�.�J�����,���Eh�!2���.7+��/;��f"&��	�,��;U��*a��^I�%Ȳ!�l]>�R!���4$�јb�'��O�����fiF1D,{�BBC�C����B�@/��IƋ8�n��Y�.��C<?�+V����]�����w�H�L�R�ybo�[ o�Ũ<Ǧ(~f��^I�F+N�4A���z<�B�a�b�ȬZυ	��YqO�F�K:��v��j[�@�X�3(1�lnd{/C�� ���X���w{(hF�0����=@�+�9~���ʺ�W^g���-�Z��X��wq��v/ᓠib,w$l�=a�!cx4��a��8x��oz��J�䇵�"��7��*[�=�BY��A"�� �~�ʿ�-9�yQ"%M�+����t�`�+�$h�`���X��=�f�p��vkQ	���A��u�o/��}(���ɛ'���#}���;��t�24r�eO����w5i�Ɖ@y���eM`�a���odU�H�R���yܣ���ׄ.#X2����1��(��tM"1��(0�D��l���<^�l��3dw�����#�����2ǖ`� �V>kx�BB8T�+�t-��2y8����v�1��$�|]��R��:�f�f����!'���pZ�l�z^��zy�"$D2��v� l�Mg� ��ZV�p� �Ӕ8 �j���<�Q�A���mĈ R�%��8i������V��}���\�Q�q	C�5Q8��f��O�?4
A`s�r=���W )����̘�P��G\��!n�����~�r�E� �m�z�C��Q!��9.���\I6���>w�����#E�=ܲ�2�+����~.7�;ܼ�_mV���ܒ�f���<.>`-�\���
�?�@�9�C��]����E��2?O�%1C����?^�Ccr�Yh{ν�������B      U   o  x�M�Ko�0�ϻ�bsk/����8q��C�� zY˴C@"�6Z����K.�K�|3�੒�s�*������|�kG'��U#t�����gUѯEU.�w����V0��?EI�i�qk�/����Sk�r�:{�Y�m$�s��� 3�,Ku�=L��x�!��h-�̑ǃ]?|�P�6�
g8�RRr^�g�1\:`F��m���y!O�eF��*<F��*���X-�h���7����6�j;E�N����zZO5�R�)^-5��������5�d��������Ї�e�� �1�r]�J{+-�r}�媌�*{�<�������?;�\�l�h�Y���F0��"�菐      V   �   x�%��0ߨ��>{I�uD"�1�!�A�Dӷa�PE��)evPX1�\8��y��V��1���&����_p!�j{�Q?/��F��K)K�:P����e����������*z�
W���S���pDCތl�9���e��(]��}Q]���y,�      W   �  x�=Sˎ�F<��"9� K�L�'��1���X��ȅ�h���t������G~,�d�t���bU)���а�����C8cM�Ha)�H�M��u��Y�[-�z�z+zFqw��u��!�n����"l��A��3_�V�1Q��Akn�ꉚ0�\Ɵ�=�M����0!�T*-4bŸ�e��{��uA���QE�R�;��g��=�Ac���1Fi�j߼�`�lY]6�9�+��+�`�����3�e0�6����7������a1��iyػ,��L��F+,����Q����[��7t�y�S�|�O\v+F�HֆaA����`r�	>ܻl��K�Hj����0��ކ�؏%�=��Ͷ1
�hQ⎩Cӗb,Z�zV;|4bb��G�V�7z5�_o�Y߅��T�������'�O���AT���Bb����!9�\`��u�xҵ_�_�pyn�)�ްEa}
XՊ�|j��,_��~-���e<&�.��w7��&f���6�*����#_����r���BBPP�us�Θ.���a���
Ac5e>$M�P��e�k���_J���BE�N��K�M3��T8#K�t
�2��k8�+mn1v��c �����������M �|E� ��Q��f4�a5PId
m�3��NASK�߬_n��w����w�c�C|���in����%����7�ܑ�Ԇ�U�1���9#|������9�{�nD     