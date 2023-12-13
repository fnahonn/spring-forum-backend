--liquibase formatted sql
-- PostgreSQL database dump
--

-- Dumped from database version 16.0
-- Dumped by pg_dump version 16.0

--changeset fnahon:20231212-1
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;


--
-- Name: forum_message; Type: TABLE; Schema: public; Owner: javaforum
--

CREATE TABLE public.forum_message (
    id bigint NOT NULL,
    accepted boolean,
    content text NOT NULL,
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    author_id bigint NOT NULL,
    topic_id bigint NOT NULL
);


ALTER TABLE public.forum_message OWNER TO javaforum;

--
-- Name: forum_message_id_seq; Type: SEQUENCE; Schema: public; Owner: javaforum
--

CREATE SEQUENCE public.forum_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.forum_message_id_seq OWNER TO javaforum;

--
-- Name: forum_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: javaforum
--

ALTER SEQUENCE public.forum_message_id_seq OWNED BY public.forum_message.id;


--
-- Name: forum_topic; Type: TABLE; Schema: public; Owner: javaforum
--

CREATE TABLE public.forum_topic (
    id bigint NOT NULL,
    content text NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    name character varying(255) NOT NULL,
    solved boolean NOT NULL,
    updated_at timestamp(6) with time zone,
    author_id bigint NOT NULL,
    last_message_id bigint
);


ALTER TABLE public.forum_topic OWNER TO javaforum;

--
-- Name: forum_topic_id_seq; Type: SEQUENCE; Schema: public; Owner: javaforum
--

CREATE SEQUENCE public.forum_topic_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.forum_topic_id_seq OWNER TO javaforum;

--
-- Name: forum_topic_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: javaforum
--

ALTER SEQUENCE public.forum_topic_id_seq OWNED BY public.forum_topic.id;


--
-- Name: refresh_token; Type: TABLE; Schema: public; Owner: javaforum
--

CREATE TABLE public.refresh_token (
    id bigint NOT NULL,
    expiry_date timestamp(6) with time zone NOT NULL,
    revoked boolean NOT NULL,
    token character varying(255) NOT NULL,
    user_id bigint
);


ALTER TABLE public.refresh_token OWNER TO javaforum;

--
-- Name: refresh_token_id_seq; Type: SEQUENCE; Schema: public; Owner: javaforum
--

CREATE SEQUENCE public.refresh_token_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.refresh_token_id_seq OWNER TO javaforum;

--
-- Name: refresh_token_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: javaforum
--

ALTER SEQUENCE public.refresh_token_id_seq OWNED BY public.refresh_token.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: javaforum
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    created_at timestamp(6) with time zone,
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255),
    pseudo character varying(255),
    role character varying(255),
    updated_at timestamp(6) with time zone,
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'USER'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO javaforum;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: javaforum
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO javaforum;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: javaforum
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: forum_message id; Type: DEFAULT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_message ALTER COLUMN id SET DEFAULT nextval('public.forum_message_id_seq'::regclass);


--
-- Name: forum_topic id; Type: DEFAULT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_topic ALTER COLUMN id SET DEFAULT nextval('public.forum_topic_id_seq'::regclass);


--
-- Name: refresh_token id; Type: DEFAULT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.refresh_token ALTER COLUMN id SET DEFAULT nextval('public.refresh_token_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);

--
-- Name: forum_message forum_message_pkey; Type: CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_message
    ADD CONSTRAINT forum_message_pkey PRIMARY KEY (id);


--
-- Name: forum_topic forum_topic_pkey; Type: CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_topic
    ADD CONSTRAINT forum_topic_pkey PRIMARY KEY (id);


--
-- Name: refresh_token refresh_token_pkey; Type: CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT refresh_token_pkey PRIMARY KEY (id);


--
-- Name: refresh_token uk_r4k4edos30bx9neoq81mdvwph; Type: CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT uk_r4k4edos30bx9neoq81mdvwph UNIQUE (token);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: forum_message fkadj2wmiq2tnq6ad3j7etyppt3; Type: FK CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_message
    ADD CONSTRAINT fkadj2wmiq2tnq6ad3j7etyppt3 FOREIGN KEY (topic_id) REFERENCES public.forum_topic(id) ON DELETE CASCADE;


--
-- Name: refresh_token fkjtx87i0jvq2svedphegvdwcuy; Type: FK CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.refresh_token
    ADD CONSTRAINT fkjtx87i0jvq2svedphegvdwcuy FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: forum_topic fkm3wcwq14i4m8auh1uo3359wny; Type: FK CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_topic
    ADD CONSTRAINT fkm3wcwq14i4m8auh1uo3359wny FOREIGN KEY (last_message_id) REFERENCES public.forum_message(id) ON DELETE SET NULL;


--
-- Name: forum_topic fkmmrfcl3eiy1hvshraal6d7vmd; Type: FK CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_topic
    ADD CONSTRAINT fkmmrfcl3eiy1hvshraal6d7vmd FOREIGN KEY (author_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: forum_message fktb02h8vwwnh95216v2pv3xdrp; Type: FK CONSTRAINT; Schema: public; Owner: javaforum
--

ALTER TABLE ONLY public.forum_message
    ADD CONSTRAINT fktb02h8vwwnh95216v2pv3xdrp FOREIGN KEY (author_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

