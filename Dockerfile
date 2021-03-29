FROM registry.aidatrans.com/library/jvm-agent:1.0.3-alpine-jdk1.8
#ADD build/libs/*.jar /app.jar
#VOLUME /tmp
##如果没有 -XX:+UseCMSInitiatingOccupancyOnly 这个参数, 只有第一次会使用CMSInitiatingPermOccupancyFraction=70 这个值. 后面的情况会自动调整
#WORKDIR /tmp
#ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
WORKDIR /data
#复制上下文目录下的target/demo-1.0.0.jar 到容器里
ARG JAR_FILE
RUN apk add --update font-adobe-100dpi ttf-dejavu fontconfig
RUN apk --no-cache add curl
ADD ${JAR_FILE} aida_translate_service.jar
#声明运行时容器提供服务端口，这只是一个声明，在运行时并不会因为这个声明应用就会开启这个端口的服务
EXPOSE 8080
#指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
CMD ["-jar","-Dspring.profiles.active=prod","-Dspring.cloud.nacos.config.enabled=true","-Dspring.cloud.sentinel.enabled=true","-Dspring.cloud.nacos.config.server-addr=opweb:40001","-Dspring.cloud.sentinel.transport.dashboard=http://sentinel:40003","aida_translate_service.jar"]
HEALTHCHECK --start-period=30s --interval=10s --timeout=3s CMD curl -f http://localhost:8080/actuator/info || exit 1