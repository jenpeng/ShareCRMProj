/**
 * 平台检测和适配逻辑
 */

import { ref, onMounted } from 'vue'

/**
 * 平台检测和适配组合式函数
 */
export function usePlatform() {
  const isMobile = ref(false)
  const isPC = ref(true)
  
  /**
   * 检测平台类型
   */
  const detectPlatform = () => {
    const userAgent = navigator.userAgent.toLowerCase()
    const mobileKeywords = [
      'android', 'webos', 'iphone', 'ipad', 'ipod', 
      'blackberry', 'iemobile', 'opera mini'
    ]
    
    isMobile.value = mobileKeywords.some(keyword => 
      userAgent.includes(keyword)
    )
    isPC.value = !isMobile.value
  }
  
  /**
   * 平台特定的属性适配
   */
  const adaptPlatformProps = (props) => {
    const adapted = { ...props }
    
    // 移动端特定适配
    if (isMobile.value) {
      // 移动端不支持的类型映射到默认支持的类型
      if (!['default', 'primary', 'success', 'danger'].includes(adapted.variant)) {
        adapted.variant = 'default'
      }
      
      // 移动端禁用圆角和圆形
      adapted.round = false
      adapted.circle = false
      
      // 移动端尺寸标准化
      if (adapted.size === 'mini') {
        adapted.size = 'small'
      }
    }
    
    return adapted
  }
  
  /**
   * 检查属性是否在当前平台支持
   */
  const isPropSupported = (propName) => {
    const pcOnlyProps = ['size', 'round', 'circle', 'icon', 'autofocus', 'nativeType']
    
    if (isMobile.value && pcOnlyProps.includes(propName)) {
      return false
    }
    
    return true
  }
  
  onMounted(() => {
    detectPlatform()
  })
  
  return {
    isMobile,
    isPC,
    detectPlatform,
    adaptPlatformProps,
    isPropSupported
  }
}