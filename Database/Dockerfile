FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=proyectoclinica
ENV MYSQL_ROOT_HOST=%

# Copiar script de inicialización
COPY script.sql /docker-entrypoint-initdb.d/

# Crear y copiar configuración de MySQL
RUN echo '[mysqld]\n\
character-set-server = utf8mb4\n\
collation-server = utf8mb4_unicode_ci\n\
default-authentication-plugin = mysql_native_password\n\
bind-address = 0.0.0.0\n\
port = 3306\n\
\n\
[client]\n\
default-character-set = utf8mb4\n\
\n\
[mysql]\n\
default-character-set = utf8mb4' > /etc/mysql/conf.d/my.cnf

# Establecer permisos
RUN chmod 644 /etc/mysql/conf.d/my.cnf \
    && chmod 644 /docker-entrypoint-initdb.d/script.sql \
    && chown -R mysql:mysql /docker-entrypoint-initdb.d/ \
    && chown -R mysql:mysql /etc/mysql/conf.d/

# Exponer puerto
EXPOSE 3306 