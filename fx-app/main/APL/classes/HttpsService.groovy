/**
 * @author 彭黄振Jame
 * @codeName HttpsService
 * @description 公共callout service
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
class HttpsService {
  
    /**
     * 统一 POST 发送 JSON（Adobe 协议创建场景）
     * @param url        完整地址
     * @param bearerKey  Bearer token
     * @param bodyMap    请求体 Map（会被 StringBody 包装）
     * @return [error, HttpResult, message] 三元组
     */
    static def postJson(String url, String bearerKey, Map bodyMap) {
        StringBody body = StringBody.builder().content(bodyMap).build()
        Request req = Request.builder()
                             .method("POST")
                             .url(url)
                             .timeout(75_000)
                             .retryCount(0)
                             .header("Content-Type", "application/json; charset=UTF-8")
                             .header("Authorization", "Bearer " + bearerKey)
                             .body(body)
                             .build()
        return http.execute(req)   // 返回三元组
    }
    
    static def getWithBearer(String fullUrl, String bearerKey) {
        Map headers = [
            "Accept"        : "application/json",
            "Authorization" : "Bearer " + bearerKey
        ]
        def (Boolean error, HttpResult result, String errorMsg) =
                Fx.http.get(fullUrl, headers, 10_000, false, 0)
        return [error, result, errorMsg]
    }
    
    static def getFileWithBearer(String fullUrl, String bearerKey) {
        Map headers = [
            "Content-Type"        : "application/pdf",
            "Authorization" : "Bearer " + bearerKey
        ]
        def (Boolean error, HttpResult result, String errorMsg) =
                Fx.http.get(fullUrl, headers, 10_000, false, 0)
        return [error, result, errorMsg]
    }
    
    /**
     * 统一上传 multipart/form-data（Adobe transientDocuments 场景）
     * @param url        完整地址
     * @param bearerKey  Bearer token
     * @param fileName   文件名（不带后缀）
     * @param inputStream 文件流
     * @return [error, HttpResult, message] 三元组
     */
    static def postMultipart(String url,
                             String bearerKey,
                             String fileName,
                             InputStream inputStream) {
    
        def multi = MultipartBody.builder()
                                 .addPart("FileName", fileName)
        multi.addPart("File", inputStream, fileName + '.pdf', 'application/pdf')
        def finalMulti = multi.build()
    
        Request req = Request.builder()
                             .method("POST")
                             .url(url)
                             .timeout(70_000)
                             .retryCount(0)
                             .header("Content-Type", "multipart/form-data")
                             .header("accept", "application/json")
                             .header("Authorization", "Bearer " + bearerKey)
                             .body(finalMulti)
                             .build()
    
        return http.execute(req)   // 依旧是三元组
    }
    
    /**
     * 统一上传 multipart/form-data; boundary=boundary123（Docusign transientDocuments 场景）
     * @param url        完整地址
     * @param bearerKey  Bearer token
     * @param fileName   文件名（不带后缀）
     * @param inputStream 文件流
     * @return [error, HttpResult, message] 三元组
     */
    static def postDocsignMultipart(String url,
                             String bearerKey,
                             String fileName,
                             InputStream inputStream,Map envelopeJson) {
                               
        def envStr = json.toJson(envelopeJson)
        
        def jsonPart = Strings.toUTF8Bytes(envStr)
                               
        def multi = MultipartBody.builder()
        // JSON 部分
        .addPart("form-data; name=\"envelopeDefinition\"", jsonPart, "request.json", "application/json")
        // PDF 文件部分
        .addPart("file; filename=\"${fileName}\"; documentid=1", inputStream, "${fileName}", "application/pdf")

        def finalMulti = multi.build()
    
        // 3️⃣ 构建 HTTP 请求
        def req = Request.builder()
            .method("POST")
            .url(url)
            .timeout(80_000)
            .retryCount(0)
            .header("Content-Type", "multipart/form-data; boundary=boundary123")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer ${bearerKey}")
            .body(finalMulti)
            .build()
    
        return http.execute(req)   // 依旧是三元组
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
