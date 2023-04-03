-- 文章表
CREATE table `article`(
    `id`           bigint(11)   not null auto_increment comment '主键',
    `title`        varchar(100) not null default '' comment '文章标题',
    `intro`        varchar(300) not null default '' comment '文章简介',
    `content`      text not null default '' comment '文章内容',
    `path`         varchar(200) not null default '' comment '文章路径',
    `author`       varchar(20)  not null default '' comment '作者',
    `publish_date` date         not null comment '发布日期',
    `insert_time`  datetime     not null default current_timestamp comment '创建时间',
    `update_time`  datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '文章表';

-- 标签表
create table `tag`(
    `id`          bigint(11)  not null auto_increment comment '主键',
    `article_id`  bigint(11)  not null comment '文章id',
    `name`        varchar(20) not null default '' comment '标签名称',
    `insert_time` datetime    not null default current_timestamp comment '创建时间',
    `update_time` datetime    not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '标签表';

-- 评论表
create table `comment`(
    `id`          bigint(11)    not null auto_increment comment '主键',
    `article_id`  bigint(11)    not null comment '文章id',
    `nickname`    varchar(20)   not null default '' comment '昵称',
    `content`     varchar(1000) not null default '' comment '评论内容',
    `commen_time` datetime      not null default current_timestamp comment '评论时间',
    `insert_time` datetime      not null default current_timestamp comment '创建时间',
    `update_time` datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '评论表';


