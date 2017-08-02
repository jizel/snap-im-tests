
-- 3SCALE OFFLINE
-- local threescale = require "threescale_nginx"
local cjson = require "cjson"
local users = require "handle_users"

local M = {} -- public interface

local function replace(input_string, regex, replacement)
    if input_string then
        local newstr, n, err = ngx.re.gsub(input_string, regex, replacement)
        if newstr then
            return newstr
        else
            ngx.log(ngx.ERR, "error: ", err)
            return
        end
    end
end

function M.replace_cookies_for_sso(cookies_headers)
    if cookies_headers ~= nil then
        -- if there is just one cookie, type is string -> we change it to table
        if (type(cookies_headers) == "string" ) then
            cookies_headers={cookies_headers}
        end
        for index,cookie in pairs(cookies_headers) do
            cookies_headers[index] = replace(cookie, "Path=/auth/realms/Snapshot", "Path=/oauth/auth")
        end
    end
    ngx.header.Set_Cookie = cookies_headers
end

function M.keycloak_post_call(keycloak_uri)
    ngx.req.read_body()

    local response = ngx.location.capture(
        keycloak_uri,
        { method = ngx.HTTP_POST, always_forward_body = true , args = ngx.req.get_uri_args(), share_all_vars = true });

    local location_header = replace(response.header["Location"], "/auth/realms/Snapshot/login-actions", "/auth/actions")
    local response_body = replace(response.body, "/auth/resources/1.9.1.final/login/keycloak", "/auth/resources")
    response_body = replace(response_body, "/auth/realms/Snapshot/login-actions", "/auth/actions")

    ngx.header.Location = location_header
    ngx.header.Content_Type = response.header["Content-Type"]
    M.replace_cookies_for_sso(response.header["Set-Cookie"])
    ngx.status = response.status
    ngx.print(response_body)
    return response
end

function M.store_token_from_header(location_header)
    if location_header ~= nil then
        local match_table = ngx.re.match(location_header, "#access_token=([^&]+)&.*expires_in=([0-9]+)")

        --e.g. for authenticate and non-implicit flow there is no access_token returned
        if match_table then
            local access_token = match_table[1]
            local expires_in = match_table[2]
			-- 3SCALE OFFLINE
            -- threescale.store_token(access_token, expires_in)
            users.handle_login_of_user(access_token)
        end
    end
end

function M.store_token_from_payload(token_response)

    local decoded_response = cjson.decode(token_response.body)

    local access_token = decoded_response.access_token
    local expires_in = decoded_response.expires_in
    if access_token ~= nil and expires_in ~=nil then
        -- 3SCALE OFFLINE
		-- threescale.store_token(access_token, expires_in)
        users.handle_login_of_user(access_token)
    else
        ngx.log(ngx.INFO, "No access token and expiration in response from keycloak, status="
                          .. token_response.status .. " body=" .. token_response.body)
    end
end

return M