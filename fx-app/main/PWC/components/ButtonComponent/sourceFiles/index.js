/**
 * PWC Button 组件入口文件
 */

import PwcButton from './PwcButton.vue'

// 默认导出组件
export default PwcButton

// 命名导出所有工具模块（方便其他组件复用）
export { 
  buttonProps,
  buttonState,
  buttonClasses
}

// 组件安装函数（用于Vue插件）
export function installPwcButton(app) {
  app.component('PwcButton', PwcButton)
}

// 自动安装（当通过script标签引入时）
if (typeof window !== 'undefined' && window.Vue) {
  window.Vue.component('PwcButton', PwcButton)
}