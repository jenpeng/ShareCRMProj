/**
 * @author 彭黄振Jame
 * @codeName MyHandler
 * @description MyHandler
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
class MyHandler implements Triggers.Handler {
  
    private String tag
    MyHandler(String tag) { this.tag = tag }
    void handle() { 
      log.info("my handler: " + tag)
      println "my handler [$tag]" 
      
    }

    //对外提供的方法
    static String action() {
        return "sucesss"
    }

    //debug 时候的入口方法
    static void main(String[] args) {
        def t = new Triggers()
        t.bind(new MyHandler('A'))
         .bind(new MyHandler('B'))
         .manage()
    }

}
