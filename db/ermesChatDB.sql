PGDMP                     
    z         	   ErmesChat    15.1    15.1                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                        0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            !           1262    16398 	   ErmesChat    DATABASE     ~   CREATE DATABASE "ErmesChat" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Italian_Italy.1252';
    DROP DATABASE "ErmesChat";
                postgres    false            �            1255    16474    aggiungi_creatoreroom()    FUNCTION     y  CREATE FUNCTION public.aggiungi_creatoreroom() RETURNS trigger
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
       public          postgres    false            �            1255    16477    verify_userinroom()    FUNCTION     G  CREATE FUNCTION public.verify_userinroom() RETURNS trigger
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
       public          postgres    false            �            1259    16445    joinrequest    TABLE     ^   CREATE TABLE public.joinrequest (
    userid integer NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.joinrequest;
       public         heap    postgres    false            �            1259    16428    messagedata    TABLE     �   CREATE TABLE public.messagedata (
    messagetext character varying(10000),
    timestampdata timestamp without time zone NOT NULL,
    userid integer NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.messagedata;
       public         heap    postgres    false            �            1259    16418    room    TABLE     z   CREATE TABLE public.room (
    creatoruserid integer,
    roomname character varying(200),
    roomid integer NOT NULL
);
    DROP TABLE public.room;
       public         heap    postgres    false            �            1259    16458 	   roomusers    TABLE     \   CREATE TABLE public.roomusers (
    userid integer NOT NULL,
    roomid integer NOT NULL
);
    DROP TABLE public.roomusers;
       public         heap    postgres    false            �            1259    16399    userdata    TABLE     �   CREATE TABLE public.userdata (
    userid integer NOT NULL,
    username character varying(1000),
    userpassword character varying(1000)
);
    DROP TABLE public.userdata;
       public         heap    postgres    false                      0    16445    joinrequest 
   TABLE DATA           5   COPY public.joinrequest (userid, roomid) FROM stdin;
    public          postgres    false    217   &                 0    16428    messagedata 
   TABLE DATA           Q   COPY public.messagedata (messagetext, timestampdata, userid, roomid) FROM stdin;
    public          postgres    false    216   9&                 0    16418    room 
   TABLE DATA           ?   COPY public.room (creatoruserid, roomname, roomid) FROM stdin;
    public          postgres    false    215   V&                 0    16458 	   roomusers 
   TABLE DATA           3   COPY public.roomusers (userid, roomid) FROM stdin;
    public          postgres    false    218   �'                 0    16399    userdata 
   TABLE DATA           B   COPY public.userdata (userid, username, userpassword) FROM stdin;
    public          postgres    false    214   e(       }           2606    16483    joinrequest joinrequest_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_pkey PRIMARY KEY (userid, roomid);
 F   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_pkey;
       public            postgres    false    217    217            {           2606    16434    messagedata messagedata_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_pkey PRIMARY KEY (userid, roomid, timestampdata);
 F   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_pkey;
       public            postgres    false    216    216    216            y           2606    16422    room room_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (roomid);
 8   ALTER TABLE ONLY public.room DROP CONSTRAINT room_pkey;
       public            postgres    false    215                       2606    16462    roomusers roomusers_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_pkey PRIMARY KEY (userid, roomid);
 B   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_pkey;
       public            postgres    false    218    218            w           2606    16405    userdata userdata_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.userdata
    ADD CONSTRAINT userdata_pkey PRIMARY KEY (userid);
 @   ALTER TABLE ONLY public.userdata DROP CONSTRAINT userdata_pkey;
       public            postgres    false    214            �           2620    16475    room aggiungi_creatoreroom    TRIGGER        CREATE TRIGGER aggiungi_creatoreroom AFTER INSERT ON public.room FOR EACH ROW EXECUTE FUNCTION public.aggiungi_creatoreroom();
 3   DROP TRIGGER aggiungi_creatoreroom ON public.room;
       public          postgres    false    219    215            �           2620    16479    joinrequest verify_userinroom    TRIGGER     ~   CREATE TRIGGER verify_userinroom AFTER INSERT ON public.joinrequest FOR EACH ROW EXECUTE FUNCTION public.verify_userinroom();
 6   DROP TRIGGER verify_userinroom ON public.joinrequest;
       public          postgres    false    220    217            �           2606    16453 #   joinrequest joinrequest_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_roomid_fkey;
       public          postgres    false    3193    217    215            �           2606    16448 #   joinrequest joinrequest_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_userid_fkey;
       public          postgres    false    3191    217    214            �           2606    16440 #   messagedata messagedata_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_roomid_fkey;
       public          postgres    false    215    3193    216            �           2606    16435 #   messagedata messagedata_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_userid_fkey;
       public          postgres    false    216    3191    214            �           2606    16423    room room_creatoruserid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_creatoruserid_fkey FOREIGN KEY (creatoruserid) REFERENCES public.userdata(userid);
 F   ALTER TABLE ONLY public.room DROP CONSTRAINT room_creatoruserid_fkey;
       public          postgres    false    215    3191    214            �           2606    16468    roomusers roomusers_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_roomid_fkey;
       public          postgres    false    3193    215    218            �           2606    16463    roomusers roomusers_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_userid_fkey;
       public          postgres    false    218    3191    214                  x�3�4����� T            x������ � �         D  x�M��n�@E��W�]���	᱌����J(�R7C��0MB������˲��soi]K�ݥ��R��J�����r�m^uQ�V���k{P؅��s��k����rK�t�C�	��=zݘ��b3������t6��0��\��6B���r*E[=d���*|�5��l��� ǐ�Bi�x�!r�}W�Ǯ�ܷ�A�#��J7�X}:���,3`�/�r��v=����p�\t��"a��*�؜�bȖ�R�R����\�N�i���f��`o8�_��*���o��{��1��#� U)]펢�)4�?����{�         �   x�%P� ���I�K����~��0�
)�0;���!���0��:��,%K<!����m�3.X����$n0�,�'���uۋ�����7z��/�4,��@c���	����E�O���s*�"=�-=	���#���6���t�����m*]&��i�bU� ?Wc/+         �  x�-�M��F�׬[d1�	Xr��"��n�_�� ̆.�6'%R�*9�>I��9G_(G���FB����=����|d���:M�)��)���v:ql������A�*!�Y�)��J���j]�����z�%�3��E7�-{�X����z)z@qW�����>����΀�>�6�]�F�>[W�)S�)o�zn���D�w��#����3f��J��&��as���B��{:��=CRѝl�K�N�[rw�UcPs8���&"�H�m�%�� K������X����ي!�F�F9Z/dn�Dݵ+
���ٔ��/�B��@���g۵+J�q��c�ª︱�ņ�M@��1�lY^h�����#��r�+�M=��)��#>3V�H�	�޸��*����x�A;j�\qcZ��H�<�n� ���JZ1�(b�)��mg�pŝ	;J�*�!��[�rX�ٮ�������3�il�r|�ʑ}g{�� *K
h��ue3�-[�#�֫�u�O���ؑI/w��|���-�rl���w�q7c�&��>W^���z��Q���4�E�l�V��73]i�X�$E��������ﰌ,6��§OЕ6������:Pi���~'�>k����Rc�x��J�B!7�>*�?[�>�����M�     