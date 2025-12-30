/**
 * @author 彭黄振Jame
 * @codeName LunchSignService
 * @description Lunch Sign Service
 * @createTime 2025-10-11
 * @bindingObjectLabel Service Report ESignature
 * @bindingObjectApiName adobe_sign__c
 * @函数需求编号
 */

    
    ESignInfo.configName = "Docusign"
    String key = DocuSignAuth.getAccessToken() as String
    log.info "accessToken:$key"
    String baseUrl = ESignInfo.getBaseURL()
    log.info "baseUrl:$baseUrl"
    String accountId = ESignInfo.getAccountId()
    log.info "AccountId:$accountId"
    
    List signers = context.data.signers__c as List 
    String keyId = context.data._id as String;
    String innerMail = context.data.initiator_email__c as String;
    String innerName = context.data.initiator_name__c as String;

    Map serviceReport = context.data.sign_file__c as Map
    log.info("serviceReport:" + serviceReport[0]['path']);
    String nPath = context.data.npath__c as String;
    
    String agreementId = ""
    
    //  1. Transient Documents
    String filePath = serviceReport[0]['path'] as String;
    String uri = "/restapi/v2.1/accounts/"+accountId+"/envelopes"
    List participants = signers 
    Integer num = 1
    def signer = [
                      email       : innerMail,
                      name        : innerName,
                      recipientId : num as String,
                      routingOrder: num as String,
                      tabs        : [
                          signHereTabs: [          // 注意：这里是 List
                              [
                                  anchorString : '/sig1/',
                                  anchorYOffset: '10',
                                  anchorXOffset: '20'
                              ]
                          ]
                      ]
                  ]
    List recipientList = [signer]
    
    if( participants ){
      
        participants.each{
            item->
            //客户联系人
            String contact_id = item
            num++
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
            String email1 = contactRet.data["email"] as String
            String name1 = contactRet.data["name"] as String
            // String contId = contactRet.data["_id"] as String
            recipientList.add([
                          email       : email1,
                          name        : name1,
                          recipientId : num as String,
                          routingOrder: num as String,
                          tabs        : [
                              signHereTabs: [          // 注意：这里是 List
                                  [
                                      anchorString : '/sig1/',
                                      anchorYOffset: '10',
                                      anchorXOffset: '20'
                                  ]
                              ]
                          ]
              ])
          
        }
    }
    
    def envelopeJson = [
        emailSubject: '请签署'+nPath,
        documents: [[
            documentId: '1',
            name: nPath
        ]],
        recipients: [
            signers: recipientList
        ],
        status: 'sent'
    ] as Map
    log.info "envelopeJson:$envelopeJson"
    
    agreementId = ESTransientDocService.uploadAndGetDocusignTransientId(baseUrl,key,filePath,nPath,uri,envelopeJson)
    
    log.info("agreementId:" + agreementId)
    
    // 更新adobe sign 的agreementId
    def objectMapCCC = ["agreementId__c": agreementId,"sign_status__c": "In Progress","signersCount__c":num]
    def resultCCC = Fx.object.update("ESign_Record__c", keyId, objectMapCCC, null, ActionAttribute.create()).result() as Map
    log.info("resultCCC:" + resultCCC)
    
    