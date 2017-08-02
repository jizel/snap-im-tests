-- Autheticates request with access token or with pair client id/secret.

local threescale = require 'threescale_nginx'
local ts = require 'threescale_utils'

local params = ngx.req.get_uri_args();

if ts.required_params_present({'client_id', 'client_secret'}, params) then
    -- Check valid client_id / secret

--  local clientToSecret = ngx.shared.clients_secrets
--  local secret = clientToSecret:get(params.client_id)
--  if secret == nil or secret ~= params.client_secret then
--    ngx.status = 401
--    ngx.header.content_type = 'text/plain; charset=us-ascii'
--    ngx.print("Authentication failed")
--    ngx.exit(ngx.HTTP_OK)
--	end
    local exists = ngx.location.capture("/_threescale/auth", { args="app_id="..params.client_id.."&app_key="..params.client_secret, share_all_vars = true })
    if exists.status ~= 200 then
        ngx.status = 401
        ngx.header.content_type = 'text/plain; charset=us-ascii'
        ngx.print("Authentication failed")
        ngx.exit(ngx.HTTP_OK)
    end
elseif ts.required_params_present({'access_token'}, params)  then
    -- Check valid access token
    threescale.verify_token()
else
    ngx.log(0, "Required params client_id and client_secret weren't passed, response is "..ngx.HTTP_UNAUTHORIZED);
    ngx.exit(ngx.HTTP_UNAUTHORIZED)
end