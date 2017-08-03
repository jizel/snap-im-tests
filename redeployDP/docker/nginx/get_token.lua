local ts = require 'threescale_utils'
local access_token_validity_seconds = 259200
local redis = require("redis_utils")

local function store_token(client_id, token)
  -- Needed otherwise 3scale returns: 422 Unprocessable Entity.
  ngx.req.set_header("Content-Type", "application/x-www-form-urlencoded")

  local result = redis.store_token(token, client_id)
  if result == false then
    ngx.log(ngx.ERR, "Couldn't store token to REDIS store! Token=" .. token .. ", client_id=" ..  client_id)
    --ngx.say("Couldn't store token to REDIS store!")
    ngx.exit(ngx.HTTP_FORBIDDEN)
  end

--  local stored = ngx.location.capture("/_threescale/oauth_store_token",
--    {method = ngx.HTTP_POST,
--    body = "provider_key=" .. ngx.var.provider_key ..
--           "&app_id=" .. client_id ..
--           "&token=" .. token .. "&ttl=" .. access_token_validity_seconds
--    })
--
-- -- if stored.status ~= 200 then
 --   ngx.say("eeeerror")
 --   ngx.exit(ngx.HTTP_OK)
 -- end

  ngx.header.content_type = "application/json; charset=utf-8"
  ngx.say({'{"access_token": "'.. token .. '", "token_type": "bearer", "expires_in": "259200", "scope": "all"}'})
  ngx.exit(ngx.HTTP_OK)
end

function get_token(params)
  local required_params = {'username', 'password', 'grant_type', "password"}

  if ts.required_params_present(required_params, params) and params['grant_type'] == 'password' then
    local res = ngx.location.capture("/_oauth/token",
      -- { method = ngx.HTTP_GET, args = { username = params.username, password = params.password, code = ngx.var.code } } )
	  { method = ngx.HTTP_GET, args = { username = params.username, password = params.password, code = 's24fet6yhd7' } } )
 --   ngx.log(ngx.ERR, "[get-token] response = " .. res.status)
    if res.status ~= 200 then
      ngx.status = res.status
      ngx.header.content_type = "application/json; charset=utf-8"
      ngx.print(res.body)
      ngx.exit(ngx.HTTP_OK)
    else
      -- Extract token from response - assuming it's in json format
    store_token(params.client_id, res.body) 
 
    end
  else
    ngx.log(0, "NOPE")
    ngx.exit(ngx.HTTP_FORBIDDEN)
  end
end

local params = {}

if "GET" == ngx.req.get_method() then
  params = ngx.req.get_uri_args()
else
  ngx.req.read_body()
  params = ngx.req.get_post_args()
end

  local clientToSecret = ngx.shared.clients_secrets
  local secret = clientToSecret:get(params.client_id)
  if secret == nil or secret ~= params.client_secret then
    ngx.status = 401
    ngx.header.content_type = 'text/plain; charset=us-ascii'
    ngx.print("Authentication failed")
    ngx.exit(ngx.HTTP_OK)
  else
-- Check valid client_id / secret first in back end
--local exists = ngx.location.capture("/_threescale/auth", { args="app_id="..params.client_id.."&app_key="..params.client_secret, share_all_vars = true })
--if exists.status ~= 200 then
--  ngx.status = 401
--  ngx.header.content_type = 'text/plain; charset=us-ascii'
--  ngx.print("Authentication failed")
--  ngx.exit(ngx.HTTP_OK)
--else
    local s = get_token(params)
  end