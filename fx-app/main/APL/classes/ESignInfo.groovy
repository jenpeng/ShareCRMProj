/**
 * @author 彭黄振Jame
 * @codeName ESignInfo
 * @description fetch ESign configuration
 * @createTime 2025-10-11
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */


/**
 * @type classes
 * @returntype
 * @namespace library
 */
class ESignInfo {
     /* 1. 外部可写的查询条件 */
  public static String configName = 'Adobe Sign';
  
  // 缓存配置
  // 2. 缓存：初始化为空 Map，避免 null
  private static Map config = [:]

  // 私有方法：加载配置
  private static synchronized Map loadConfig() {
    // 已经加载过（即使是空配置）就直接返回
        if (config) return config
        
        def (Boolean error1, QueryResult queryResult, String errorMessage1) = Fx.object.find(
          'ESignConfig__c',
          FQLAttribute.builder()
            .columns(["_id", "name", "BaseUrl__c", "ClientID__c", "Securitykey__c","isActive__c","pem__c","accountId__c","auth_url__c","userId__c"])
            .queryTemplate(
              QueryTemplate.AND(
                ["name": QueryOperator.EQ(configName)], ["isActive__c": QueryOperator.EQ(true)]
              )
            )
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
                  .searchRichTextExtra(true)
                  .build()
        )
  
        if (error1) {
          log.error("获取对象异常" + errorMessage1)
          config = [
            signBaseURL: "",
            baseURL    : "",
            authURL    : "",
            clientId    : "",
            userId      : "",
            accountId   : "",
            key        : "",
            pemKey     :"",
            error: "didnot find data"
          ]
          log.info(config);
          
          return config
        } else {
          def dataList = queryResult.dataList as List //数据列表
          def total = queryResult.total //符合条件的数据总条数
          def size = queryResult.size  //本次返回的数据条数
          dataList.each {
                e ->
                    //dosomething，将示例逻辑细化至业务数据层
                    def map = e as Map
                    
                    config = [
                      baseURL    : map["BaseUrl__c"],
                      authURL    : map["auth_url__c"],
                      clientId   : map["ClientID__c"],
                      userId     : map["userId__c"],
                      accountId  : map["accountId__c"],
                      key        : map["Securitykey__c"],
                      pemKey     : map["pem__c"],
                      error: ""
                    ]
                    log.info "config:${config.baseURL}"
              
            } 
          return config
        }
    
  }

    // 对外暴露的静态方法
    static String getBaseURL()     { return loadConfig().baseURL }
    static String getAuthURL()     { return loadConfig().authURL }
    static String getClientId()     { return loadConfig().clientId }
    static String getAccountId()     { return loadConfig().accountId }
    static String getUserId()     { return loadConfig().userId }
    static String getKey()         { return loadConfig().key }
    static String getpemKey()         { return loadConfig().pemKey }
    //对外提供的方法
    static String action() {
        return "sucesss"
    }

    //debug 时候的入口方法
    static void main(String[] args) {
        ESignInfo.configName = 'Adobe Sign'
        log.info("baseURL: ${ESignInfo.getBaseURL()}") 
        log.info("baseURL: ${ESignInfo.getClientId()}")
        log.info("key: ${ESignInfo.getKey()}")
    }

}