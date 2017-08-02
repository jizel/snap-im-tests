-- originated from https://github.com/auth0/nginx-jwt

local jwt = require "resty.jwt"
local cjson = require "cjson"

local secret = certString

assert(secret ~= nil, "Certificate was not loaded properly!")

local M = {}

function M.unauthorized_exit(code, message)
    ngx.status = ngx.HTTP_UNAUTHORIZED
    local error = {code = code, message = message }
    ngx.header.Content_Type = "application/json"
    ngx.print(cjson.encode(error))
    ngx.exit(ngx.HTTP_UNAUTHORIZED)
end

local function token_missing()
    M.unauthorized_exit(123, "Missing token - supply Authorization header or access_token query parameter")
end

function M.extract_and_verify_jwt()
    -- require Authorization request header
    local auth_header = ngx.var.http_Authorization
    local access_token_query_param = ngx.req.get_uri_args()["access_token"]

    if auth_header == nil and access_token_query_param == nil then
        token_missing()
    end

    -- require Bearer token or access token in query parameter
    local token = nil
    if auth_header ~= nil then
        _, _, token = string.find(auth_header, "Bearer%s+(.+)")
    elseif access_token_query_param ~= nil then
        token = access_token_query_param
    end

    if token == nil then
        token_missing()
    end

    -- require valid JWT
    local jwt_obj = jwt:verify(secret, token, 0)
    if jwt_obj.verified == false then
        local reason = jwt_obj.reason
        if string.find(reason, "expired") then
            M.unauthorized_exit(124, "Please use your refresh token to get a new access token if applicable, " .. reason)
        else
            M.unauthorized_exit(125, "Invalid token: " .. reason)
        end
    end
    return jwt_obj
end

function M.auth(claim_specs)

    local jwt_obj = M.extract_and_verify_jwt()

    -- optionally require specific claims
    if claim_specs ~= nil then
        -- make sure they passed a Table
        if type(claim_specs) ~= 'table' then
            ngx.log(ngx.STDERR, "Configuration error: claim_specs arg must be a table")
            ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
        end

        -- process each claim
        local blocking_claim = ""
        for claim, spec in pairs(claim_specs) do
            -- make sure token actually contains the claim
            local claim_value = jwt_obj.payload[claim]
            if claim_value == nil then
                blocking_claim = claim .. " (missing)"
                break
            end

            local spec_actions = {
                -- claim spec is a string (pattern)
                ["string"] = function (pattern, val)
                    return string.match(val, pattern) ~= nil
                end,

                -- claim spec is a predicate function
                ["function"] = function (func, val)
                    -- convert truthy to true/false
                    if func(val) then
                        return true
                    else
                        return false
                    end
                end
            }

            local spec_action = spec_actions[type(spec)]

            -- make sure claim spec is a supported type
            if spec_action == nil then
                ngx.log(ngx.STDERR, "Configuration error: claim_specs arg claim '" .. claim .. "' must be a string or a table")
                ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
            end

            -- make sure token claim value satisfies the claim spec
            if not spec_action(spec, claim_value) then
                blocking_claim = claim
                break
            end
        end

        if blocking_claim ~= "" then
            M.unauthorized_exit(126, "User did not satisfy claim: ".. blocking_claim)
        end
    end

    -- write the X-Auth headers, identity-id requires setup in keycloak
    -- identity_id is empty for service accounts
    local identity_id = jwt_obj.payload.identity_id
    if identity_id ~= nil then
        ngx.req.set_header("X-Auth-UserId", jwt_obj.payload.identity_id)
    else
        -- todo later - set groups to header instead of X-Auth-ServiceId
        ngx.req.set_header("X-Auth-ServiceId", jwt_obj.payload.aud)
    end
    ngx.req.set_header("X-Auth-AppId", jwt_obj.payload.aud)

    return jwt_obj
end

function M.table_contains(table, item)
    for _, value in pairs(table) do
        if value == item then return true end
    end
    return false
end

return M