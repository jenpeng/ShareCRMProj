/**
 * @author 彭黄振Jame
 * @codeName DocuSignAuth
 * @description DocuSignAuth
 * @createTime 2025-10-28
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE adb
 * @函数需求编号
 */
import fx.custom.apl.jar.JWTUtils


/**
 * @type classes
 * @returntype
 * @namespace library aa
 */
class DocuSignAuth {
  
    // 设置 DocuSign 配置名称
    private static String configName = 'Docusign'
    private static Boolean refreshToken = false
    
    // 使用 ESignInfo 获取配置值
    private static String getIss() { 
        String originalConfigName = ESignInfo.configName
        ESignInfo.configName = configName
        String clientId = ESignInfo.getClientId()
        ESignInfo.configName = originalConfigName
        return clientId
    }
    
    private static String getSub() { 
        String originalConfigName = ESignInfo.configName
        ESignInfo.configName = configName
        String userId = ESignInfo.getUserId()
        ESignInfo.configName = originalConfigName
        return userId
    }
    
    private static String getAud() { 
        String originalConfigName = ESignInfo.configName
        ESignInfo.configName = configName
        String authUrl = ESignInfo.getAuthURL()
        ESignInfo.configName = originalConfigName
        return authUrl
    }
    
    private static String getScope() { 
        return "signature impersonation"
    }
    
    private static String getAlg() { 
        return "RS256" 
    }
    
    private static String getCacheKey() { 
        return "DocuSign_token" 
    }
    
    private static String getDocusignAuthUrl() {
        String audUrl = getAud()
        log.info "audUrl:${audUrl}/oauth/token"
        return "${audUrl}/oauth/token"
    }
    
    // 获取私钥的方法
    private static String getKeyPem() {
        String originalConfigName = ESignInfo.configName
        ESignInfo.configName = configName
        String pemKey = ESignInfo.getpemKey()
        ESignInfo.configName = originalConfigName
        return pemKey
    }
  
    // private static String iss = "a305ecce-a390-422c-96f0-52d5e1cbd158"
    // private static String sub = "f0d94e7b-946f-4ef6-841d-e945ee2b93d9"
    // private static String aud = "account-d.docusign.com"
    // private static String scope = "signature impersonation"
    // private static String alg = "RS256"
    // private static String cacheKey = "DocuSign_token"
    
    // private static String docusignAuthUrl ="https://"+aud+"/oauth/token"
    /**
     * 无循环，顺序无关，保留换行
     * 返回 Map["PUBLIC":"...","PRIVATE":"..."]
     */
    static Map<String, String> splitPem(String pem) {
        Map<String, String> ans = new LinkedHashMap<String, String>()
        if (pem == null) return ans
    
        /* 两个独立前瞻，各抓各的块，顺序任意 */
        String regex = '(?s)(?=.*-----BEGIN PUBLIC KEY-----(.*?)-----END PUBLIC KEY-----)' +
                       '(?=.*-----BEGIN RSA PRIVATE KEY-----(.*?)-----END RSA PRIVATE KEY-----)'
        def m = pem =~ regex
    
        if (m.find()) {
            ans.put('PUBLIC',
                    '-----BEGIN PUBLIC KEY-----' + m.group(1) + '-----END PUBLIC KEY-----')
            ans.put('PRIVATE',
                    '-----BEGIN RSA PRIVATE KEY-----' + m.group(2) + '-----END RSA PRIVATE KEY-----')
        }
        return ans
    }
    
    /**
     * 向 DocuSign 账户服务器申请 JWT AccessToken
     * @param privateKeyPem 完整的 RSA 私钥 PEM（含 -----BEGIN/END...）
     * @return Map 成功返回 ["access_token":..., "expires_in":...]；失败返回 null
     */
    static String requestAccessToken(Map<String,Object> payload, String privateKeyPem, Integer expires, String alg) {
        
        def content
        // 2. 生成 JWT
        String token = JWTUtils.generateToken(payload, privateKeyPem, expires, alg)
        if (token == null) {
            log.error('JWT generate failed')
            return null
        }
        log.info "token:$token"
        // 3. 组装表单请求
        FormBody body = FormBody.builder()
                .field('grant_type', 'urn:ietf:params:oauth:grant-type:jwt-bearer')
                .field('assertion', token)
                .build()

        Request request = Request.builder()
                .method('POST')
                .url(getDocusignAuthUrl())
                .timeout(7000)
                .retryCount(0)
                .header('Content-Type', 'application/x-www-form-urlencoded')
                .body(body)
                .build()
    
        // 4. 执行请求
        def(Boolean error, HttpResult result, String message) = Fx.http.execute(request)
        if (error || result.statusCode != 200) {
            //可以增加打印请求参数
            log.error("http request error: "+ error +" errorMessage: " + message + " result: " + result)

        } else {
          content = result.content as Map//函数封装的结果，一般使用这个即可
          // log.info("result: "+ result)
          Integer expiresSecond = content["expires_in"] as Integer
          Long nowMillis = DateTime.now().toTimestamp()
          if (nowMillis < 10_000_000_000L) nowMillis *= 1000
          Long tokenExpireMillis = nowMillis + (expiresSecond - 30) * 1000
          Map tokenMap = [:]
          tokenMap.put("access_token", content["access_token"] as String)
          tokenMap.put("token_type", content["token_type"] as String)
          tokenMap.put("token_expires_millis", tokenExpireMillis)
          
          Cache cache = Fx.cache.getDefaultCache()//将token放入本地缓存
          String value = Fx.json.toJson(tokenMap)
          cache.put(getCacheKey(), value, expiresSecond)
          log.info("saveAccessToken2Cache key:" + getCacheKey())
        }
        return content["access_token"] as String
    }
    
    static String getAccessToken(String privateKeyPem) {
        Cache cache = Fx.cache.getDefaultCache()
        log.info "current cache :${cache}"
        String value = cache.get(getCacheKey()) as String//从本地缓存取出token
        String accessToken = ""
        if(refreshToken || value == null){
            // log.info("getAccessTokenFromCache key:" + getCacheKey() )
            // log.info("value is null refreshToken:" + refreshToken )
            accessToken = getRefreshToken(privateKeyPem)
            log.info("get New AccessToken for value null:" +accessToken)
            return accessToken
        }
        Map tokenMap = Fx.json.parse(value)
        accessToken = tokenMap["access_token"] as String
        Long expired = tokenMap["token_expires_millis"] as Long
        log.info("getAccessTokenFromCache key:" + accessToken)
        
        if(expired <= DateTime.now().toTimestamp()){
            
            accessToken = getRefreshToken(privateKeyPem)
            log.info("get New AccessToken for expired:" +accessToken)
        }
        
  
        return accessToken
    }
    
    static String getRefreshToken (String privateKeyPem) {
          Map<String, Object> payload = new LinkedHashMap<String, Object>()
          payload.put('iss', getIss())        // Integrator Key
          payload.put('sub', getSub())        // UserId
          payload.put('aud', getAud().replace("https://", ""))
          payload.put('scope', getScope())
          String accessToken = requestAccessToken(payload, privateKeyPem,6000,getAlg())
          return accessToken
    }
    
    static String getAccessToken() {
      String keyStr = getKeyPem()
      // log.info "keyStr:$keyStr"
      Map<String, String> keys = splitPem(keyStr)
      String privateKeyPem = keys.get('PRIVATE')
      // log.info "privateKeyPem:$privateKeyPem"
      return getAccessToken(privateKeyPem)
    }
    //对外提供的方法
    static String action() {
        return "sucesss"
    }

    //debug 时候的入口方法
    static void main(String[] args) {
      // refreshToken = true
      String key = getAccessToken()
      log.info "Access Token key: $key"

      
    }

}