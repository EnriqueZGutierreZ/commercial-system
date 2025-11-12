INSERT INTO administracion.rol (id, creado, creador, activo, area, cargo, descripcion, can_create, can_read, can_update, can_delete)
VALUES (1, NOW(), 'admin', TRUE, 'Tecnología de la Información', 'Administrador del Sistema', 'Responsable de la administración y mantenimiento del sistema', TRUE, TRUE, TRUE, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO administracion.persona(id, creado, creador, activo, apellidos, nombres, sexo, tipo_documento, num_documento, email, celular)
VALUES(1, NOW(), 'admin', TRUE, 'admin', 'admin', 'M', 1, 12345678, 'admin@example.com', 987654321)
ON CONFLICT (id) DO NOTHING;

INSERT INTO administracion.usuario (id, creado, creador, activo, rol_id, usuario, password, persona_id)
VALUES (1, NOW(), 'admin', TRUE, 1, 'admin', '$2b$12$z8kZdvmu81JqoQ9KcvIeMOfekEmvvNinx02a.ERYXvXNqF2zUtpnC', 1)
ON CONFLICT (id) DO NOTHING;