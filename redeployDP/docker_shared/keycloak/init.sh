#!/usr/bin/env sh

DIR=${BASH_SOURCE%/*}
API="http://localhost:8081/auth"

ACCESS_TOKEN=$(curl --insecure -s \
  -d "client_id=admin-cli" \
  -d "username=admin" \
  -d "password=admin" \
  -d "grant_type=password" \
  "$API/realms/master/protocol/openid-connect/token" \
  | sed 's/.*"access_token":"\([^"]*\).*/\1/');

curl --insecure -s -v \
  -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  --data-binary "@$DIR/Snapshot-realm.json" \
  "$API/admin/realms";

curl --insecure -s -v \
  -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  --data-binary "@$DIR/Snapshot-user-federation.json" \
  "$API/admin/realms/Snapshot/user-federation/instances";
