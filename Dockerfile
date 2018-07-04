# Docker file for tomcat
FROM tomcat:8.0.43-jre8

# Extracting the war started
ADD . /usr/local/my_app/
RUN unzip /usr/local/my_app/target/anchelGateway.war -d /usr/local/tomcat/webapps/anchelGateway
RUN rm -rf /usr/local/my_app
# Extracting the war ended

ENV JPDA_ADDRESS="8000"
ENV JPDA_TRANSPORT="dt_socket"
EXPOSE 8080 8000
RUN chmod +x /usr/local/tomcat/bin/catalina.sh
CMD ["catalina.sh", "run"]
