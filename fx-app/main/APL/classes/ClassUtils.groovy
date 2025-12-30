/**
 * @author 彭黄振Jame
 * @codeName ClassUtils
 * @description Class Utils
 * @createTime 2025-10-22
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */


/**
 * @type classes
 * @returntype
 * @namespace library
 */
@groovy.transform.CompileStatic
class ClassUtils {
    
    /** 静态编译：只负责拿到 Class */
    static Class<?> classForName(String clsName) {

        String targetName = 'fx.custom.apl.script.' + clsName
        // 任意“已加载”的类当锚点
        def anchorClass = this // 拿去当前类的基本属性
        def cl = anchorClass.getClassLoader() // 不会触发 CPS 黑名单
        def targetClass = cl.loadClass(targetName)//获取定义类
        return targetClass as Class // 字段访问，安全
    }
    /** 动态编译：一次封装，处处可用 */
    // @groovy.transform.CompileDynamic
    static Object invokeStatic(String clsName, String methodName, Object... args) {
        Class<?> clazz = classForName(clsName)
        def util = ClassUtils.newInstance(clsName)
        Class[] paramTypes = args.collect { it?.class } as Class[]
        return clazz.getDeclaredMethod(methodName, paramTypes)
                    .invoke(util, args)
    }
    
    /* 下面这两个方法同样动态，调用者仍无需注解 */
    static Object invoke(String clsName, String methodName, Object instance, Object... args) {
        Class<?> clazz = classForName(clsName)
        Class[] paramTypes = args.collect { it?.class } as Class[]
        return clazz.getDeclaredMethod(methodName, paramTypes)
                    .invoke(instance, args)
    }
    
    /* 快速 new 实例 */
    static Object newInstance(String clsName, Object... args) {
        Class<?> clazz = classForName(clsName)
        Class[] paramTypes = args.collect { it?.class } as Class[]
        return clazz.getDeclaredConstructor(paramTypes)
                    .newInstance(args)
    }
    
    //对外提供的方法
    static String action() {
        return "sucesss"
    }

    //debug 时候的入口方法
    static void main(String[] args) {
        def id = ClassUtils.invokeStatic('ShortSnowflakeIdUtil', 'nextId')
        log.info(id)
        // 实例方法同理
        def util = ClassUtils.newInstance('ShortSnowflakeIdUtil')
        // log.info(util.getDeclaredMethod('nextId').invoke(null))
        def id2  = ClassUtils.invoke('ShortSnowflakeIdUtil', 'nextId', util)
        log.info(id2)
    }

}
