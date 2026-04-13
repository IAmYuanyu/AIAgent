# 使用预装的maven镜像和jdk21镜像
FROM maven:3.9-amazoncorretto-21

# 设置工作目录
WORKDIR /app

# 复制必要的项目文件
COPY pom.xml .
COPY src ./src

# 使用maven打包项目
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8123

# 使用生成环境配置启动项目
CMD ["java", "-jar", "/app/target/AIAgent-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]