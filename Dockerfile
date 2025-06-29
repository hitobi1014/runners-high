FROM amazoncorretto:17

ARG JAR_FILE=build/libs/*.jar

# 로컬에서 빌드된 JAR 파일 -> 컨테이너 복사
COPY ${JAR_FILE} /app/app.jar

# 애플리케이션 실행 포트
EXPOSE 8080

# 컨테이너 시작될 때 실행할 명령어
ENTRYPOINT ["java", "-Dspring.profiles.active=docker","-jar", "/app/app.jar"]