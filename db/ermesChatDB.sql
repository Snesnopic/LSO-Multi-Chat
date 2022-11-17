PGDMP     3    #            
    z         	   ErmesChat    15.1    15.1                  0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false            !           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false            "           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false            #           1262    16398 	   ErmesChat    DATABASE     ~   CREATE DATABASE "ErmesChat" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Italian_Italy.1252';
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
       public          postgres    false            �            1255    16485    existsuser()    FUNCTION     �  CREATE FUNCTION public.existsuser() RETURNS trigger
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
    messagetext character varying(100000),
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
       public         heap    postgres    false                      0    16445    joinrequest 
   TABLE DATA           5   COPY public.joinrequest (userid, roomid) FROM stdin;
    public          postgres    false    217   �*                 0    16428    messagedata 
   TABLE DATA           Q   COPY public.messagedata (messagetext, timestampdata, userid, roomid) FROM stdin;
    public          postgres    false    216   $+                 0    16418    room 
   TABLE DATA           ?   COPY public.room (creatoruserid, roomname, roomid) FROM stdin;
    public          postgres    false    215   <5                 0    16458 	   roomusers 
   TABLE DATA           3   COPY public.roomusers (userid, roomid) FROM stdin;
    public          postgres    false    218   �6                 0    16399    userdata 
   TABLE DATA           B   COPY public.userdata (userid, username, userpassword) FROM stdin;
    public          postgres    false    214   K7       ~           2606    16483    joinrequest joinrequest_pkey 
   CONSTRAINT     f   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_pkey PRIMARY KEY (userid, roomid);
 F   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_pkey;
       public            postgres    false    217    217            |           2606    16434    messagedata messagedata_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_pkey PRIMARY KEY (userid, roomid, timestampdata);
 F   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_pkey;
       public            postgres    false    216    216    216            z           2606    16422    room room_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (roomid);
 8   ALTER TABLE ONLY public.room DROP CONSTRAINT room_pkey;
       public            postgres    false    215            �           2606    16462    roomusers roomusers_pkey 
   CONSTRAINT     b   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_pkey PRIMARY KEY (userid, roomid);
 B   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_pkey;
       public            postgres    false    218    218            x           2606    16405    userdata userdata_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.userdata
    ADD CONSTRAINT userdata_pkey PRIMARY KEY (userid);
 @   ALTER TABLE ONLY public.userdata DROP CONSTRAINT userdata_pkey;
       public            postgres    false    214            �           2620    16475    room aggiungi_creatoreroom    TRIGGER        CREATE TRIGGER aggiungi_creatoreroom AFTER INSERT ON public.room FOR EACH ROW EXECUTE FUNCTION public.aggiungi_creatoreroom();
 3   DROP TRIGGER aggiungi_creatoreroom ON public.room;
       public          postgres    false    215    219            �           2620    16486    messagedata existsuser    TRIGGER     p   CREATE TRIGGER existsuser AFTER INSERT ON public.messagedata FOR EACH ROW EXECUTE FUNCTION public.existsuser();
 /   DROP TRIGGER existsuser ON public.messagedata;
       public          postgres    false    232    216            �           2620    16479    joinrequest verify_userinroom    TRIGGER     ~   CREATE TRIGGER verify_userinroom AFTER INSERT ON public.joinrequest FOR EACH ROW EXECUTE FUNCTION public.verify_userinroom();
 6   DROP TRIGGER verify_userinroom ON public.joinrequest;
       public          postgres    false    220    217            �           2606    16453 #   joinrequest joinrequest_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_roomid_fkey;
       public          postgres    false    3194    217    215            �           2606    16448 #   joinrequest joinrequest_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_userid_fkey;
       public          postgres    false    214    3192    217            �           2606    16440 #   messagedata messagedata_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_roomid_fkey;
       public          postgres    false    3194    216    215            �           2606    16435 #   messagedata messagedata_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_userid_fkey;
       public          postgres    false    214    3192    216            �           2606    16423    room room_creatoruserid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_creatoruserid_fkey FOREIGN KEY (creatoruserid) REFERENCES public.userdata(userid);
 F   ALTER TABLE ONLY public.room DROP CONSTRAINT room_creatoruserid_fkey;
       public          postgres    false    214    215    3192            �           2606    16468    roomusers roomusers_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_roomid_fkey;
       public          postgres    false    218    215    3194            �           2606    16463    roomusers roomusers_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.roomusers
    ADD CONSTRAINT roomusers_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 I   ALTER TABLE ONLY public.roomusers DROP CONSTRAINT roomusers_userid_fkey;
       public          postgres    false    218    214    3192               C   x����0B�a��ئ��d�9���'Ā�-�P35��TiHTا?�D1�p����3��F�s         
  x��X�n����b��.�&8CRtS�V�^K�dz3�FҠ$����e����L��@{�7ɓ�;C9��8E��.�����|���~҆�4�ׅj��Q�#q��O��q0���V�9&�.U�Z1��V5-�%���o.}�3�x*&S�a:��kKN�>T� ���O`9�#kX˃d�)=��ynX�t�ο����4J���G"q�֔�1,;j/�d�>�rg��>v2W��I.��&S��"���
�
Y�Ͷ���R�7�)�n�ʰmg�Տ����Aq2�OňG#x�D�daص658k��gN�4���y��Q�e��	Qh��Y������4�}��ǰ�ư�R<~��?X�j�q���-l�j�n����|v���(�F؉gCܙR�;D*�/\s��$�x4��j�<��l��\�uԮ�J&��_0|]��`�gq��5���k���ԭz�
�#
/��^�,N(h�܃��#��(���(<�&q_ˡ������qf�g�̠*T��D[oQ4JaU��j[������['bs?|$�,I��5�O
]�v-�_eCP�3����&����ޕ�jY����/!><=�]��L�q����[�F���l�HCt��t���īt���3���2�Ѥ���r��0�p*~҃�>wi%Q4�?�� ��\o����ˎ���A�~̓���u� ׿d�&��t���A�G(�`wi����)= ZPn�5=K���f�����R�J���'�5P*��������b3c�,��n�0�,�Ղm�7���������]ξ�n��~��л�Z��-�:�s�m�S>�S������ҟ��5�&�"R�h4��솰ђ�8�"�@��EP��aj��	Z�T���n�X��ST���}��K_�rte��v��!+��Hx���j��}X��[��>[������a~�\�O9�+���ZO��N��d���G��anቱ4(]�뉅0��L�cLr��!�n��}�Ѵ9�\q0�##�]e����3��Oc�ʉ2h�X�!c��Cg0�N�"M{ൺ�Y�*D{YZz�
#���s
�q�GT�Cn�2��Re�F�b���*��8�s�{^:v\�0��Lm�@ϒ]�-@�ڡ!c�D��|ԒBF�*�K��0q㉟��l��l?���ڵ��П��!�A雎}��/�%������/�4��'�D$�xX�o�6����#��[����翑D�P|;�w�T+�;��U�+�������8��@�৖�Z!�'|=$0᫦2���"r��-����DlN�%2�4Jm��hA�I��'c��J�W�q���`	r$���FY�Ӻ���?`���=�&��ܚ�"�x8����=�i�x<	ʬ�i�?���41Sg*�/@��Z��&�C�$2���_�Ե�\����D�[�$����5�sTpE�v��#��)��Ekܬ��C���� �p�чݾU��b��Hv��+W�nҴ�3d�.O��x^cr����~ �w4��F�_� +�xj5�Q6��9��u2�`�D�P�5�Q�?VcR)�j�M�&)�Q�5&9����}m䮣H�t$D�����BҺRT�<(�|�PD�^1��Gs�Q;�गb����N�:���C�5��3"������~�vb2'^N�������Y(~��a8EϤ���+�$D�IM�hx=���X��f�2�X�� NM������m�����b���9��g�������!Bi)@�i8c2[���s�DPk'ad=���%��v�YB��񃭡ȖV���8�'X��B��D�W�?�V��RU�]��G�,K#;���=Q�C��h��	���U����a��8�GR������ri��j�� ���^�lJ��Ie�^���z�F�N���A�{�EriW}k��?�B`� �P���{� >��[��o��;��0fd���u�o�/r�!���7���;����k�ߑ�(u�:{٨T��vO��ixe�'�� �֮{�,
�j��M�sK�� ��c����_?���M�$�Ȥs��9pA��}��T�	���Z�����B�{��O�UD{�8�?@F�����D��*��Z&12U�]��p����V;��Ġ��<��Oun�SgzA\�?i!�@��}�`B�9����?�C56=G��Qj�=��ЙkR�$D#1��F߳�)]KAt���ҿm7<y:!۽vq��� ��������J��D#���텅�]�v�d�ӵV?� ��¾9 l���IJH?�"Ax����Ai��wv��"쵑]D�^��[�u0'U�f�D�z��$���3d�ح�{����5��C������V��t7[;���+��R�56����v����`k�}�z��lu3������?o׳w���ؽ��+������z���k|\|�-[_�V�%�Xb2������Fñw��Y{S����N��%"��{z�X��dz_�sp�-����'K���EoK��1myih��{�=��7���J         D  x�M��n�@E��W�]���	᱌����J(�R7C��0MB������˲��soi]K�ݥ��R��J�����r�m^uQ�V���k{P؅��s��k����rK�t�C�	��=zݘ��b3������t6��0��\��6B���r*E[=d���*|�5��l��� ǐ�Bi�x�!r�}W�Ǯ�ܷ�A�#��J7�X}:���,3`�/�r��v=����p�\t��"a��*�؜�bȖ�R�R����\�N�i���f��`o8�_��*���o��{��1��#� U)]펢�)4�?����{�         �   x�%P� ���I�K����~��0�
)�0;���!���0��:��,%K<!����m�3.X����$n0�,�'���uۋ�����7z��/�4,��@c���	����E�O���s*�"=�-=	���#���6���t�����m*]&��i�bU� ?Wc/+         �  x�-�M��F�׬[d1�	Xr��"��n�_�� ̆.�6'%R�*9�>I��9G_(G���FB����=����|d���:M�)��)���v:ql������A�*!�Y�)��J���j]�����z�%�3��E7�-{�X����z)z@qW�����>����΀�>�6�]�F�>[W�)S�)o�zn���D�w��#����3f��J��&��as���B��{:��=CRѝl�K�N�[rw�UcPs8���&"�H�m�%�� K������X����ي!�F�F9Z/dn�Dݵ+
���ٔ��/�B��@���g۵+J�q��c�ª︱�ņ�M@��1�lY^h�����#��r�+�M=��)��#>3V�H�	�޸��*����x�A;j�\qcZ��H�<�n� ���JZ1�(b�)��mg�pŝ	;J�*�!��[�rX�ٮ�������3�il�r|�ʑ}g{�� *K
h��ue3�-[�#�֫�u�O���ؑI/w��|���-�rl���w�q7c�&��>W^���z��Q���4�E�l�V��73]i�X�$E��������ﰌ,6��§OЕ6������:Pi���~'�>k����Rc�x��J�B!7�>*�?[�>�����M�     