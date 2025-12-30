/**
 * @author 彭黄振Jame
 * @codeName GetESigningUrl
 * @description 获取电子签url链接
 * @createTime 2025-10-11
 * @bindingObjectLabel Service Report ESignature
 * @bindingObjectApiName adobe_sign__c
 * @函数需求编号
 */
String apiName = context.data.object_describe_api_name as String;
    
    String esignUrl = "";
    
    ESignInfo.configName = "Docusign"
    String key = DocuSignAuth.getAccessToken() as String
    log.info "accessToken:$key"
    String baseUrl = ESignInfo.getBaseURL()
    log.info "baseUrl:$baseUrl"
    String accountId = ESignInfo.getAccountId()
    log.info "AccountId:$accountId"
    //获取附件信息
    String inemail = context.data.initiator_email__c as String;
    String inname = context.data.initiator_name__c as String;
    String agreementId = context.data.agreementId__c as String;
    String uri = "/restapi/v2.1/accounts/"+accountId+"/envelopes/"+agreementId+"/views/recipient";
    log.info(uri)
    
    
    if (agreementId) {
      
      log.info("get  signingUrls >>>>>>>>>>>>>>>> ")
      Map bodyMap = [:]
      bodyMap.put("authenticationMethod","none")
      bodyMap.put("email",inemail)
      bodyMap.put("userName",inname)
      bodyMap.put("clientUserId","1")
      bodyMap.put("returnUrl","https://www.fxiaoke.com")
      
      esignUrl = ESAgreementService.getDocusignAgreementURL(baseUrl,key,uri,bodyMap)
      
  
    }

    String source = Fx.utils.getRequestSource()
    Fx.log.info(source)
    UIAction action = null
    if( source == "WEB" ) {
      //web端返回 WebAction
      action = WebAction.builder()
        .type('url')//组件类型是 跳转 url
        .url(esignUrl)//跳转的 url 地址
        .build()
    } else {
      //移动端需要返回AppAction
      action = AppAction.builder() 
        .url(esignUrl)
        .build()
    }
    
    return action