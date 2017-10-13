#!/usr/bin/env sh
# Keycloak configuration
# TODO: Use Admin CLI (kcadm.sh) after input files are supported.

DIR=${BASH_SOURCE%/*}
API="http://localhost:8081/auth"
REALM="Snapshot"

ACCESS_TOKEN=$(curl --insecure -s \
    -d "client_id=admin-cli" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" \
    "$API/realms/master/protocol/openid-connect/token" \
    | sed 's/.*"access_token":"\([^"]*\).*/\1/');

function send {
    local http_method=$1
    local path=$2

    curl --insecure -s -v \
        -X $http_method \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        -H "Content-Type: application/json" \
        --data @- \
        "$API/$path";
}

function post {
    send "POST" $1 $2
}

function put {
    send "PUT" $1 $2
}

function get {
    local path=$1

    curl --insecure -s \
        -H "Authorization: Bearer $ACCESS_TOKEN" \
        "$API/$path";
}

function urlEncode {
    echo $1 | sed "s/ /%20/g"
}

function activateIdentityAuthenticator {
    local flow=$1
    local EXECUTION_ID=$(get "admin/realms/$REALM/authentication/flows/$flow/executions" | sed 's/.*"id":"\([^"]*\).*/\1/' | tail -1)

    cat flow-activate-execution.json |
    sed -e "s/\${ID}/$EXECUTION_ID/" | # replace ID variable
    put "admin/realms/$REALM/authentication/flows/$flow/executions"
}

DIRECT_GRANT=$(urlEncode "direct grant")
IDENTITY_BROWSER=$(urlEncode "identity browser")
IDENTITY_DIRECT_GRANT=$(urlEncode "identity direct grant")

cd $DIR

# Configure Federation provider
cat component-UserStorageProvider.json | post "admin/realms/$REALM/components"
# Setup java keystore
cat component-KeyProvider.json | post "admin/realms/$REALM/components"
# Create custom "browser" flow
cat flow-browser.json | post "admin/realms/$REALM/authentication/flows/browser/copy"
# Create custom "direct grant" flow
cat "flow-directGrant.json" | post "admin/realms/$REALM/authentication/flows/$DIRECT_GRANT/copy"
# Add custom authenticator into new browser flow
cat flow-authenticator.json | post "admin/realms/$REALM/authentication/flows/$IDENTITY_BROWSER/executions/execution"
# Add custom authenticator into new direct grant flow
cat flow-authenticator.json | post "admin/realms/$REALM/authentication/flows/$IDENTITY_DIRECT_GRANT/executions/execution"
# Activate authenticators
activateIdentityAuthenticator "$IDENTITY_DIRECT_GRANT"
activateIdentityAuthenticator "$IDENTITY_BROWSER"
# Use new flows in realm
cat realm.json | put "admin/realms/$REALM"
