/**
 * @author 彭黄振Jame
 * @codeName Triggers
 * @description Triggers handlers使用
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
class Triggers {
  
    interface Handler { void handle() }

    private List<Triggers.Handler> handlers = []

    Triggers bind(Triggers.Handler h) {
        if (h) handlers << h
        return this
    }

    Triggers bind(Closure c) {
        if (c) handlers << ({ -> c() } as Triggers.Handler)   // 包一层
        return this
    }

    void manage() { handlers.each { it.handle() } }

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
