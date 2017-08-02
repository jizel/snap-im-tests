
local M = {} -- public interface

local redis = require "resty.redis"

local function connect_to_redis()
  local red = redis:new()

  red:set_timeout(1000) -- 1 sec

  local ok, err = red:connect(redis_backend, redis_port)
  if not ok then
    ngx.log(ngx.WARN, "failed to connect: " .. err)
    return
  end
  
  -- local res, err = red:auth(redis_key)
  -- if not res then
  --  ngx.say("failed to authenticate: ", err)
  --  return
  -- end

  return red
end

local function return_connection_to_pool(red)
  -- put it into the connection pool of size 100,
  -- with 10 seconds max idle time
  local ok, err = red:set_keepalive(10000, 100)
  if not ok then
    ngx.log("failed to set keepalive: ", err)
    return
  end
end

function M.store_user_to_cache(user_id, reason_string)
  local red = connect_to_redis()
  if not red then
    ngx.log(ngx.WARN, "Unable to store user " .. user_id .. " in redis cache")
  else
    -- we only care that the key exists
    ok, err = red:set(user_id, reason_string)
    if not ok then
      ngx.log(ngx.WARN, "failed to set key to redis cache " .. err)
      return
    end

    -- set proper expiry of the key - the same as validity of access token
    ok, err = red:expire(user_id, access_token_validity_seconds)
    if not ok then
      ngx.log(ngx.WARN, "failed to set expiry " .. err)
      return
    end
    return_connection_to_pool(red)
  end
end

function M.delete_user_from_cache(user_id)
  local red = connect_to_redis()
  if not red then
    ngx.log(ngx.WARN, "Unable to delete user " .. user_id .. " from redis cache")
  else
    res, err = red:del(user_id)
    if not res then
      ngx.log(ngx.WARN, "failed to delete key " .. user_id .. " error " .. err)
      return
    elseif res > 0 then
      ngx.log(ngx.INFO, "User " .. user_id .. " deleted from cache")
    end
    return_connection_to_pool(red)
  end
end

function M.user_exists_in_cache(user_id)
  local red = connect_to_redis()
  if not red then
    ngx.log(ngx.WARN, "Unable to get user " .. user_id .. " from redis cache")
  else
    local res, err = red:get(user_id)
    if not res then
      ngx.log(ngx.WARN, "failed to get cache key " .. user_id .. " error " .. err)
      -- rather return that the user is not in the cache
      return false
    end

    if res == ngx.null then
      return false
    else
      return true, res
    end
    return_connection_to_pool(red)
  end
end

return M