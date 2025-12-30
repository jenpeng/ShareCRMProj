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
    ESignInfo.configName = "Adobe Sign"
    String key = ESignInfo.getKey();
    log.info(key)
    String baseUrl = ESignInfo.getBaseURL();
    log.info(baseUrl)
    String uri = "/api/rest/v6/agreements/";
    log.info(uri)
    String agreementId = "";
    
    if (apiName == 'ESign_Record__c') {

      agreementId = context.data.agreementId__c;
      
    } else if (apiName == "reportability__c") {
      
      agreementId = context.data.agreementId__c;
      
    }
    
    if (agreementId) {
      
      log.info("get  signingUrls >>>>>>>>>>>>>>>> ")
      
      esignUrl = ESAgreementService.getAgreementURL(baseUrl,key,uri,agreementId)
      
      log.info("esignUrl:" + esignUrl)
  
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