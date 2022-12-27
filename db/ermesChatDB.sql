PGDMP     '                    z            ErmesDB    14.5 (Ubuntu 14.5-1ubuntu1)    14.5 (Ubuntu 14.5-1ubuntu1) +    X           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            Y           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            Z           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            [           1262    24657    ErmesDB    DATABASE     ^   CREATE DATABASE "ErmesDB" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'it_IT.UTF-8';
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
    START WITH 32
    INCREMENT BY 1
    MINVALUE 32
    MAXVALUE 50000
    CACHE 1
);
            public          postgres    false    213            O          0    24664    joinrequest 
   TABLE DATA           5   COPY public.joinrequest (userid, roomid) FROM stdin;
    public          postgres    false    209   CA       P          0    24667    messagedata 
   TABLE DATA           Q   COPY public.messagedata (messagetext, timestampdata, userid, roomid) FROM stdin;
    public          postgres    false    210   �A       Q          0    24672    room 
   TABLE DATA           ?   COPY public.room (creatoruserid, roomname, roomid) FROM stdin;
    public          postgres    false    211   �K       R          0    24675 	   roomusers 
   TABLE DATA           3   COPY public.roomusers (userid, roomid) FROM stdin;
    public          postgres    false    212   �L       S          0    24678    userdata 
   TABLE DATA           B   COPY public.userdata (userid, username, userpassword) FROM stdin;
    public          postgres    false    213   �M       \           0    0    room_roomid_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.room_roomid_seq', 51, false);
          public          postgres    false    215            ]           0    0    userdata_userid_seq    SEQUENCE SET     B   SELECT pg_catalog.setval('public.userdata_userid_seq', 34, true);
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
       public          postgres    false    217    210            �           2620    24697    room checkemptystringroom    TRIGGER     }   CREATE TRIGGER checkemptystringroom AFTER INSERT ON public.room FOR EACH ROW EXECUTE FUNCTION public.checkemptystringroom();
 2   DROP TRIGGER checkemptystringroom ON public.room;
       public          postgres    false    218    211            �           2620    24698    userdata checkemptystringuser    TRIGGER     �   CREATE TRIGGER checkemptystringuser AFTER INSERT ON public.userdata FOR EACH ROW EXECUTE FUNCTION public.checkemptystringuser();
 6   DROP TRIGGER checkemptystringuser ON public.userdata;
       public          postgres    false    222    213            �           2620    24699    messagedata existsuser    TRIGGER     p   CREATE TRIGGER existsuser AFTER INSERT ON public.messagedata FOR EACH ROW EXECUTE FUNCTION public.existsuser();
 /   DROP TRIGGER existsuser ON public.messagedata;
       public          postgres    false    210    231            �           2620    24700    joinrequest verify_userinroom    TRIGGER     ~   CREATE TRIGGER verify_userinroom AFTER INSERT ON public.joinrequest FOR EACH ROW EXECUTE FUNCTION public.verify_userinroom();
 6   DROP TRIGGER verify_userinroom ON public.joinrequest;
       public          postgres    false    209    232            �           2606    24701 #   joinrequest joinrequest_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_roomid_fkey;
       public          postgres    false    209    211    3248            �           2606    24706 #   joinrequest joinrequest_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_userid_fkey;
       public          postgres    false    213    3252    209            �           2606    24711 #   messagedata messagedata_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_roomid_fkey;
       public          postgres    false    210    211    3248            �           2606    24716 #   messagedata messagedata_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_userid_fkey;
       public          postgres    false    210    213    3252            �           2606    24721    room room_creatoruserid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_creatoruserid_fkey FOREIGN KEY (creatoruserid) REFERENCES public.userdata(userid);
 F   ALTER TABLE ONLY public.room DROP CONSTRAINT room_creatoruserid_fkey;
       public          postgres    false    3252    211    213            �           2606    24726    roomusers roomusers_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_roomid_fkey;
       public          postgres    false    212    3248    211            �           2606    24731    roomusers roomusers_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_userid_fkey;
       public          postgres    false    212    3252    213            O   ?   x�L��0z�0�M������q��fË7�Y.l��XCkJ5�0��=�ȳQ>���	�      P   �	  x��X�n��]�_Q3@�n��`_���D�5%nK�l2ȦD��BH�M�e�"�,'@~ ��O�KrnQ=�fy� nV����9W�J?i�v�-�R���(���x��[�2��`��֍9&��Z��F1]�N��%���o.|�3�x&�3�a6��w֖�}8��A��oy�x �YY�F$�M��Q�,
���t��g�'�(ŷ�$N�wc*��5�k�F�T�3��azY(�C�$G�A�O}��I�f��Ȍf[Ynu�٫�TW7umض7x�ǿ����8�	�gb£	�U!U�4�J�������3�tĳ��<�'�$�r��(4��<�Ǉ^���lž@�	�1�����V��r\�mc[��������ן�4Eeg
��v��w������ל�=�&<�L��9�F��4۩��t�k�����o׍:��Y��I��&�W�v�B��^��SoPvD�e����	m�{P�	�N2^��m��zj�
�O�}F��BU�K��E�֍9���Y\�\m�u�!f1��'�Β�^����u]ߡ�V>*x��0�t�C��J�C#��b����4%ċ�'٠+�Ӄ)5�#8{��(M���i���C�M�	�z��?N�y���2��t�������S��aH��s��E��#�#���c?:Ϫ�����_p��t"0aӷ��_��K]9�Ƴ �#�5���0e_}�= ZPn�=KVv�5��ɺV�:�J��;�rO�k�T8�������ݜ}�b�����n�.����/�n�77��f��[�gű�E�r<��/��~�q��jǶɌ'~Fs�dD�+���;�5���"R�a�h��ٍ�@K�I�;D�����8� ��+���S��[�&?>��c��OQ����!/�.C�7��z9�b7s���}�^ܲ������y"�q@R�)I��!<Ɂt�wi>���*�)�'�&	(���Fyߛf}(�����4Jáu�D�YL��h Y�f�Ac��3����l, X� cêCo@bc';p"ˆy�t��,h����������s���~'�̡0[Y`<�J�F�bwM?K8	qF���Jv���
�cb����%���(�bg�w㩟vl�Sp����'�����ٗK���|z���N~9��
>��"��h�����@1]=��S����d�=��8=GY-��̩QV(8�)� &X^Y�Gt����Uk���ā/ `(�n�1����B3	h��~'[ �91yH �(���ϴ����k0�N��Z�W%��r��!ű��'pؒ��`^V?vG�t���d2���ɭ)�,p�T�:��C�q6�2�`��_D���k�K|h�7�3L�Z��&aB��
���/�umA��?>"���n��q ���`Ѭ=��`�r`�DF9�(�7+��/:���'��	�0��w�:�D�%O��{��ZA�w���Iw�kMAb1S�����K�l��%"���gVde;ȓ�Y'S(�i4(�Fct�"��jP��v}�$Y��A�^�z���k#wE�-!% D�P�V@�����~�Aa�C5"��=���L$>��97�-8	��f���i�������`�#�Ȏ�r}��;F�\;� �	qS�yd����v��0��g2{��k�D��pM0D���L">
]Z����I'A���ѠGk��nS-6=�~�q��7���A�B5��G��v��VҶ:��6��iY���f��
�W�T�bH���S/<,��)�$+i��(��?/d�s	k%U��EXQp�β0�#�G_�	B���G�4N8���@�?�3���@Iqӣ�K��>���AD卽Vٔ�N8z�{��[�!�֝�>Ń������άA4�t��0k%M ՠ������M�f��b���=���"b��Q�>o�{�_k�G��d	�Mص�oHB���V��lT��J�XK�4�HY�)m��!j��F�%u5���@�xn�=�uZ#!H�ɰ��c�*݄L�NK:���#�����aL�e��]�v�vJ�]D\Z�"Z�y�GF��̑y���*�
V.A��Х��,\bP���D�A�Cb@ft&"̧:�ԩ7��-՟ǰrR ���O�@h`�(ua)�wE�O�D����F8&�*�� v��,u� �)�H��l��wlc*�R��"��o��'O'd{�.N��8��S0���89�?:c{iG��=;u2gvm����ah�3
�怰�ԔNRBR�1�	«շ4���+����/r�"�#�r\�ꮃ9��23&"ԣA'�/t���R��q�ߟ�\��A,l_n�i���!O���3^]ϱ��-d_ae�,�n�ws��C<��Pe��[_����o}�x��������������|=���Y���/��a�6��z��V`�9����Y��2��7���-���j��� �&�>��90Ŗ�t\i~�'���>��ƴ�e��+����<��Za��      Q   S  x�M�Ko�@���_�ܒK!�-?�BܨF�P#7H�^6��! �
+9h��J.�	��E�kõCF�]�\ۮS{	\�^�����7�'�4���1�=Mq��k]�������?��q�)����bJl�_��w��;*r�c͝{w~P>*om�s���H?WU��{�I�z$��r�Q�$�����8�Bexs1�HF3Tv���9�>͊��z1$9�WO��gȄd�N2�j�Nz�)����P�oĖ����~�Ɍ��RD�K��S8)�ݾ���̓�U��x}��q��E��M�m��g���dA&ǃ��'�	�5��զ*�*�� ���"��܀      R   �   x�%PK1Z�a���{�����!F�̈
)�0�-2���!h���0��ZEʖ���SŅ��f\�����$n0�,�'���U�K�����T��Jj��آ�e��w��"�ק֫��(�"=�#=���M�ǧ�d�7]�(���t��A���t|�*�� ?��/U      S   �  x�-Sݎ�D��<\p5��!�E7��*���8�Ɲ��ى�L��><�y1�eEJ4����3�g�ˉu�4K�5�S���Ŀ���̕PZ�Ͷ�}i�G��O��v�=k`�,����
zBq7������!�џ�.��}x$l���Z/G�XU�)S�9�Q=���Dmd&�w��#.���f��J��&l��>���ȃ�S�Ȟ!��^��Z�s~���$6.lZ���#:�&d�ꊉ�al4Z74�� sGM �o]Q���h�Fg��x�
�8���}���m]QBs���������&2�5ƈ���J�>�����W�XFh��0̩S񍱊f�����+na���}���g����w&��0
�����me��pFI�E�_J�:c׳��;ɐ*�!��l=��bm��f}7zs�o�mQNo\9��l�eDeMmI��,`Z��	6�Mg�g]�����,�kΤ�ޢ�<Dl�bǮ�ZD(f˗�_�yB����+o�=M}��Y�d�C���p6�6�q�0]iv��H*�^W{��<_��u|b�����n|��]iS�u<����Z��55�^fC�L-%oP�Fmq��?�nW(�8D�t��n��7��X��<`�T�|6�_�`�8w�-L��LKH�1__���M!�nH�ሯ�N�Mma�n#E�Tht���\tZ�     