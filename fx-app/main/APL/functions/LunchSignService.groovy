/**
 * @author 彭黄振Jame
 * @codeName LunchSignService
 * @description Lunch Sign Service
 * @createTime 2025-10-11
 * @bindingObjectLabel Service Report ESignature
 * @bindingObjectApiName adobe_sign__c
 * @函数需求编号
 */
    String key = ESignInfo.getKey();
    log.info(key)
    String baseUrl = ESignInfo.getBaseURL();
    log.info(baseUrl)
    
    String keyId = context.data._id as String;
    String innerMail = context.data.employee_email__c as String;
    String innerName = context.data.employee_name__c as String;
    
    Map serviceReport = context.data.sign_file__c as Map
    log.info("serviceReport:" + serviceReport[0]['path']);
    String nPath = context.data.npath__c as String;
    
    //  1. Transient Documents
    String filePath = serviceReport[0]['path'] as String;
    String uri = "/api/rest/v6/transientDocuments"
    
    String transientDocumentId = ESTransientDocService.uploadAndGetTransientId(baseUrl,key,filePath,nPath,uri)
    
    log.info("transientDocumentId:" + transientDocumentId)
    
    // 更新adobe sign 的transientDocumentId
    Map objectMap = ["multi_line_text__c": transientDocumentId]
    Map resultBBB = Fx.object.update("adobe_sign__c", keyId, objectMap, null, ActionAttribute.create()).result(); 
    log.info("=====resultBBB=====" + resultBBB)

    log.info("1. Transient Documents  send  end>>>>>>>>>>>>>>>>>>>")
      
      
     // 2. create agreement 

    // 协议头名称
    String signName = "MyTestAgreement"
    String agreementURI = "/api/rest/v6/agreements"
    
    //outter mail attach
    
    List outterSingers = context.data.signers_name__c as List 
    
    // String contactEmailStrs = "";
    List participantOuterSetsInfoList =[]
    //遍历 outterSingers
    if (outterSingers) {
      
      outterSingers.each {
        item ->
        log.info(item)
        
        //客户联系人
        String contact_id = item
        
        //根据主键contact_id查询联系人对象ContactObj数据
        
        APIResult contactRet = Fx.object.findOne(
          "ContactObj",
          FQLAttribute.builder()
            .columns(["_id", "name", "email"])
            .queryTemplate(QueryTemplate.AND(["_id": QueryOperator.EQ(contact_id)]))
            .build(),
          SelectAttribute.builder()
            .build()
        );
        if (contactRet.isError()) {
          log.info(contactRet.message());
        }
        
        //联系人邮箱
        String email = contactRet.data["email"] as String
        String name = contactRet.data["name"] as String
            
        List outterMembersList = []
        
        Map outterEmailMap = [:]
        
        outterEmailMap.put("email",email)  
        outterMembersList.add(outterEmailMap)
        
        Map participantOutterMap = [:]
        
        participantOutterMap.put("order",1)
        participantOutterMap.put("role","SIGNER")
        participantOutterMap.put("name",name)
        participantOutterMap.put("memberInfos",outterMembersList)
        participantOuterSetsInfoList.add(participantOutterMap)
        
      }
    }
    
    //提交合约并获取ID
    String agreementId = ESAgreementService.createAgreement(signName,
                                                            baseUrl,
                                                            key,
                                                            agreementURI,
                                                            transientDocumentId,
                                                            innerMail,
                                                            innerName,
                                                            participantOuterSetsInfoList
                                                            ); 
    
    log.info("agreementId:" + agreementId)
    
     // 更新adobe sign 的agreementId
    def objectMapCCC = ["agreementId__c": agreementId,"signature_status__c": "In Progress"]
    def resultCCC = Fx.object.update("adobe_sign__c", keyId, objectMapCCC, null, ActionAttribute.create()).result() as Map
    log.info("resultCCC:" + resultCCC)