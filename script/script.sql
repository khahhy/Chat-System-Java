-- Tạo bảng `users`
CREATE TABLE public.users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    address VARCHAR(255),
    date_of_birth DATE,
    gender CHAR(1) CHECK (gender = ANY (ARRAY['M', 'F', 'U'])),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN DEFAULT TRUE,
    is_online BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_admin BOOLEAN DEFAULT FALSE
);

-- Tạo bảng `groups`
CREATE TABLE public.groups (
    group_id SERIAL PRIMARY KEY,
    group_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng `spamreports`
CREATE TABLE public.spamreports (
    report_id SERIAL PRIMARY KEY,
    reporter_id INTEGER NOT NULL,
    reported_id INTEGER NOT NULL,
    reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reporter_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reported_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

-- Tạo bảng `messages`
CREATE TABLE public.messages (
    message_id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER,
    group_id INTEGER,
    content TEXT,
    "timestamp" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_encrypted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (sender_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES public.groups(group_id) ON DELETE CASCADE
);

-- Tạo bảng `loginhistory`
CREATE TABLE public.loginhistory (
    history_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

-- Tạo bảng `groupmembers`
CREATE TABLE public.groupmembers (
    group_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN DEFAULT FALSE NOT NULL,
    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES public.groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

-- Tạo bảng `friends`
CREATE TABLE public.friends (
    user_id INTEGER NOT NULL,
    friend_id INTEGER NOT NULL,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

-- Tạo bảng `friendrequests`
CREATE TABLE public.friendrequests (
    request_id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    status VARCHAR(20) CHECK (status IN ('pending', 'accepted', 'rejected')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

-- Tạo bảng `deletemessages`
CREATE TABLE public.deletemessages (
    message_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (message_id, user_id),
    FOREIGN KEY (message_id) REFERENCES public.messages(message_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);


INSERT INTO public.users (username, full_name, email, password, is_admin)
VALUES 
('admin1', 'Admin One', 'vhduc22@clc.fitus.edu.vn', '123', TRUE),
('admin2', 'Admin Two', 'ttctuong22@clc.fitus.edu.vn', '123', TRUE);
