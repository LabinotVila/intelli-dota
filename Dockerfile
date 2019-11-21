FROM aa8y/sbt:0.13.18

USER root

RUN apk update && apk add --no-cache libc6-compat

COPY ./target/universal/scalatry-1.0.zip .

RUN unzip scalatry-1.0.zip

COPY ./main_route .

CMD scalatry-1.0/bin/scalatry -Dhttp.port=${PORT}