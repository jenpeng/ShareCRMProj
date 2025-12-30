/**
 * @author 彭黄振Jame
 * @codeName ShortSnowflakeIdUtil
 * @description 20位以内短雪花ID（无位运算、无循环，APL兼容）
 * @createTime 2025-10-19
 * @bindingObjectLabel --
 * @bindingObjectApiName NONE
 * @函数需求编号
 */

/**
 * @type classes
 * @returntype String
 * @namespace library
 */
class ShortSnowflakeIdUtil {

    /*========== 可外部注入的参数 ==========*/
    private static Long workerId     = 1L   // 0~31
    private static Long datacenterId = 1L   // 0~31

    /*========== 常量 ==========*/
    private static Long TWPOCH = 1420041600000L   // 2015-01-01 00:00:00
    private static Long SEQUENCE_BITS  = 12L
    private static Long WORKER_BITS    = 5L
    private static Long DATACENTER_BITS= 5L

    private static Long MAX_SEQUENCE   = 4095L      // 2^12-1
    private static Long MAX_WORKER     = 31L        // 2^5-1
    private static Long MAX_DATACENTER = 31L        // 2^5-1

    /*========== 运行时变量 ==========*/
    private static Long sequence  = 0L
    private static Long lastTime  = -1L

    /*========== 对外 API ==========*/
    /** 生成下一个全局唯一ID（字符串） */
    public static String nextId() {
        return generateId()
    }

    /*========== 私有实现 ==========*/
    private static synchronized String generateId() {
        Long now = System.currentTimeMillis()
        if (now < lastTime) {
            log.info("Clock moved backwards")
        }

        if (now == lastTime) {
            sequence = sequence + 1
            if (sequence > MAX_SEQUENCE) {
                // 序列耗尽，直接等到下一毫秒（APL不支持while，用递归代替）
                return waitNextMillis(lastTime)
            }
        } else {
            sequence = 0
        }

        lastTime = now

        /* 代替位运算的乘加拼装 */
        Long timePart  = (now - TWPOCH) * 4194304L          // 左移22位：2^(5+5+12)
        Long dataPart  = datacenterId * 131072L             // 左移17位：2^(5+12)
        Long workPart  = workerId     * 4096L               // 左移12位
        Long id = timePart + dataPart + workPart + sequence

        return id.toString()
    }

    /** 递归等待下一毫秒（替代while） */
    private static String waitNextMillis(Long last) {
        Long t = System.currentTimeMillis()
        if (t <= last) {
            // 仍没越过上一毫秒，继续递归
            return waitNextMillis(last)
        }
        // 时间已更新，重新生成ID
        return generateId()
    }

    /*========== 调试入口 ==========*/
    static void main(String[] args) {
        log.info("ShortSnowflakeIdUtil test => " + nextId())
        def str = '{"baseurl":"https://www.fxiaoke.com","method":{"POST":"/postmethod","GET":"/getmethod"},"cert":"ZXlKaGJHY2lPaUpJVXpJMU5pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SnpkV0lpT2lKU1pYRjFaWE4wVkc5clpXNGlMQ0pwYzNNaU9pSkRSVkF0VTJWeWRtVnlJaXdpWlc1MFpYSndjbWx6WlVGalkyOTFiblFpT2lKbWEzUmxjM1E0TlRBM0lpd2laVzUwWlhKd2NtbHpaVWxrSWpvNE1qUXpNRElzSW5ObGMzTnBiMjVKWkNJNklqWTRZMk14TTJZNExUZ3lNVEV0TkRnd01TMDRNVEkwTFdKaFlqSm1NR0prTTJJNE55SXNJbVY0Y0NJNk16Z3pOVFE0TWpJME15d2lkWE5sY2tsa0lqb3hNREF3TENKcFlYUWlPakUzTmpBd05qQTRNakY5LjNQcC14MTB2TmJFYUpzZ3ZBQTJzZmc5Zk5RV09sWjhUTGFtY2cyaEpuYjA="}'
        def encode1 = Fx.crypto.MD5.encode(str)
        log.info("MD5 test => " +encode1)
    }
}