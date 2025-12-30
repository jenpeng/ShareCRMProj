/**
 * @author 彭黄振Jame
 * @codeName ESign Received Service
 * @description ESign Received Service
 * @createTime 2025-10-17
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */


/**
 * @type classes
 * @returntype
 * @namespace library
 */
class ESignReceivedService {
    static Map getESignReceived(String repsBody){
        def body = Fx.json.parse(repsBody) 
        log.info("I am here")
        def eventResourceType = body.eventResourceType //用于判断是否来自于“agreement”的事件请求
        def event = body.event //用于判断是否完成
        def participantRole = body.participantRole as String//签署人角色
        def actingUserEmail = body.actingUserEmail as String//签署人邮箱
        def agreement = body.agreement as Map 
        
        String agreementId = agreement.id //用于找到主表记录进行更新
        String agreeStatus = agreement.status as String 
        String apiName = "ESign_Record__c"
        
        if(eventResourceType == "agreement" &&
           participantRole == "SIGNER" &&
           event =="AGREEMENT_ACTION_COMPLETED") {//全部签署完成
           Map updateData = Maps.newHashMap();
           
           if(agreeStatus == "SIGNED"){
             updateData.put("sign_status__c", "COMPLETED");
           }
            
           def (Boolean error, QueryResult data, String errorMessage) = Fx.object.find(apiName, //对象apiName
                                                                            FQLAttribute.builder()
                                                                                    .columns(["_id", "name"]) //需要返回的字段
                                                                                    .queryTemplate(QueryTemplate.AND(["agreementId__c": QueryOperator.EQ(agreementId)])) //查询条件
                                                                                    .build(),
                                                                           SelectAttribute.builder()
                                                                                    .needCalculate(true) //是否实时处理计算字段，默认true
                                                                                    .needQuote(true) //是否实时处理引用字段，默认true
                                                                                    .calculateCount(true) //是否实时处理统计字段，默认true
                                                                                    .fillExtendInfo(false) //是否补充字段扩展信息，比如查找关联字段的主属性、人员部门名称等，以${字段apiName}__r返回，默认false
                                                                                    .needOptionLabel(false) //是否返回单选、多选字段的label，以${字段apiName}__r返回，默认false
                                                                                    .convertQuoteForView(false) //引用字段是否返回label，如果为true，引用字段的value通过${字段apiName}__v返回，默认false
                                                                                    .needInvalid(false) //是否返回已作废的数据，默认false
                                                                                    .needRelevantTeam(false) //是否返回相关团队，默认false
                                                                                    .build())
            if (error) {
                log.error("获取对象异常" + errorMessage)
            
            } else {
                log.info("I am here1")
                def dataList = data.dataList as List
                String keyId = ""
                dataList.each {
                    e ->
                        //dosomething，将示例逻辑细化至业务数据层
                        def map = e as Map
                        keyId = map['_id'] as String
                }
                def (Boolean err, Map reslt, String errMessage) =  Fx.object.update(apiName, keyId, updateData, UpdateAttribute.builder().triggerWorkflow(true).build())
                log.info("I am here2")
                if (err) {
                    log.error("获取对象异常" + errMessage)
                }
                else {
                    log.info("update data is : $reslt")
                    collectSigners(actingUserEmail, keyId, agreement)
                }

            }

        } 
      
        return body
    }
    
    static Map collectSigners(String emailStr,String parentId,Map agreeMap) {
        String userAPIName = "ContactObj"
        def (Boolean error, QueryResult data, String errorMessage) = Fx.object.find(userAPIName, //对象apiName
                                                                          FQLAttribute.builder()
                                                                                    .columns(["_id", "name","email"]) //需要返回的字段
                                                                                    .queryTemplate(QueryTemplate.AND(["email": QueryOperator.EQ(emailStr)])) //查询条件
                                                                                    .build(),
                                                                          SelectAttribute.builder()
                                                                                    .needCalculate(true) //是否实时处理计算字段，默认true
                                                                                    .needQuote(true) //是否实时处理引用字段，默认true
                                                                                    .calculateCount(true) //是否实时处理统计字段，默认true
                                                                                    .fillExtendInfo(false) //是否补充字段扩展信息，比如查找关联字段的主属性、人员部门名称等，以${字段apiName}__r返回，默认false
                                                                                    .needOptionLabel(false) //是否返回单选、多选字段的label，以${字段apiName}__r返回，默认false
                                                                                    .convertQuoteForView(false) //引用字段是否返回label，如果为true，引用字段的value通过${字段apiName}__v返回，默认false
                                                                                    .needInvalid(false) //是否返回已作废的数据，默认false
                                                                                    .needRelevantTeam(false) //是否返回相关团队，默认false
                                                                                    .build())
         if (error) {
                log.error("获取对象异常" + errorMessage)
                return
            } else {
                log.info("I am here5")
                def dataList = data.dataList as List
                String contactId = ""
                dataList.each {
                    e ->
                        //dosomething，将示例逻辑细化至业务数据层
                        def map = e as Map
                        contactId = map['_id'] as String
                }
                // def contactId = map['_id'] as String
                log.info("I am here6")
                Map mainData = Maps.newHashMap();
                mainData.put("esignRec__c", parentId);
                mainData.put("sign_status__c", agreeMap.status);
                mainData.put("signer__c", contactId);
                Map detailData = [:];
                def (Boolean error1, Map data1, String errorMessage1) = Fx.object.create("ESign_Report_Record__c", mainData, detailData, CreateAttribute.builder().build())
                
                if (error1) {
                    log.error("获取对象异常error1" + errorMessage1)
                    return
                }
                else {
                    log.info("create mainData is : $data1")
                }
                return data1
            }
    }
    
    //对外提供的方法
    static String action() {
        return "sucesss"
    }

    //debug 时候的入口方法
    static void main(String[] args) {
        
        log.info(action()) 

    }

}
