local redis = require "redis_utils"
local jwt = require "resty.jwt"

local secret = certString

local M = {} -- public interface

function M.handle_inactive_deleted_users()
  -- API for active/inactive returns 204 no content
  if ngx.var.request_method == "POST" and ngx.status == 204 then
    local match_table = ngx.re.match(ngx.var.request_uri, "/v1/identity/users/([^/]+)/(active|inactive)")
    if match_table then
      local user_id = match_table[1]
      local flag = match_table[2]

      if flag == "inactive" then
        redis.store_user_to_cache(user_id, "inactive")
      else
        -- activating again
        redis.delete_user_from_cache(user_id)
      end
    end
    -- API for deletion of user returns 204 no content
  elseif ngx.var.request_method == "DELETE" and ngx.status == 204 then
    local match_table = ngx.re.match(ngx.var.request_uri, "/v1/identity/users/([^/]+)")
    if match_table then
      local user_id = match_table[1]
      redis.store_user_to_cache(user_id)
    end
  end
end

function M.handle_logged_out_users()
  -- we can get 200 if don't provide redirect_uri in request, 302 otherwise
  if ngx.var.request_method == "GET" and (ngx.status == 200 or ngx.status == 302) then
    local access_token = ngx.var.id_token_hint

    if access_token then
      local jwt_obj = jwt:verify(secret, access_token, 0)
      local identity_id = jwt_obj.payload.identity_id

      -- for service accounts there is no identity_id, we don't need to logout from every app
      if identity_id ~= nil then
        redis.store_user_to_cache(identity_id, "logged out")
      end
    end
  end
end

function M.handle_login_of_user(access_token)
  local jwt_obj = jwt:verify(secret, access_token, 0)
  local identity_id = jwt_obj.payload.identity_id

  if identity_id ~= nil then
    redis.delete_user_from_cache(identity_id)
  end
end

return M