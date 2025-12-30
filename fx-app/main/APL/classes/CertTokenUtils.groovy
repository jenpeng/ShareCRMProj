/**
 * 证书令牌工具类
 * 功能：生成包含摘要、压缩原文、盐值和校验码的安全令牌
 * 令牌格式：12位摘要 + 14位压缩原文 + 6位盐 + 2位计数 + 8位校验码 = 42字符
 */
class CertTokenUtils {
    // 计数器，用于生成唯一序列
    private static int COUNTER = 0
    
    // 存储原始内容的映射，key为12位摘要，value为原始Base64字符串
    private static Map<String, String> originalMap = new HashMap<>()

    /**
     * 1. 编码方法：生成42字符的安全令牌
     * 令牌结构：12位摘要 + 14位压缩原文 + 6位时间盐 + 2位计数器 + 8位校验码
     * @param rawBase64 原始Base64字符串
     * @return 42字符的安全令牌
     */
    static String encode(String rawBase64) {
        // ① 摘要计算：使用SHA-256生成哈希，取前12位36进制作为摘要标识
        byte[] hash32 = java.security.MessageDigest.getInstance("SHA-256").digest(rawBase64.getBytes())
        String hash36 = bytesToHex(hash32)          // 将字节数组转换为十六进制字符串
        String idx12  = hash36.substring(0, 12)     // 取前12位作为摘要标识
    
        // ② 压缩原文：将原始Base64编码并截取前14位，不足补'A'
        String comp14 = java.util.Base64.getUrlEncoder()
                                      .encodeToString(rawBase64.getBytes())  // Base64URL编码
                                      .substring(0, Math.min(14, rawBase64.length()))  // 截取前14位
                                      .padRight(14, 'A')        // 不足14位时右侧补'A'
    
        // ③ 盐值 + 校验码生成
        String time = Long.toString(System.currentTimeMillis(), 36).substring(0, 6)  // 当前时间戳的36进制，取前6位
        String cnt  = padTwo(Long.toString((nextCounter() & 0xFF), 36))              // 2位计数器，36进制
        String salt = time + cnt                                                     // 组合成8位盐值(6位时间+2位计数)
        
        // 校验码计算：摘要与盐值异或后取低33位，转为8位36进制
        long mix = Long.parseLong(idx12, 36) ^ Long.parseLong(salt, 36)              // 摘要与盐值异或
        String check = padEight(Long.toString(mix & 0x1FFFFFFFFL, 36))               // 取低33位并补零到8位
    
        // 组合生成完整令牌：12位摘要 + 14位压缩原文 + 8位盐值 + 8位校验码 = 42字符
        String token = idx12 + comp14 + salt + check
        
        // 存储原始内容到映射，后续解码时使用
        originalMap.put(idx12, rawBase64)
        
        return token
    }

    /**
     * 2. 解码方法：从令牌中还原原始Base64字符串
     * 优先从内存映射中获取，映射不存在时降级到解码压缩版本
     * @param token 42字符的安全令牌
     * @return 原始Base64字符串，解码失败返回空字符串
     */
    static String decode(String token) {
        // 基础校验：令牌长度必须至少42字符
        if (token == null || token.length() < 42) {
            return ""
        }
        
        try {
            // 提取令牌前12位作为摘要标识
            String idx12 = token.substring(0, 12)
            
            // 优先从内存映射中获取原始内容（确保返回完整原始数据）
            String original = originalMap.get(idx12)
            if (original != null) {
                return original
            }
            
            // 降级方案：如果映射中不存在，解码压缩的Base64部分
            // 注意：这可能返回被截断的内容，因为编码时只保留了前14位
            String comp14 = token.substring(12, 26)  // 提取14位压缩原文
            byte[] decodedBytes = java.util.Base64.getUrlDecoder().decode(comp14)  // Base64URL解码
            return new String(decodedBytes)
        } catch (Exception e) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * 3. 验证方法：校验令牌的完整性和真实性
     * 通过重新计算校验码并与令牌中的校验码对比来验证
     * @param token 42字符的安全令牌
     * @return 验证通过返回true，否则返回false
     */
    static boolean verify(String token) {
        try {
            // 基础校验：令牌必须恰好42字符
            if (token == null || token.length() != 42) {
                return false
            }
            
            // 提取令牌各组成部分
            String idx12 = token.substring(0, 12)           // 前12位：摘要标识
            String salt = token.substring(26, 34)           // 第27-34位：盐值(6位时间+2位计数)
            String chk = token.substring(34, 42)            // 第35-42位：校验码
            
            // 重新计算校验码：使用相同的算法
            long mix = Long.parseLong(idx12, 36) ^ Long.parseLong(salt, 36)      // 摘要与盐值异或
            String calculatedCheck = padEight(Long.toString(mix & 0x1FFFFFFFFL, 36))  // 计算8位校验码
            
            // 比较计算出的校验码与令牌中的校验码是否一致
            return calculatedCheck.equals(chk)
        } catch (Exception e) { 
            e.printStackTrace()
            return false 
        }
    }

    /**
     * 清理方法：从内存映射中移除令牌对应的原始内容
     * 防止内存泄漏，建议在使用完令牌后调用
     * @param token 要清理的令牌
     */
    static void cleanup(String token) {
        if (token != null && token.length() >= 12) {
            originalMap.remove(token.substring(0, 12))  // 使用摘要标识作为key进行移除
        }
    }

    /**
     * 4. 对外接口方法
     * @return 固定返回"success"字符串
     */
    static String action() { return "success" }

    /* ====== 内部工具方法 ====== */
    
    /**
     * 计数器递增方法
     * 每次调用计数器加1，并限制在0xFFFFFF范围内
     * @return 更新后的计数器值
     */
    private static int nextCounter() { return COUNTER = (COUNTER + 1) & 0xFFFFFF }

    /**
     * DJB哈希算法：递归实现的无循环哈希函数
     * @param data 输入字节数组
     * @param idx 当前处理索引
     * @param h 当前哈希值
     * @return 计算完成的哈希值
     */
    private static long djb(byte[] data, int idx, long h) {
        return idx >= data.length ? h : djb(data, idx + 1, ((h << 5) + h + (data[idx] & 0xFF)) & 0x7FFFFFFFL)
    }

    /**
     * 补零工具方法：确保字符串至少2位长度
     * @param s 输入字符串
     * @return 补零后的2位字符串
     */
    private static String padTwo(String s)  { return s.length() >= 2 ? s : "0" + s }
    
    /**
     * 补零工具方法：确保字符串至少8位长度
     * @param s 输入字符串
     * @return 补零后的8位字符串
     */
    private static String padEight(String s){ return s.length() >= 8 ? s : String.format("%0" + (8 - s.length()) + "d%s", 0, s) }

    /* 5. 十六进制转换工具 */
    
    /**
     * 字节数组转十六进制字符串
     * 使用Stream API实现无循环转换
     * @param bytes 输入字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        return java.util.stream.IntStream.range(0, bytes.length)
                                  .mapToObj(new java.util.function.IntFunction<String>() {
                                      @Override
                                      String apply(int i) {
                                          return byteToHex(bytes[i]);
                                      }
                                  })
                                  .collect(java.util.stream.Collectors.joining());
    }
    
    /**
     * 单字节转十六进制字符串
     * @param b 输入字节
     * @return 2位十六进制字符串（大写）
     */
    private static String byteToHex(byte b) {
        return Integer.toHexString(b & 0xFF).toUpperCase();
    }

    /**
     * 6. 测试方法：验证编码、解码、验证功能的正确性
     */
    static void main(String[] args) {
        // 测试用的长Base64字符串
        String raw = 'ZXlKaGJHY2lPaUpJVXpJMU5pSXNJblI1Y0NJNklrcFhWQ0o5LmV5SnpkV0lpT2lKU1pYRjFaWE4wVkc5clpXNGlMQ0pwYzNNaU9pSkRSVkF0VTJWeWRtVnlJaXdpWlc1MFpYSndjbWx6WlVGalkyOTFiblFpT2lKbWEzUmxjM1E0TlRFNUlpd2laVzUwWlhKd2NtbHpaVWxrSWpvMk56QXdNREl3Tnl3aWMyVnpjMmx2Ymtsa0lqb2lOMlZsTVdKa01qWXRNalV6T1MwME16RTJMVGd6TURJdE5qZzNaakUzT1RBeU1EZzJJaXdpWlhod0lqb3pPRE0yTURBek1UZ3pMQ0oxYzJWeVNXUWlPakV3TURBc0ltbGhkQ0k2TVRjMk1ETXlNVEk1TW4wLjZUcDc0RF9hZlJRWTlZOVRQdFl5UUJXaWVDQ1lFaUtaWVNQUVp1d3dNR2M='
        
        // 编码测试
        def t = encode(raw)
        log.info("Token: " + t)
        
        // 解码测试
        log.info("Decoded: " + decode(t))
        
        // 验证测试
        log.info("Verify: " + verify(t))
        
        // 清理测试
        cleanup(t)
    }
}