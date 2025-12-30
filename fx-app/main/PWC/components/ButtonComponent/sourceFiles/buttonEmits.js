/**
 * PWC Button 组件事件定义
 */

export const buttonEmits = {
  // 点击事件
  click: (event) => event instanceof Event,
  
  // 鼠标按下事件
  mousedown: (event) => event instanceof Event,
  
  // 鼠标抬起事件  
  mouseup: (event) => event instanceof Event,
  
  // 状态变化事件
  'state-change': (state) => typeof state === 'string',
  
  // 加载开始事件
  'loading-start': () => true,
  
  // 加载结束事件
  'loading-end': (success) => typeof success === 'boolean'
}