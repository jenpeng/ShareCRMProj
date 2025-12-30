/**
 * @author 彭黄振Jame
 * @codeName Adobe Webhook Controller
 * @description Adobe Webhook Controller
 * @createTime 2025-10-16
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */


/**
 * @type classes
 * @returntype
 * @namespace library
 */
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
class AdobeWebhookController {
    
    @AplRequestMapping(value = "/postmethod", method = RequestMethod.POST)
    public HttpResponse query(HttpRequest request) {
        String requestBody = new String(request.getBody(), "UTF-8")
        log.info(requestBody)
        Map returnBody = ESignReceivedService.getESignReceived(requestBody) as Map
        // Map returnBody = ["status": 200, "data": "ook", "message": "success!","xAdobeSignClientId":"UB7E5BXCXY"]
        String body = Fx.json.toJson(returnBody)
        return HttpResponse.ok()
                .header("Content-Type", "application/json")
                .header("x-adobesign-clientid", "UB7E5BXCXY")
                // .header("x-adobesign-clientid", "ats-1e6bdecd-7ca6-4e5f-9ef6-05068f29712d")
                .body(body)
    }
    
    @AplRequestMapping(value = "/getmethod", method = RequestMethod.POST)
    public HttpResponse getmethod(HttpRequest request) {
        String requestBody = new String(request.getBody(), "UTF-8")
        log.info("request body: " + requestBody)
        Map returnBody = ["xAdobeSignClientId":"UB7E5BXCXY","status":200]
        String body = Fx.json.toJson(returnBody)
        return HttpResponse.ok()
                .header("Content-Type", "application/json")
                .header("x-adobesign-clientid", "UB7E5BXCXY")
                .body(body)
    }

    /**
     * 硬编码方法：获取AplRequestMapping路径映射
     * @return Map<String, String> 方法名到路径的映射
     */
    public Map<String, String> getAplRequestMappingPaths() {
        Map<String, String> pathMapping = [:]
        
        // 硬编码添加所有AplRequestMapping的路径
        pathMapping.put("POST", "/apl/postmethod")
        pathMapping.put("GET", "/apl/getmethod")
        
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
        def clz = ClassUtils.invokeStatic('AdobeWebhookController', 'postmethod',request)
        // log.info(Fx.json.toJson(query(request)))
        log.info(Fx.json.toJson(clz))
        
        // 测试获取路径映射
        log.info("AplRequestMapping Paths: " + Fx.json.toJson(getAplRequestMappingPaths()))
        
        // request = new HttpRequest(headers, parameters, body)
        // log.info("====result: " + Fx.json.toJson(getMethod(request)))
    }
}
