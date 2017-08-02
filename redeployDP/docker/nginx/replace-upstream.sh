#!/bin/sh

sed -i 's/___BACKEND_IDENTITY_HOST___/${BACKEND_IDENTITY_HOST:=dp-identity}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf
sed -i 's/___BACKEND_IDENTITY_PORT___/${BACKEND_IDENTITY_PORT:=18080}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf

sed -i 's/___BACKEND_TOMCAT_HOST___/${BACKEND_TOMCAT_HOST:=tomcat}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf
sed -i 's/___BACKEND_TOMCAT_PORT___/${BACKEND_TOMCAT_PORT:=8080}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf

sed -i 's/___BACKEND_KEYCLOAK_HOST___/${BACKEND_KEYCLOAK_HOST:=keycloak}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf
sed -i 's/___BACKEND_KEYCLOAK_PORT___/${BACKEND_KEYCLOAK_PORT:=8080}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf

sed -i 's/___BACKEND_DWHOUTPUT_HOST___/${BACKEND_DWHOUTPUT_HOST:=dwh-output.dwh-sandbox.svc.cluster.local}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf
sed -i 's/___BACKEND_DWHOUTPUT_PORT___/${BACKEND_DWHOUTPUT_PORT:=80}/g' /usr/local/openresty/nginx/conf/snapshot-upstreams.conf

sed -i 's/___REDIS_HOST___/${REDIS_HOST:=redis}/g' /usr/local/openresty/nginx/conf/nginx.conf
sed -i 's/___REDIS_PORT___/${REDIS_PORT:=6379}/g' /usr/local/openresty/nginx/conf/nginx.conf