insert into app_permission (name) values
    ('PROJECT_READ'),
    ('PROJECT_WRITE'),
    ('TASK_READ'),
    ('TASK_WRITE'),
    ('MILESTONE_READ'),
    ('MILESTONE_WRITE'),
    ('KPI_READ'),
    ('REPORT_READ'),
    ('USER_ADMIN')
on conflict (name) do nothing;

insert into app_role (name) values
    ('ADMIN'),
    ('PMO_MANAGER'),
    ('PROJECT_MANAGER'),
    ('TEAM_MEMBER'),
    ('VIEWER')
on conflict (name) do nothing;

with matrix(role_name, permission_name) as (
    values
        ('ADMIN', 'PROJECT_READ'),
        ('ADMIN', 'PROJECT_WRITE'),
        ('ADMIN', 'TASK_READ'),
        ('ADMIN', 'TASK_WRITE'),
        ('ADMIN', 'MILESTONE_READ'),
        ('ADMIN', 'MILESTONE_WRITE'),
        ('ADMIN', 'KPI_READ'),
        ('ADMIN', 'REPORT_READ'),
        ('ADMIN', 'USER_ADMIN'),
        ('PMO_MANAGER', 'PROJECT_READ'),
        ('PMO_MANAGER', 'PROJECT_WRITE'),
        ('PMO_MANAGER', 'TASK_READ'),
        ('PMO_MANAGER', 'TASK_WRITE'),
        ('PMO_MANAGER', 'MILESTONE_READ'),
        ('PMO_MANAGER', 'MILESTONE_WRITE'),
        ('PMO_MANAGER', 'KPI_READ'),
        ('PMO_MANAGER', 'REPORT_READ'),
        ('PROJECT_MANAGER', 'PROJECT_READ'),
        ('PROJECT_MANAGER', 'PROJECT_WRITE'),
        ('PROJECT_MANAGER', 'TASK_READ'),
        ('PROJECT_MANAGER', 'TASK_WRITE'),
        ('PROJECT_MANAGER', 'MILESTONE_READ'),
        ('PROJECT_MANAGER', 'MILESTONE_WRITE'),
        ('PROJECT_MANAGER', 'KPI_READ'),
        ('PROJECT_MANAGER', 'REPORT_READ'),
        ('TEAM_MEMBER', 'PROJECT_READ'),
        ('TEAM_MEMBER', 'TASK_READ'),
        ('TEAM_MEMBER', 'TASK_WRITE'),
        ('TEAM_MEMBER', 'MILESTONE_READ'),
        ('TEAM_MEMBER', 'KPI_READ'),
        ('VIEWER', 'PROJECT_READ'),
        ('VIEWER', 'TASK_READ'),
        ('VIEWER', 'MILESTONE_READ'),
        ('VIEWER', 'KPI_READ'),
        ('VIEWER', 'REPORT_READ')
)
insert into role_permission (role_id, permission_id)
select r.id, p.id
from matrix m
join app_role r on r.name = m.role_name
join app_permission p on p.name = m.permission_name
on conflict do nothing;
