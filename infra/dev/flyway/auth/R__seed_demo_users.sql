INSERT INTO admin_users (email, full_name, password_hash, role, created_at)
VALUES
    (
        'admin@avelrune.com',
        'Avelrune Yönetim',
        '$2a$10$xsr.L/Ok6g3cmLonotzvY.fC9aY7p22j0W084gUN3KjkXKi8bBCLa',
        'ADMIN',
        TIMESTAMPTZ '2026-01-01 00:00:00+00'
    ),
    (
        'user@avelrune.com',
        'Ayşe Yılmaz',
        '$2a$10$CMkrihBdQEN2KaRwySrxEu5kGjtegv/xW2kzNSNX/eGh7WbqfLE5O',
        'USER',
        TIMESTAMPTZ '2026-01-01 00:00:00+00'
    )
ON CONFLICT (email) DO NOTHING;
