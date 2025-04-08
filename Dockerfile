FROM gradle:8.6-jdk21 AS builder

WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .

# gradlew 실행권한 부여
RUN chmod +x ./gradlew

## 빌드 (jar 파일 생성)
#RUN ./gradlew build --no-daemon -Dspring.profiles.active=test
##CMD ["./gradlew", "build", "--no-daemon", "-Dspring.profiles.active=test"]
#
#FROM eclipse-temurin:21-jdk as runner
#WORKDIR /app
#COPY --from=builder --chown=gradle:gradle /home/gradle/project/build/libs/app.jar app.jar
#RUN chmod 755 app.jar
#
##  이거는 docker-compose working_dir에서 실행
#CMD ["java", "-Dspring.profiles.active=test", "-jar", "app.jar"]



# 테스트 실행 (test 프로파일 지정)
CMD ["./gradlew", "test", "--no-daemon", "-Dspring.profiles.active=test"]