/**
 * @author 彭黄振Jame
 * @codeName Generate Webhook Url
 * @description Generate Webhook Url
 * @createTime 2025-10-15
 * @bindingObjectLabel Webhook URL
 * @bindingObjectApiName WebhookURL__c
 * @函数需求编号
 */
String apiName = context.data.object_describe_api_name as String;

String baseUrl = "https://www.kitdoll.com"; 
String instanceURL = "https://www.fxiaoke.com";
String saveToken2GateUrl = "https://www.kitdoll.com/admin/token"

String certification = context.data.certification__c as String;
String keyId = context.data._id as String;
String webhookUrl = ""
String methodCls = context.data.webhookMethods__c as String;
String webhookToken = ""
String dataToken = context.data.token__c as String
if (dataToken == null){
  if (certification != null && webhookToken != null) {
    String tokenBody1 = '{"method":'+methodCls+',"baseurl":'+instanceURL+',"cert" :' +certification+'}' 
    webhookToken = Fx.crypto.MD5.encode(tokenBody1) as String
    // String tokenBody = '{"token":'+webhookToken+',"method":'+methodCls+',"baseurl":'+baseUrl+',"cert" :' +certification+'}' 
    webhookUrl = baseUrl + "/webhook/" + webhookToken;
    log.info("webhookurl: "+ webhookUrl)
  } else {
    log.info("Certification is null, cannot generate token.")
  }
  
  // 保存到网关的请求体
  Map map = ["token": webhookToken, "method": methodCls,"baseurl": instanceURL,"cert":certification]
  StringBody body = StringBody.builder().content(map).build()
  
  Request request = Request.builder()
                  .method("POST")
                  .url(saveToken2GateUrl)
                  .timeout(7000)
                  .retryCount(0)
                  .header("Content-Type", "application/json")
                  .body(body)
                  .build()
  def(Boolean error, HttpResult result, String message) = Fx.http.execute(request)
          
  if (error || result.statusCode != 200){
    log.error("http request error01: "+ error +" errorMessage: " + message + " result: " + result)
  }else {
    def content = result
    log.info("req body is : $content")
  }
  
  Map updateData = Maps.newHashMap();
  updateData.put("token__c", webhookToken);
  updateData.put("webhookUrl__c", webhookUrl);
  
  // UpdateAttribute attribute = UpdateAttribute.builder().triggerWorkflow(true).build()
  def (Boolean err, Map data, String errMessage)  = Fx.object.update(apiName, keyId, updateData,UpdateAttribute.builder().triggerWorkflow(true).build())
  if (err) {
      log.error("获取对象异常" + errMessage)
  }
  else {
      log.info("update data is : $data")
  }
  
  UIAction alertAction = AlertAction.builder()
      .type("default")
      .text("The Webhook URL Generated Success.")
      .build()
  return alertAction
} else {
  UIAction alertAction = AlertAction.builder()
      .type("default")
      .text("The Webhook URL has been generated already.")
      .build()
  return alertAction
}
// Return the AlertAction
