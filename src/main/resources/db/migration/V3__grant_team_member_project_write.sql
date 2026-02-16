insert into role_permission (role_id, permission_id)
select r.id, p.id
from app_role r
join app_permission p on p.name = 'PROJECT_WRITE'
where r.name = 'TEAM_MEMBER'
on conflict do nothing;
