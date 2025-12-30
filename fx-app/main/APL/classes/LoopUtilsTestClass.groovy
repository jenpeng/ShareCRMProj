/**
 * @author 彭黄振Jame
 * @codeName AAATest
 * @description AAATest
 * @createTime 2025-10-25
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */
import fx.custom.apl.jar.LoopUtils

/**
 * @type classes
 * @returntype
 * @namespace library
 */
class LoopUtilsTestClass {

    //对外提供的方法
    static String action() {
        return "sucesss"
    }

    //debug 时候的入口方法
    static void main(String[] args) {
        log.info "=== Groovy调用LoopUtils示例 ==="
        
        // 1. 标准for循环示例
        log.info "[1. 标准for循环示例 (0到9)]："
        LoopUtils.forLoop(0, 10, 1) { i ->
            log.info "$i "
        }
        
        // 示例1: for循环中使用breakLoop()方法 - 无需return或throw
        log.info "[1. for循环中使用breakLoop()]:"
        LoopUtils.forLoop(0, 10, 1, { int i ->
            log.info "  循环: $i"
            if (i == 5) {
                log.info "[执行breakLoop]"
                LoopUtils.breakLoop()  // 直接调用方法，无需return或throw
            }
        })
        
        // 示例2: for循环中使用continueLoop()方法 - 无需return或throw
        log.info "[2. for循环中使用continueLoop()]:"
        LoopUtils.forLoop(0, 10, 1, { int i ->
            if (i % 2 == 0) {
                log.info "跳过偶数: $i"
                LoopUtils.continueLoop()  // 直接调用方法，无需return或throw
            }
            log.info " 处理奇数: $i"
        })
        
        // 2. 带步长的for循环示例
        log.info "[2. 带步长的for循环示例 (0到20，步长为2)]："
        LoopUtils.forLoop(0, 20, 2) { i ->
            log.info "$i "
        }
        
        // 示例: while循环中使用breakLoop()和continueLoop()
        log.info "[while循环中使用breakLoop()和continueLoop()]:"
        def count = 0
        LoopUtils.whileLoop({ count < 10 }, {
            count++
            log.info "While循环: $count"
            
            if (count % 3 == 0) {
                log.info "跳过能被3整除的数: $count"
                LoopUtils.continueLoop()
            }
            
            if (count == 7) {
                log.info "执行breakLoop"
                LoopUtils.breakLoop()
            }
        })
        
        
    }

}
