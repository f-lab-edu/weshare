drop table if exists logout cascade;
drop table if exists pay_result cascade;

drop table if exists payment cascade;

drop table if exists party_capsule cascade;

drop table if exists party cascade;

drop table if exists party_join cascade;

drop table if exists ott cascade;

drop table if exists card cascade;

drop table if exists users cascade;

create table ott
(
    ott_id           bigint auto_increment primary key,
    per_day_price       int          not null,
    maximum_capacity int          not null,
    minimum_capacity int          not null,
    name             varchar(255) null
);

create table users
(
    user_id       bigint auto_increment primary key,
    created_date  datetime(6) null,
    modified_date datetime(6) null,
    email         varchar(255) not null,
    nick_name     varchar(255) not null,
    password      varchar(255) not null,
    role          enum ('ADMIN', 'CLIENT') not null,
    telephone     varchar(255) not null,
    --constraint users_email_uk unique (email),
    constraint users_nickname_uk unique (nick_name)
);

create table party
(
    party_id             bigint auto_increment primary key,
    created_date         datetime(6) null,
    modified_date        datetime(6) null,
    capacity             int          not null,
    ott_account_id       varchar(255) not null,
    ott_account_password varchar(255) not null,
    party_status         enum ('CLOSED', 'RUNNING') not null,
    user_id              bigint       not null,
    ott_id               bigint       not null,
    constraint party_ott_fk foreign key (ott_id) references ott (ott_id),
    constraint party_user_fk foreign key (user_id) references users (user_id)
);

create table party_join
(
    party_join_id     bigint auto_increment
        primary key,
    created_date      datetime(6) null,
    modified_date     datetime(6) null,
    party_join_status enum ('JOINED', 'WAITING','PAY_WAITING') not null,
    ott_id            bigint not null,
    user_id           bigint not null,
    constraint partyjoin_user_fk foreign key (user_id) references users (user_id),
    constraint partyjoin_ott_fk foreign key (ott_id) references ott (ott_id)
);


create table party_capsule
(
    party_capsule_id     bigint auto_increment primary key,
    created_date         datetime(6) null,
    modified_date        datetime(6) null,
    party_capsule_status enum ('CLOSED', 'DELETED', 'EMPTY', 'OCCUPIED', 'WITHDRAWN','PRE_OCCUPIED') not null,
    party_id             bigint             not null,
    user_id              bigint             null,
    expiration_date      DATE           null,
    join_date      DATE           null,
    cancel_reservation   bool default false not null,
    withdrawn_time       DATETIME           null,
    constraint partycapsule_party_fk foreign key (party_id) references party (party_id),
    constraint partycapsule_user_fk foreign key (user_id) references users (user_id)
);

create table card
(
    card_id       bigint auto_increment primary key,
    card_number   varchar(255) not null,
    billing_key   varchar(255) not null,
    card_status   enum ('AVAILABLE', 'UNAVAILABLE') not null,
    user_id       bigint       not null,
    created_date  datetime(6) null,
    modified_date datetime(6) null,
    constraint card_user_fk foreign key (user_id) references users (user_id)
);

create table payment
(
    payment_id       bigint auto_increment primary key,
    party_capsule_id bigint not null,
    card_id          bigint not null,
    amount           bigint not null,
    pay_date         date not null ,
    created_date     datetime(6) null,
    modified_date    datetime(6) null,
    constraint payment_party_capsule_fk foreign key (party_capsule_id) references party_capsule (party_capsule_id),
    constraint payment_card_fk foreign key (card_id) references card (card_id)
);

create table pay_result
(
    pay_result_id     bigint auto_increment primary key,
    receipt           json   null,
    payment_id        bigint not null,
    pay_result_status enum('SUCCESS', 'PAY_REJECTED', 'ERROR_OCCUR') not null,
    error_message     varchar(255) null,
    constraint pay_result_payment_fk foreign key (payment_id) references payment(payment_id)
);


create table logout
(
    id           bigint auto_increment primary key,
    expired_date datetime(6) not null,
    token        varchar(255) not null
);

INSERT INTO ott (per_day_price, maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 2, '넷플릭스');
INSERT INTO ott (per_day_price, maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 1, '왓챠');
INSERT INTO ott (per_day_price, maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 1, '아마존 프라임');
INSERT INTO ott (per_day_price,  maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 1, '티빙');
INSERT INTO ott (per_day_price,  maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 1, '웨이브');
INSERT INTO ott (per_day_price, maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 1, '왓챠');
INSERT INTO ott (per_day_price,  maximum_capacity, minimum_capacity, name)
VALUES (100, 4, 1, '디즈니 플러스');


INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람1', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람2', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람3', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람4', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람5', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람6', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람7', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람8', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람9', 'aaass232312321',
        'CLIENT', '010-1111-1111');
INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람10', 'aaass232312321 ',
        'CLIENT', '010-1111-1111');

INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람11', 'aaass232312321 ',
        'CLIENT', '010-1111-1111');

INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람12', 'aaass232312321 ',
        'CLIENT', '010-1111-1111');

INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람13', 'aaass232312321 ',
        'CLIENT', '010-1111-1111');

INSERT INTO users (created_date, modified_date, email, nick_name, password, role, telephone)
VALUES ('2024-03-25 00:12:08.000000', '2024-03-25 00:12:12.000000', 'jangu3384@gmail.com', '사람14', 'aaass232312321 ',
        'CLIENT', '010-1111-1111');


INSERT INTO party_join (created_date, modified_date, party_join_status, ott_id, user_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'WAITING', 1, 1);
INSERT INTO party_join (created_date, modified_date, party_join_status, ott_id, user_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'WAITING', 1, 2);
INSERT INTO party_join (created_date, modified_date, party_join_status, ott_id, user_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'WAITING', 1, 3);
INSERT INTO party_join (created_date, modified_date, party_join_status, ott_id, user_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'WAITING', 1, 4);

INSERT INTO party_join (created_date, modified_date, party_join_status, ott_id, user_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'WAITING', 1, 6);
INSERT INTO party_join (created_date, modified_date, party_join_status, ott_id, user_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'WAITING', 1, 7);


INSERT INTO party (created_date, modified_date, capacity, ott_account_id, ott_account_password, party_status,
                           user_id, ott_id)
VALUES ('2024-03-27 03:15:23.000000', '2024-03-27 03:15:26.000000', 3, 'asdfdf', 'adsf22', 'RUNNING', 8, 1);


INSERT INTO party_capsule (created_date, modified_date, party_capsule_status, party_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'EMPTY', 1);
INSERT INTO party_capsule (created_date, modified_date, party_capsule_status, party_id)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'EMPTY', 1);

INSERT INTO party_capsule (created_date, modified_date, party_capsule_status, party_id, user_id,join_date,expiration_date)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'OCCUPIED', 1, 5,'2024-03-26', '2024-04-03');
INSERT INTO party_capsule (created_date, modified_date, party_capsule_status, party_id, user_id,join_date,
                                   expiration_date)
VALUES ('2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000', 'OCCUPIED', 1, 6, '2024-03-26','2024-04-03');

INSERT INTO card (card_number, billing_key, card_status, user_id, created_date, modified_date)
VALUES ('test222', 'test333', 'AVAILABLE', 4, '2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000');
INSERT INTO card (card_number, billing_key, card_status, user_id, created_date, modified_date)
VALUES ('test222', 'test333', 'AVAILABLE', 5, '2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000');
INSERT INTO card (card_number, billing_key, card_status, user_id, created_date, modified_date)
VALUES ('test222', 'test333', 'AVAILABLE', 6, '2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000');
INSERT INTO card (card_number, billing_key, card_status, user_id, created_date, modified_date)
VALUES ('test222', 'test333', 'AVAILABLE', 7, '2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000');
INSERT INTO card (card_number, billing_key, card_status, user_id, created_date, modified_date)
VALUES ('test222', 'test333', 'AVAILABLE', 8, '2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000');

INSERT INTO card (card_number, billing_key, card_status, user_id, created_date, modified_date)
VALUES ('test222', 'test333', 'AVAILABLE', 9, '2024-03-25 23:32:50.000000', '2024-03-25 23:32:50.000000');







