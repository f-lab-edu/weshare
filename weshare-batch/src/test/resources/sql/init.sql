drop table if exists logout cascade;
drop table if exists pay_result cascade;
drop table if exists party_extension cascade;
drop table if exists payment cascade;
drop table if exists party_capsule cascade;
drop table if exists party cascade;
drop table if exists party_join cascade;
drop table if exists ott cascade;
drop table if exists users cascade;
drop table if exists card cascade;

create table ott
(
    ott_id           bigint auto_increment primary key,
    per_day_price    int          not null,
    maximum_capacity int          not null,
    minimum_capacity int          not null,
    name             varchar(255) null
);

create table card
(
    card_id       bigint auto_increment primary key,
    card_number   varchar(255)                      not null,
    billing_key   varchar(255)                      not null,
    card_status   enum ('AVAILABLE', 'UNAVAILABLE') not null,
    created_date  datetime(6)                       null,
    modified_date datetime(6)                       null
);


create table users
(
    user_id        bigint auto_increment primary key,
    created_date   datetime(6)              null,
    modified_date  datetime(6)              null,
    email          varchar(255)             not null,
    nick_name      varchar(255)             not null,
    password       varchar(255)             not null,
    role           enum ('ADMIN', 'CLIENT') not null,
    telephone      varchar(255)             not null,
    available_card bigint                   null,

    constraint users_email_uk unique (email),
    constraint users_nickname_uk unique (nick_name),
    constraint users_card foreign key (available_card) references card (card_id)
);

create table party
(
    party_id             bigint auto_increment primary key,
    created_date         datetime(6)                null,
    modified_date        datetime(6)                null,
    capacity             int                        not null,
    ott_account_id       varchar(255)               not null,
    ott_account_password varchar(255)               not null,
    party_status         enum ('CLOSED', 'RUNNING') not null,
    user_id              bigint                     not null,
    ott_id               bigint                     not null,
    constraint party_ott_fk foreign key (ott_id) references ott (ott_id),
    constraint party_user_fk foreign key (user_id) references users (user_id)
);

create table party_join
(
    party_join_id     bigint auto_increment
        primary key,
    created_date      datetime(6)                              null,
    modified_date     datetime(6)                              null,
    party_join_status enum ('JOINED', 'WAITING','PAY_WAITING') not null,
    ott_id            bigint                                   not null,
    user_id           bigint                                   not null,
    constraint partyjoin_user_fk foreign key (user_id) references users (user_id),
    constraint partyjoin_ott_fk foreign key (ott_id) references ott (ott_id)
);


create table party_capsule
(
    party_capsule_id     bigint auto_increment primary key,
    created_date         datetime(6)                                                                 null,
    modified_date        datetime(6)                                                                 null,
    party_capsule_status enum ('CLOSED', 'DELETED', 'EMPTY', 'OCCUPIED', 'WITHDRAWN','PRE_OCCUPIED') not null,
    party_id             bigint                                                                      not null,
    user_id              bigint                                                                      null,
    ott_id               bigint                                                                      not null,
    expiration_date      DATE                                                                        null,
    join_date            DATE                                                                        null,
    cancel_reservation   bool default false                                                          not null,
    withdrawn_time       DATETIME                                                                    null,
    constraint partycapsule_party_fk foreign key (party_id) references party (party_id),
    constraint partycapsule_user_fk foreign key (user_id) references users (user_id),
    constraint partycapsule_ott_fk foreign key (ott_id) references ott (ott_id)
);


create table payment
(
    payment_id        bigint auto_increment primary key,
    party_capsule_id  bigint                                          not null,
    card_id           bigint                                          not null,
    amount            bigint                                          not null,
    pay_date          date                                            not null,
    created_date      datetime(6)                                     null,
    modified_date     datetime(6)                                     null,
    receipt           json                                            null,
    pay_result_status enum ('SUCCESS', 'PAY_REJECTED', 'ERROR_OCCUR') null,
    error_message     varchar(255)                                    null,
    constraint payment_party_capsule_fk foreign key (party_capsule_id) references party_capsule (party_capsule_id),
    constraint payment_card_fk foreign key (card_id) references card (card_id)
);

create table pay_result
(
    pay_result_id     bigint auto_increment primary key,
    receipt           json                                            null,
    payment_id        bigint                                          not null,
    pay_result_status enum ('SUCCESS', 'PAY_REJECTED', 'ERROR_OCCUR') not null,
    error_message     varchar(255)                                    null,
    constraint pay_result_payment_fk foreign key (payment_id) references payment (payment_id)
);

CREATE TABLE party_extension
(
    party_extension_id       BIGINT AUTO_INCREMENT primary key,
    created_date             DATETIME                                         NULL,
    modified_date            DATETIME                                         NULL,
    party_capsule_id         BIGINT                                           NULL,
    payment_id               BIGINT                                           NULL,
    previous_expiration_date DATE                                             NULL,
    renew_expiration_date    DATE                                             NULL,
    sent_at                  DATETIME                                         NULL,
    error_message            VARCHAR(255)                                     NULL,
email_sent_status           enum ('NOT_SENT','SENT_SUCCESS', 'SENT_FAILURE') NULL,

    constraint party_extension_payment_fk foreign key (payment_id) references payment (payment_id),
    constraint party_extension_party_capsule_fk foreign key (party_capsule_id) references party_capsule (party_capsule_id)
);






