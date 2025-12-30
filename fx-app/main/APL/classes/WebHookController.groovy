/**
 * @author 彭黄振Jame
 * @codeName WebHookController
 * @description WebHookController
 * @createTime 2025-10-13
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */


/**
 * @type classes
 * @returntype
 * @namespace apl_controller
 */
@AplController(baseUrl = "/apl")
class WebHookController {

    @AplRequestMapping(value = "/query", method = RequestMethod.POST)
    public HttpResponse query(HttpRequest request) {
        String requestBody = new String(request.getBody(), "UTF-8")
        log.info(requestBody)

        Map returnBody = ["code": 0, "data": "ook", "message": "success!"]
        String body = Fx.json.toJson(returnBody)
        return HttpResponse.ok()
                .header("Content-Type", "application/json")
                .body(body)
    }
    
    // @AplRequestMapping(value = "/getmethod", method = RequestMethod.GET)
    // public HttpResponse getMethod(HttpRequest request) {
    //     String requestBody = new String(request.getBody(), "UTF-8")
    //     log.info(requestBody)
    //
    //     Map returnBody = ["xAdobeSignClientId":"ats-1e6bdecd-7ca6-4e5f-9ef6-05068f29712d"]
    //     String body = Fx.json.toJson(returnBody)
    //     return HttpResponse.ok()
    //             .header("Content-Type", "application/json")
    //             .body(body)
    // }

    /**
     * 硬编码方法：获取AplRequestMapping路径映射
     * @return Map<String, String> 方法名到路径的映射
     */
    public Map<String, String> getAplRequestMappingPaths() {
        Map<String, String> pathMapping = [:]
        
        // 硬编码添加所有AplRequestMapping的路径
        pathMapping.put("query", "/apl/query")
        pathMapping.put("getMethod", "/apl/getmethod")
        
        return pathMapping
    }

    public void debug(FunctionContext context, Map<String, Object> args) {
        Map headers = [
                "Content-Type": ["application/json"]
        ]
        Map parameters = [
                "name": ["zhangsan"]
        ]
        byte[] body = [1, 2, 3] as byte[]
        HttpRequest request = new HttpRequest(headers, parameters, body)
        log.info(Fx.json.toJson(query(request)))
        
        // 测试获取路径映射
        log.info("AplRequestMapping Paths: " + Fx.json.toJson(getAplRequestMappingPaths()))
        
        // request = new HttpRequest(headers, parameters, body)
        // log.info("====result: " + Fx.json.toJson(getMethod(request)))
    }
}