/**
 * @author 彭黄振Jame
 * @codeName 测试请求
 * @description 测试请求
 * @createTime 2025-10-17
 * @bindingObjectLabel ESign记录表
 * @bindingObjectApiName ESign_Record__c
 * @nameSpace button
 * @returnType UIAction
 * @函数需求编号
 */
    String apiName = context.data.object_describe_api_name as String;
    String keyId = context.data._id as String;

    ESignInfo.configName = 'Adobe Sign';
    String key = ESignInfo.getKey();
    log.info(key)
    String baseUrl = ESignInfo.getBaseURL();
    log.info(baseUrl)
    
    String agreementId = context.data.agreementId__c as String;
    String uri = "/api/rest/v6";
    Map fileUrlMap = ESAgreementService.getAgreementAcrobatSignedFileURL(baseUrl,key,uri,agreementId)
    // 将获取的附件构建并上传到文件服务器
    Object xmap = fileUrlMap[1] as Object
    String path = xmap["path"] as String
    String size = xmap["size"] as String
    String fileName = context.data.npath__c as String
    String extensionName = "pdf"
    List fileInfo = [["path":path,"size":size,"ext":extensionName,"filename": fileName+"."+extensionName]]
    
    Map updateData = Maps.newHashMap();
    updateData.put("finalfile__c", fileInfo);

    UpdateAttribute attribute = UpdateAttribute.builder().triggerWorkflow(true).build();
    
    def result = Fx.object.update(apiName, keyId, updateData,attribute).result() as Map
    log.info("result:" + result)
    
    UIAction alertAction = AlertAction.builder()
      .type("default")
      .text("Signed Docs Fetched Success!")
      .build()
    
    // Return the AlertAction
    return alertAction