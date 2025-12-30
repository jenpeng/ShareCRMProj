/**
 * @author 彭黄振Jame
 * @codeName TransientDocService
 * @description 上传文件到 Adobe Sign 拿到 transientDocumentId
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
class ESTransientDocService {
    /**
     * 上传文件到 Adobe Sign 拿到 transientDocumentId
     * @param baseUrl  Adobe 根地址
     * @param key      Bearer token
     * @param filePath 文件在 FX 中的完整路径（serviceReport[0].path）
     * @param nPath    文件名（带后缀，例如 "test" -> 实际用 "test.pdf"）
     * @return  transientDocumentId 或 null
     */
    static String uploadAndGetTransientId(String baseUrl,
                                          String key,
                                          String filePath,
                                          String nPath,
                                          String uri) {

        InputStream inputStream = Fx.file.downloadStream(filePath)[1]['inputStream'] as InputStream

        String url = baseUrl + uri
        // 一行搞定
        List ret = HttpsService.postMultipart(url, key, nPath, inputStream) as List
        Boolean error = ret[0] as Boolean
        HttpResult result = ret[1] as HttpResult
        String message = ret[2] as String
    
        if (error) {
            log.info("TransientDocService error: " + message)
            return null
        }
        

        Map jsonMap = Fx.json.parse(result["content"] as String)
        return jsonMap["transientDocumentId"] as String
    }
    
    /**
     * 上传文件到 Docusign 拿到 transientDocumentId
     * @param baseUrl  Adobe 根地址
     * @param key      Bearer token
     * @param filePath 文件在 FX 中的完整路径（serviceReport[0].path）
     * @param nPath    文件名（带后缀，例如 "test" -> 实际用 "test.pdf"）
     * @return  transientDocumentId 或 null
     */
    static String uploadAndGetDocusignTransientId(String baseUrl,
                                          String key,
                                          String filePath,
                                          String nPath,
                                          String uri,
                                          Map envelopeJson) {

        InputStream inputStream = Fx.file.downloadStream(filePath)[1]['inputStream'] as InputStream

        String url = baseUrl + uri
        // 一行搞定
        List ret = HttpsService.postDocsignMultipart(url, key, nPath, inputStream,envelopeJson) as List
        Boolean error = ret[0] as Boolean
        HttpResult result = ret[1] as HttpResult
        String message = ret[2] as String
    
        if (error) {
            log.info("TransientDocService error: " + message)
            return null
        }
        
        log.info "TransientDocService result: $result"
        Map jsonMap = Fx.json.parse(result["content"] as String)
        return jsonMap["envelopeId"] as String
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