# Использование официального образа MongoDB с Docker Hub
FROM mongo

# Определение рабочей директории
WORKDIR /usr/src/app

# Копирование скрипта инициализации базы данных
#COPY init_db.js ./

# Определение команды запуска MongoDB с указанием порта
CMD ["mongod", "--bind_ip_all", "--port", "27017"]