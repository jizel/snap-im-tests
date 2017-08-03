#!/bin/ash

if [ $KEYCLOAK_USER ] && [ $KEYCLOAK_PASSWORD ]; then
    /opt/jboss/keycloak/bin/add-user-keycloak.sh --user $KEYCLOAK_USER --password $KEYCLOAK_PASSWORD >/dev/null
fi

exec /opt/jboss/keycloak/bin/standalone.sh $@
exit $?