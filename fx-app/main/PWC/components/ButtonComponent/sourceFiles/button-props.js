/**
 * PWC Button 组件属性定义
 */

export const buttonProps = {
  // 按钮变体类型
  variant: {
    type: String,
    default: 'default',
    validator: (value) => [
      'default', 'primary', 'success', 'warning', 'danger', 'info', 'text'
    ].includes(value)
  },
  
  // 按钮尺寸
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['mini', 'small', 'medium', 'large'].includes(value)
  },
  
  // 按钮状态
  state: {
    type: String,
    default: 'default',
    validator: (value) => [
      'default', 'loading', 'disabled', 'success', 'error'
    ].includes(value)
  },
  
  // 是否朴素按钮
  plain: {
    type: Boolean,
    default: false
  },
  
  // 是否圆角按钮
  round: {
    type: Boolean,
    default: false
  },
  
  // 是否圆形按钮
  circle: {
    type: Boolean,
    default: false
  },
  
  // 是否加载中状态
  loading: {
    type: Boolean,
    default: false
  },
  
  // 是否禁用状态
  disabled: {
    type: Boolean,
    default: false
  },
  
  // 图标类名
  icon: {
    type: String,
    default: ''
  },
  
  // 是否默认聚焦
  autofocus: {
    type: Boolean,
    default: false
  },
  
  // 原生 type 属性
  nativeType: {
    type: String,
    default: 'button',
    validator: (value) => ['button', 'submit', 'reset'].includes(value)
  },
  
  // 是否块级按钮
  block: {
    type: Boolean,
    default: false
  },
  
  // 徽章计数
  badge: {
    type: [String, Number],
    default: null
  },
  
  // 加载中文本
  loadingText: {
    type: String,
    default: '加载中...'
  },
  
  // 自动重置状态
  autoReset: {
    type: Boolean,
    default: true
  },
  
  // 重置延迟时间
  resetDelay: {
    type: Number,
    default: 2000
  }
}