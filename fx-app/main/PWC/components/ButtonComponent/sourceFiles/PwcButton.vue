<template>
  <button
    :class="buttonClasses"
    :disabled="isDisabled"
    :autofocus="autofocus"
    :type="nativeType"
    @click="handleClick"
    @mousedown="$emit('mousedown', $event)"
    @mouseup="$emit('mouseup', $event)"
  >
    <!-- 加载指示器 -->
    <span
      v-if="isLoading"
      class="pwc-button__loading"
    >
      <i class="pwc-button__icon pwc-icon-loading pwc-button__icon--spin"></i>
      {{ loadingText }}
    </span>
    
    <!-- 非加载状态的图标 -->
    <i
      v-else-if="icon && !isLoading"
      :class="['pwc-button__icon', icon]"
    ></i>
    
    <!-- 按钮内容 -->
    <span
      v-if="!isLoading || (isLoading && !loadingText)"
      class="pwc-button__content"
    >
      <slot></slot>
    </span>
    
    <!-- 徽章 -->
    <span
      v-if="badge && !isLoading"
      class="pwc-button__badge"
    >
      {{ badge }}
    </span>
  </button>
</template>

<script>
// 导入拆分后的模块
import { buttonProps } from './button-props.js'
import { buttonState } from './button-state.js'
import { buttonClasses } from './button-classes.js'

// 导入样式
import './pwc-button.css'

/**
 * PWC Button 组件 - 拆分维护版
 */
export default {
  name: 'PwcButton',
  
  // 属性定义
  props: buttonProps,
  
  // 事件定义
  emits: ['click', 'mousedown', 'mouseup', 'state-change', 'loading-start', 'loading-end'],
  
  // 混入状态管理和样式计算
  mixins: [buttonState, buttonClasses],
  
  // 组件特定的方法（如果需要）
  methods: {
    // 这里可以添加组件特有的方法
    // 大部分通用方法已经在 button-state.js 中定义
  }
}
</script>