/**
 * @author 彭黄振Jame
 * @codeName 测试请求
 * @description 测试请求
 * @createTime 2025-10-17
 * @bindingObjectLabel ESign记录表
 * @bindingObjectApiName ESign_Record__c
 * @函数需求编号
 */
    String apiName = context.data.object_describe_api_name as String;
    String keyId = context.data._id as String;

    ESignInfo.configName = "Docusign"
    String key = DocuSignAuth.getAccessToken() as String
    log.info "accessToken:$key"
    String baseUrl = ESignInfo.getBaseURL();
    log.info(baseUrl)
    String accountId = ESignInfo.getAccountId()
    log.info "AccountId:$accountId"
    //获取附件信息
    String agreementId = context.data.agreementId__c as String;
    String uri = "/restapi/v2.1/accounts/"+accountId
    Map fileUrlMap = ESAgreementService.getAgreementDocuSignedFileURL(baseUrl,key,uri,agreementId)
    // 将获取的附件构建并上传到文件服务器
    Object xmap = fileUrlMap[1] as Object
    String path = xmap["path"] as String
    String size = xmap["size"] as String
    String fileName = context.data.npath__c as String
    String extensionName = "pdf"
    

    List fileInfo = [["path":path,"size":size,"ext":extensionName,"filename": fileName+"."+extensionName]]
    //更新上传附件
    Map updateData = Maps.newHashMap();
    updateData.put("finalfile__c", fileInfo);
    updateData.put("sign_status__c", "Completed");

    UpdateAttribute attribute = UpdateAttribute.builder().triggerWorkflow(true).build();
    
    def result = Fx.object.update(apiName, keyId, updateData,attribute).result() as Map
    log.info("result:" + result)
    
    UIAction alertAction = AlertAction.builder()
      .type("default")
      .text("Signed Docs Fetched Success!")
      .build()
    
    // Return the AlertAction
    return alertAction