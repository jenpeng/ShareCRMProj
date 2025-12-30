/**
 * @author 彭黄振Jame
 * @codeName ESAgreementService
 * @description 提交Agreement服务
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


class ESAgreementService {
/**
     * 创建 Adobe Sign 协议
     * @param signName       协议名称
     * @param baseUrl        Adobe 根地址
     * @param key            Bearer token
     * @param transientId    上一步拿到的 transientDocumentId
     * @param innerMail      内部员工邮箱
     * @param innerName      内部员工姓名
     * @param participantOuterSetsInfoList   外部联系人信息
     * @return  agreementId 或 null
     */
    static String createAgreement(String signName,
                                  String baseUrl,
                                  String key,
                                  String uri,
                                  String transientId,
                                  String innerMail,
                                  String innerName,
                                  List participantOuterSetsInfoList) {

        // 1. 协议头
        Map agreeMap = [
            name          : signName,
            signatureType : "ESIGN",
            state         : "IN_PROCESS",
            fileInfos     : [[transientDocumentId: transientId]]
        ]

        // 2. 内部签署人
        List participantSetsInfoList = []
        participantSetsInfoList << [
            order       : "1",
            role        : "SIGNER",
            name        : innerName,
            memberInfos : [[email: innerMail]]
        ]

        participantSetsInfoList.addAll(participantOuterSetsInfoList);
        agreeMap["participantSetsInfo"] = participantSetsInfoList
        String url = baseUrl + uri;//"/api/rest/v6/agreements"
        // --- 改成 List 接收，避开解构 ---
        List postRet = HttpsService.postJson(url, key, agreeMap) as List
        Boolean err       = postRet[0] as Boolean
        HttpResult res    = postRet[1] as HttpResult
        String msg        = postRet[2] as String
        if (err) {
            log.info("AgreementService error: " + msg)
            return null
        }
        Map jsonMap = Fx.json.parse(res["content"] as String)
        return jsonMap["id"] as String
    }
    
    //docusign 专属获得去签名的url
    static String getDocusignAgreementURL(String baseUrl,String key, String uri, Map bodyMap){
      String url = baseUrl+uri
      log.info('url:'+url)
      log.info "body is : $bodyMap"
      StringBody body = StringBody.builder().content(bodyMap).build()
      Request req = Request.builder()
                           .method("POST")
                           .url(url)
                           .timeout(75_000)
                           .retryCount(0)
                           .header("Content-Type", "application/json")
                           .header("Accept", "application/json")
                           .header("Authorization", "Bearer " + key)
                           .body(body)
                           .build()
                           
      def(Boolean error, HttpResult result, String message) =Fx.http.execute(req)

      if (error) {
          log.error("http request error: "+ error +" errorMessage: " + message + " result: " + result)
          return null
      }
      
      
      String contentMap = result.content as String
      Map xmap = Fx.json.parse(contentMap)
      def tmpurl = xmap['url']
      log.info "url:$tmpurl"
      return tmpurl as String
    }
    
    //Adobe 专属获得去签名的url
    static String getAgreementURL(String baseUrl,
                                  String key,
                                  String uri,
                                  String agreementId
                                  ){
      String url = baseUrl + uri + agreementId + "/signingUrls"
      log.info('url:'+url)
      // --- 同样改成 List 接收 ---
      List getRet = HttpsService.getWithBearer(url, key) as List
      Boolean error        = getRet[0] as Boolean
      HttpResult fileResult = getRet[1] as HttpResult
      String errorMessage   = getRet[2] as String

      if (error) {
          Fx.log.info("http 请求出错 ： " + errorMessage)
          return null
      }
      if (fileResult.statusCode != 200) {
          Fx.log.info("http 响应错误 ：" + fileResult.content)
          return null
      }
      
     
      Map contentMap = fileResult["content"] as Map
      
      List signingUrlSetInfoList = contentMap["signingUrlSetInfos"] as List
      
      Map signingUrlsMap = signingUrlSetInfoList[0] as Map 
      
      List signingUrlsList = signingUrlsMap["signingUrls"] as List
      
      Map esignUrlMap = signingUrlsList[0] as Map
      
      return esignUrlMap["esignUrl"] as String
    }
    
    //获取签署完毕的URL
    static String getAgreementSignedFileURL(
                                            String baseUrl,
                                            String key,
                                            String uri,
                                            String agreementId
                                            ){
                                              
      String url = baseUrl + uri + agreementId + "/combinedDocument/url"
      log.info('url:'+url)
      
      // --- 同样改成 List 接收 ---
      List getRet = HttpsService.getWithBearer(url, key) as List
      Boolean error        = getRet[0] as Boolean
      HttpResult fileResult = getRet[1] as HttpResult
      String errorMessage   = getRet[2] as String
      if (error) {
          Fx.log.info("http 请求出错 ： " + errorMessage)
          return null
      }
      if (fileResult.statusCode != 200) {
          Fx.log.info("http 响应错误 ：" + fileResult.content)
          return null
      }
      
      Map fileUrl = fileResult["content"] as Map
      
      log.info(fileUrl)
      
      return fileUrl.url as String
                                              
    }
    
    //获取签署完毕的Docusign流文件
    static Map getAgreementDocuSignedFileURL(
                                            String baseUrl,
                                            String key,
                                            String uri,
                                            String agreementId
                                            ){
                                              
      String url = baseUrl + uri + "/envelopes/"+agreementId+"/documents/combined"
      log.info('url:'+url)
      
      // Map map = ["id": agreementId, "name": "协议"]
      // StringBody body = StringBody.builder().content(map).build()
      
      Request request = Request.builder()
                        .method("GET")
                        .url(url)
                        .timeout(7000)
                        .retryCount(0)
                        .header("Authorization", "Bearer "+key)
                        .header("Content-Type", "application/json")
                        .build()
      def res = Fx.file.uploadFileByStream(request)
      log.info(res)
      
      return res as Map
                                              
    }
    
    //获取签署完毕的Adobe流文件
    static Map getAgreementAcrobatSignedFileURL(
                                            String baseUrl,
                                            String key,
                                            String uri,
                                            String agreementId
                                            ){
                                              
      String url = baseUrl + uri + "/agreements/"+agreementId+"/combinedDocument"
      log.info('url:'+url)
      
      Request request = Request.builder()
                        .method("GET")
                        .url(url)
                        .timeout(7000)
                        .retryCount(0)
                        .header("Authorization", "Bearer "+key)
                        .header("Accept", "application/pdf")
                        .build()
      def res = Fx.file.uploadFileByStream(request)
      log.info(res)
      
      return res as Map
                                              
    }
    //对外提供的方法
    static String action() {
        return "sucesss"
    }
    
    //debug 时候的入口方法
    static void main(String[] args) {
        String ret = action();
        log.info(ret)
    }

}
