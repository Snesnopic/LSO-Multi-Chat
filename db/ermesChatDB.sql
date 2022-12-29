PGDMP     9    	                z            ErmesDB    14.5 (Ubuntu 14.5-1ubuntu1)    14.5 (Ubuntu 14.5-1ubuntu1) /    \           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
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
    public          postgres    false    211   R       V          0    24675 	   roomusers 
   TABLE DATA           3   COPY public.roomusers (userid, roomid) FROM stdin;
    public          postgres    false    212   �S       W          0    24678    userdata 
   TABLE DATA           B   COPY public.userdata (userid, username, userpassword) FROM stdin;
    public          postgres    false    213   KT       `           0    0    room_roomid_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.room_roomid_seq', 52, true);
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
       public          postgres    false    213    212    3254            S   ?   x�L��0z�0�M������q��fË7�Y.l��XCkJ5�0��=�ȳQ>���	�      T   �	  x��X�n��]�_Q3@�n��`_���D�5%nK�l2ȦD��BH�M�e�"�,'@~ ��O�KrnQ=�fy� nV����9W�J?i�v�-�R���(���x��[�2��`��֍9&��Z��F1]�N��%���o.|�3�x&�3�a6��w֖�}8��A��oy�x �YY�F$�M��Q�,
���t��g�'�(ŷ�$N�wc*��5�k�F�T�3��azY(�C�$G�A�O}��I�f��Ȍf[Ynu�٫�TW7umض7x�ǿ����8�	�gb£	�U!U�4�J�������3�tĳ��<�'�$�r��(4��<�Ǉ^���lž@�	�1�����V��r\�mc[��������ן�4Eeg
��v��w������ל�=�&<�L��9�F��4۩��t�k�����o׍:��Y��I��&�W�v�B��^��SoPvD�e����	m�{P�	�N2^��m��zj�
�O�}F��BU�K��E�֍9���Y\�\m�u�!f1��'�Β�^����u]ߡ�V>*x��0�t�C��J�C#��b����4%ċ�'٠+�Ӄ)5�#8{��(M���i���C�M�	�z��?N�y���2��t�������S��aH��s��E��#�#���c?:Ϫ�����_p��t"0aӷ��_��K]9�Ƴ �#�5���0e_}�= ZPn�=KVv�5��ɺV�:�J��;�rO�k�T8�������ݜ}�b�����n�.����/�n�77��f��[�gű�E�r<��/��~�q��jǶɌ'~Fs�dD�+���;�5���"R�a�h��ٍ�@K�I�;D�����8� ��+���S��[�&?>��c��OQ����!/�.C�7��z9�b7s���}�^ܲ������y"�q@R�)I��!<Ɂt�wi>���*�)�'�&	(���Fyߛf}(�����4Jáu�D�YL��h Y�f�Ac��3����l, X� cêCo@bc';p"ˆy�t��,h����������s���~'�̡0[Y`<�J�F�bwM?K8	qF���Jv���
�cb����%���(�bg�w㩟vl�Sp����'�����ٗK���|z���N~9��
>��"��h�����@1]=��S����d�=��8=GY-��̩QV(8�)� &X^Y�Gt����Uk���ā/ `(�n�1����B3	h��~'[ �91yH �(���ϴ����k0�N��Z�W%��r��!ű��'pؒ��`^V?vG�t���d2���ɭ)�,p�T�:��C�q6�2�`��_D���k�K|h�7�3L�Z��&aB��
���/�umA��?>"���n��q ���`Ѭ=��`�r`�DF9�(�7+��/:���'��	�0��w�:�D�%O��{��ZA�w���Iw�kMAb1S�����K�l��%"���gVde;ȓ�Y'S(�i4(�Fct�"��jP��v}�$Y��A�^�z���k#wE�-!% D�P�V@�����~�Aa�C5"��=���L$>��97�-8	��f���i�������`�#�Ȏ�r}��;F�\;� �	qS�yd����v��0��g2{��k�D��pM0D���L">
]Z����I'A���ѠGk��nS-6=�~�q��7���A�B5��G��v��VҶ:��6��iY���f��
�W�T�bH���S/<,��)�$+i��(��?/d�s	k%U��EXQp�β0�#�G_�	B���G�4N8���@�?�3���@Iqӣ�K��>���AD卽Vٔ�N8z�{��[�!�֝�>Ń������άA4�t��0k%M ՠ������M�f��b���=���"b��Q�>o�{�_k�G��d	�Mص�oHB���V��lT��J�XK�4�HY�)m��!j��F�%u5���@�xn�=�uZ#!H�ɰ��c�*݄L�NK:���#�����aL�e��]�v�vJ�]D\Z�"Z�y�GF��̑y���*�
V.A��Х��,\bP���D�A�Cb@ft&"̧:�ԩ7��-՟ǰrR ���O�@h`�(ua)�wE�O�D����F8&�*�� v��,u� �)�H��l��wlc*�R��"��o��'O'd{�.N��8��S0���89�?:c{iG��=;u2gvm����ah�3
�怰�ԔNRBR�1�	«շ4���+����/r�"�#�r\�ꮃ9��23&"ԣA'�/t���R��q�ߟ�\��A,l_n�i���!O���3^]ϱ��-d_ae�,�n�ws��C<��Pe��[_����o}�x��������������|=���Y���/��a�6��z��V`�9����Y��2��7���-���j��� �&�>��90Ŗ�t\i~�'���>��ƴ�e��+����<��Za��      U   f  x�M�Ko�0�ϳ�bsk/����8
q��C�� zad�% �I�}���˂���~3,P�h��
��V�������;��~g����}�k>��ES��ڶ��j��i��ծńJ�{w
:�������M�|1G���X�`n7�����[�<f$��&_us�CЎǂ�uo��S�X�L�sq\��P��L�*R��N�:}>�هYZ`��!������i�����J�[���dJ��:�*��R����OV�R�Y�h}2{'�۷��<�[e�W��_m��I�?�����}�,H�x����du�5��,�@�i���/(�g�I��^�����!k�M@���"��*��      V   �   x�%P�0:�0}A�ݥ��Q�	1��T����$�_��&��C	�Y�d)YZaRX1�\8��<`�+^��A�\�Op�/���=�Hϋ����Ee})�bE�&�8�`x���\�����*z�
�@+��'���0�!�|�H���d[��e��P6������ʸ��
����00      W   �  x�=Sˎ�F<��"9� K�L�'��1���X��ȅ�h���t������G~,�d�t���bU)���а�����C8cM�Ha)�H�M��u��Y�[-�z�z+zFqw��u��!�n����"l��A��3_�V�1Q��Akn�ꉚ0�\Ɵ�=�M����0!�T*-4bŸ�e��{��uA���QE�R�;��g��=�Ac���1Fi�j߼�`�lY]6�9�+��+�`�����3�e0�6����7������a1��iyػ,��L��F+,����Q����[��7t�y�S�|�O\v+F�HֆaA����`r�	>ܻl��K�Hj����0��ކ�؏%�=��Ͷ1
�hQ⎩Cӗb,Z�zV;|4bb��G�V�7z5�_o�Y߅��T�������'�O���AT���Bb����!9�\`��u�xҵ_�_�pyn�)�ްEa}
XՊ�|j��,_��~-���e<&�.��w7��&f���6�*����#_����r���BBPP�us�Θ.���a���
Ac5e>$M�P��e�k���_J���BE�N��K�M3��T8#K�t
�2��k8�+mn1v��c �����������M �|E� ��Q��f4�a5PId
m�3��NASK�߬_n��w����w�c�C|���in����%����7�ܑ�Ԇ�U�1���9#|������9�{�nD     