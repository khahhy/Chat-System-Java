--
-- PostgreSQL database dump
--

-- Dumped from database version 17.1
-- Dumped by pg_dump version 17.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
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
-- Name: friendrequests; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friendrequests (
    request_id integer NOT NULL,
    sender_id integer,
    receiver_id integer,
    status character varying(20),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT friendrequests_status_check CHECK (((status)::text = ANY ((ARRAY['pending'::character varying, 'accepted'::character varying, 'rejected'::character varying])::text[])))
);


ALTER TABLE public.friendrequests OWNER TO postgres;

--
-- Name: friendrequests_request_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.friendrequests_request_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.friendrequests_request_id_seq OWNER TO postgres;

--
-- Name: friendrequests_request_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.friendrequests_request_id_seq OWNED BY public.friendrequests.request_id;


--
-- Name: friends; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.friends (
    user_id integer NOT NULL,
    friend_id integer NOT NULL,
    is_blocked boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.friends OWNER TO postgres;

--
-- Name: groupmembers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groupmembers (
    group_id integer NOT NULL,
    user_id integer NOT NULL,
    is_admin boolean DEFAULT false,
    joined_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.groupmembers OWNER TO postgres;

--
-- Name: groups; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.groups (
    group_id integer NOT NULL,
    group_name character varying(100),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.groups OWNER TO postgres;

--
-- Name: groups_group_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.groups_group_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.groups_group_id_seq OWNER TO postgres;

--
-- Name: groups_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.groups_group_id_seq OWNED BY public.groups.group_id;


--
-- Name: loginhistory; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.loginhistory (
    history_id integer NOT NULL,
    user_id integer,
    login_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    logout_time timestamp without time zone
);


ALTER TABLE public.loginhistory OWNER TO postgres;

--
-- Name: loginhistory_history_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.loginhistory_history_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.loginhistory_history_id_seq OWNER TO postgres;

--
-- Name: loginhistory_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.loginhistory_history_id_seq OWNED BY public.loginhistory.history_id;


--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    message_id integer NOT NULL,
    sender_id integer,
    receiver_id integer,
    group_id integer,
    content text,
    "timestamp" timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    is_encrypted boolean DEFAULT false
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: messages_message_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.messages_message_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.messages_message_id_seq OWNER TO postgres;

--
-- Name: messages_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.messages_message_id_seq OWNED BY public.messages.message_id;


--
-- Name: spamreports; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.spamreports (
    report_id integer NOT NULL,
    reporter_id integer,
    reported_id integer,
    reason text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE public.spamreports OWNER TO postgres;

--
-- Name: spamreports_report_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.spamreports_report_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.spamreports_report_id_seq OWNER TO postgres;

--
-- Name: spamreports_report_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.spamreports_report_id_seq OWNED BY public.spamreports.report_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    user_id integer NOT NULL,
    username character varying(50) NOT NULL,
    full_name character varying(100),
    address character varying(255),
    date_of_birth date,
    gender character(1),
    email character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    status boolean DEFAULT true,
    is_online boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_gender_check CHECK ((gender = ANY (ARRAY['M'::bpchar, 'F'::bpchar])))
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_user_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_user_id_seq OWNER TO postgres;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: friendrequests request_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendrequests ALTER COLUMN request_id SET DEFAULT nextval('public.friendrequests_request_id_seq'::regclass);


--
-- Name: groups group_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups ALTER COLUMN group_id SET DEFAULT nextval('public.groups_group_id_seq'::regclass);


--
-- Name: loginhistory history_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loginhistory ALTER COLUMN history_id SET DEFAULT nextval('public.loginhistory_history_id_seq'::regclass);


--
-- Name: messages message_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages ALTER COLUMN message_id SET DEFAULT nextval('public.messages_message_id_seq'::regclass);


--
-- Name: spamreports report_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spamreports ALTER COLUMN report_id SET DEFAULT nextval('public.spamreports_report_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- Data for Name: friendrequests; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friendrequests (request_id, sender_id, receiver_id, status, created_at) FROM stdin;
\.


--
-- Data for Name: friends; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.friends (user_id, friend_id, is_blocked, created_at) FROM stdin;
\.


--
-- Data for Name: groupmembers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.groupmembers (group_id, user_id, is_admin, joined_at) FROM stdin;
\.


--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.groups (group_id, group_name, created_at) FROM stdin;
\.


--
-- Data for Name: loginhistory; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.loginhistory (history_id, user_id, login_time, logout_time) FROM stdin;
\.


--
-- Data for Name: messages; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.messages (message_id, sender_id, receiver_id, group_id, content, "timestamp", is_encrypted) FROM stdin;
\.


--
-- Data for Name: spamreports; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.spamreports (report_id, reporter_id, reported_id, reason, created_at) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (user_id, username, full_name, address, date_of_birth, gender, email, password, status, is_online, created_at) FROM stdin;
\.


--
-- Name: friendrequests_request_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.friendrequests_request_id_seq', 1, false);


--
-- Name: groups_group_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.groups_group_id_seq', 1, false);


--
-- Name: loginhistory_history_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.loginhistory_history_id_seq', 1, false);


--
-- Name: messages_message_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.messages_message_id_seq', 1, false);


--
-- Name: spamreports_report_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.spamreports_report_id_seq', 1, false);


--
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_user_id_seq', 1, false);


--
-- Name: friendrequests friendrequests_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendrequests
    ADD CONSTRAINT friendrequests_pkey PRIMARY KEY (request_id);


--
-- Name: friends friends_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT friends_pkey PRIMARY KEY (user_id, friend_id);


--
-- Name: groupmembers groupmembers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groupmembers
    ADD CONSTRAINT groupmembers_pkey PRIMARY KEY (group_id, user_id);


--
-- Name: groups groups_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (group_id);


--
-- Name: loginhistory loginhistory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loginhistory
    ADD CONSTRAINT loginhistory_pkey PRIMARY KEY (history_id);


--
-- Name: messages messages_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_pkey PRIMARY KEY (message_id);


--
-- Name: spamreports spamreports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spamreports
    ADD CONSTRAINT spamreports_pkey PRIMARY KEY (report_id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- Name: friendrequests friendrequests_receiver_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendrequests
    ADD CONSTRAINT friendrequests_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.users(user_id);


--
-- Name: friendrequests friendrequests_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friendrequests
    ADD CONSTRAINT friendrequests_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(user_id);


--
-- Name: friends friends_friend_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT friends_friend_id_fkey FOREIGN KEY (friend_id) REFERENCES public.users(user_id);


--
-- Name: friends friends_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.friends
    ADD CONSTRAINT friends_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: groupmembers groupmembers_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groupmembers
    ADD CONSTRAINT groupmembers_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(group_id);


--
-- Name: groupmembers groupmembers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.groupmembers
    ADD CONSTRAINT groupmembers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: loginhistory loginhistory_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loginhistory
    ADD CONSTRAINT loginhistory_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id);


--
-- Name: messages messages_group_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_group_id_fkey FOREIGN KEY (group_id) REFERENCES public.groups(group_id);


--
-- Name: messages messages_receiver_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_receiver_id_fkey FOREIGN KEY (receiver_id) REFERENCES public.users(user_id);


--
-- Name: messages messages_sender_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.messages
    ADD CONSTRAINT messages_sender_id_fkey FOREIGN KEY (sender_id) REFERENCES public.users(user_id);


--
-- Name: spamreports spamreports_reported_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spamreports
    ADD CONSTRAINT spamreports_reported_id_fkey FOREIGN KEY (reported_id) REFERENCES public.users(user_id);


--
-- Name: spamreports spamreports_reporter_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.spamreports
    ADD CONSTRAINT spamreports_reporter_id_fkey FOREIGN KEY (reporter_id) REFERENCES public.users(user_id);


--
-- PostgreSQL database dump complete
--

