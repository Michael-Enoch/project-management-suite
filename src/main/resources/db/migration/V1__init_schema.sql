create table if not exists app_user (
    id uuid primary key,
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    active boolean not null default true,
    created_at timestamptz not null default now()
);

create table if not exists app_role (
    id bigserial primary key,
    name varchar(64) not null unique
);

create table if not exists app_permission (
    id bigserial primary key,
    name varchar(64) not null unique
);

create table if not exists user_role (
    user_id uuid not null references app_user(id) on delete cascade,
    role_id bigint not null references app_role(id) on delete cascade,
    primary key (user_id, role_id)
);

create table if not exists role_permission (
    role_id bigint not null references app_role(id) on delete cascade,
    permission_id bigint not null references app_permission(id) on delete cascade,
    primary key (role_id, permission_id)
);

create table if not exists project (
    id uuid primary key,
    code varchar(24) not null unique,
    name varchar(140) not null,
    description varchar(2000),
    status varchar(24) not null,
    owner_user_id uuid not null references app_user(id),
    start_date date not null,
    end_date date not null,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists project_member (
    project_id uuid not null references project(id) on delete cascade,
    user_id uuid not null references app_user(id) on delete cascade,
    membership_role varchar(32) not null,
    created_at timestamptz not null default now(),
    primary key (project_id, user_id)
);

create table if not exists task_item (
    id uuid primary key,
    project_id uuid not null references project(id) on delete cascade,
    title varchar(200) not null,
    description varchar(4000),
    assignee_user_id uuid references app_user(id),
    status varchar(24) not null,
    due_date date,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists milestone (
    id uuid primary key,
    project_id uuid not null references project(id) on delete cascade,
    title varchar(180) not null,
    target_date date not null,
    achieved boolean not null default false,
    achieved_at timestamptz,
    version bigint not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists refresh_token (
    id uuid primary key,
    user_id uuid not null references app_user(id) on delete cascade,
    token_hash varchar(128) not null unique,
    expires_at timestamptz not null,
    revoked boolean not null default false,
    created_at timestamptz not null default now()
);

create table if not exists audit_event (
    id bigserial primary key,
    actor_user_id uuid references app_user(id),
    action varchar(64) not null,
    entity_type varchar(64) not null,
    entity_id uuid,
    details jsonb,
    created_at timestamptz not null default now()
);

create index if not exists idx_project_owner_status on project (owner_user_id, status);
create index if not exists idx_project_member_user on project_member (user_id);
create index if not exists idx_task_project_status on task_item (project_id, status);
create index if not exists idx_task_project_due on task_item (project_id, due_date);
create index if not exists idx_refresh_token_user on refresh_token (user_id, revoked);
create index if not exists idx_audit_event_entity on audit_event (entity_type, entity_id, created_at);
