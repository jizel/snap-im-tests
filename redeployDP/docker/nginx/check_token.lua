-- We need to require threescale_nginx.lua script and run its methods,
-- because dofile('threescale_nginx') is not working propertly in Nginx-lua.

-- 3SCALE OFFLINE
-- local threescale = require 'threescale_nginx'

local jwt = require("nginx-jwt")
local redis = require("redis_utils")

local jwt_obj = jwt.auth()
local identity_id = jwt_obj.payload.identity_id

-- for service accounts there is no identity_id
if identity_id ~= nil then
    local exists, reason_string = redis.user_exists_in_cache(jwt_obj.payload.identity_id)
    if exists == true then
        jwt.unauthorized_exit(127,
            "User ".. jwt_obj.payload.identity_id .. " associated with the token is not authorized, reason=" .. reason_string)
    end
end

-- we need to verify the token in 3scale - e.g. for API limits
-- 3SCALE OFFLINE
-- threescale.verify_token(jwt_obj)