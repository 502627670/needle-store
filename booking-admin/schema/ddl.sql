insert into t_group(id, name, parent_id, path) values(1, '系统管理组', null, "1-");

insert into t_user(id,username,password,sort_order,user_status,group_id) 
values(1, 'administrator', '$2a$10$K6O53bmvbE/zHQ6ZW7kX4u/cEdgWFhd7WrQbmKztb5bZqJN5.Aq/e', 0, 'ENABLE',1);