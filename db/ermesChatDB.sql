PGDMP     +                
    z         	   ErmesChat    15.1    15.1                0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                       0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16398 	   ErmesChat    DATABASE     ~   CREATE DATABASE "ErmesChat" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Italian_Italy.1252';
    DROP DATABASE "ErmesChat";
                postgres    false            �            1259    16445    joinrequest    TABLE     L   CREATE TABLE public.joinrequest (
    userid integer,
    roomid integer
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
       public         heap    postgres    false            �            1259    16399    userdata    TABLE     �   CREATE TABLE public.userdata (
    userid integer NOT NULL,
    username character varying(1000),
    userpassword character varying(1000)
);
    DROP TABLE public.userdata;
       public         heap    postgres    false                      0    16445    joinrequest 
   TABLE DATA           5   COPY public.joinrequest (userid, roomid) FROM stdin;
    public          postgres    false    217   �                 0    16428    messagedata 
   TABLE DATA           Q   COPY public.messagedata (messagetext, timestampdata, userid, roomid) FROM stdin;
    public          postgres    false    216   �       
          0    16418    room 
   TABLE DATA           ?   COPY public.room (creatoruserid, roomname, roomid) FROM stdin;
    public          postgres    false    215   �       	          0    16399    userdata 
   TABLE DATA           B   COPY public.userdata (userid, username, userpassword) FROM stdin;
    public          postgres    false    214           u           2606    16434    messagedata messagedata_pkey 
   CONSTRAINT     u   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_pkey PRIMARY KEY (userid, roomid, timestampdata);
 F   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_pkey;
       public            postgres    false    216    216    216            s           2606    16422    room room_pkey 
   CONSTRAINT     P   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (roomid);
 8   ALTER TABLE ONLY public.room DROP CONSTRAINT room_pkey;
       public            postgres    false    215            q           2606    16405    userdata userdata_pkey 
   CONSTRAINT     X   ALTER TABLE ONLY public.userdata
    ADD CONSTRAINT userdata_pkey PRIMARY KEY (userid);
 @   ALTER TABLE ONLY public.userdata DROP CONSTRAINT userdata_pkey;
       public            postgres    false    214            y           2606    16453 #   joinrequest joinrequest_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_roomid_fkey;
       public          postgres    false    3187    217    215            z           2606    16448 #   joinrequest joinrequest_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.joinrequest
    ADD CONSTRAINT joinrequest_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.joinrequest DROP CONSTRAINT joinrequest_userid_fkey;
       public          postgres    false    214    3185    217            w           2606    16440 #   messagedata messagedata_roomid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_roomid_fkey FOREIGN KEY (roomid) REFERENCES public.room(roomid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_roomid_fkey;
       public          postgres    false    3187    216    215            x           2606    16435 #   messagedata messagedata_userid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.messagedata
    ADD CONSTRAINT messagedata_userid_fkey FOREIGN KEY (userid) REFERENCES public.userdata(userid);
 M   ALTER TABLE ONLY public.messagedata DROP CONSTRAINT messagedata_userid_fkey;
       public          postgres    false    214    216    3185            v           2606    16423    room room_creatoruserid_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_creatoruserid_fkey FOREIGN KEY (creatoruserid) REFERENCES public.userdata(userid);
 F   ALTER TABLE ONLY public.room DROP CONSTRAINT room_creatoruserid_fkey;
       public          postgres    false    215    214    3185                  x������ � �            x������ � �      
      x������ � �      	      x������ � �     