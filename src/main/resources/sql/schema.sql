-- 文章表
CREATE table `article`(
    `id`           bigint(11)   not null auto_increment comment '主键',
    `title`        varchar(100) not null default '' comment '文章标题',
    `intro`        varchar(300) not null default '' comment '文章简介',
    `content`      text         not null            comment '文章内容',
    `path`         varchar(200) not null default '' comment '文章路径',
    `sign`          varchar(50) not null default '' comment 'path的sha值',
    `author`       varchar(20)  not null default '' comment '作者',
    `insert_time`  datetime     not null default current_timestamp comment '创建时间',
    `update_time`  datetime     not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '文章表';

-- 标签表
create table `tag`(
    `id`          bigint(11)  not null auto_increment comment '主键',
    `name`        varchar(20) not null default '' comment '标签名称',
    `insert_time` datetime    not null default current_timestamp comment '创建时间',
    `update_time` datetime    not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '标签表';

-- 文章标签关联表
create table `article_tag_relation` (
    `id`          bigint(11)  not null auto_increment comment '主键',
    `article_id`  bigint(11)  not null comment '文章id',
    `tag_id`      bigint(11)  not null comment '标签id',
    `insert_time` datetime    not null default current_timestamp comment '创建时间',
    `update_time` datetime    not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '文章标签关联表';


-- 评论表
create table `comment`(
    `id`            bigint(11)    not null auto_increment comment '主键',
    `article_id`    bigint(11)    not null comment '文章id',
    `nickname`      varchar(20)   not null default '' comment '昵称',
    `content`       varchar(1000) not null default '' comment '评论内容',
    `email`         varchar(100)  not null default '' comment '邮箱',
    `personal_site` varchar(200)  not null default '' comment '个人站点',
    `insert_time` datetime      not null default current_timestamp comment '创建时间',
    `update_time` datetime      not null default current_timestamp on update current_timestamp comment '更新时间',
    primary key (`id`)
) engine=InnoDB default charset=utf8mb4 comment '评论表';


