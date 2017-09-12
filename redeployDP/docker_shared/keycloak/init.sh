#!/usr/bin/env sh
# Keycloak configuration
# TODO: Use Admin CLI (kcadm.sh) after input files are supported.

DIR=${BASH_SOURCE%/*}
API="http://localhost:8081/auth"

ACCESS_TOKEN=$(curl --insecure -s \
    -d "client_id=admin-cli" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" \
    "$API/realms/master/protocol/openid-connect/token" \
    | sed 's/.*"access_token":"\([^"]*\).*/\1/');

function run {
    local http_method=$1
    local path=$2
    local file=$3

    curl --insecure -s -v \
        -X $http_method \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "Content-Type: application/json" \
        --data-binary "@$DIR/$file" \
        "$API/$path";
}

run "POST" "admin/realms/Snapshot/clients" "client.json"
run "POST" "admin/realms/Snapshot/components" "component-UserStorageProvider.json"
run "POST" "admin/realms/Snapshot/components" "component-KeyProvider.json"
