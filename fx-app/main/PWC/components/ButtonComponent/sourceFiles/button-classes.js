/**
 * PWC Button 样式类计算
 */

export const buttonClasses = {
  computed: {
    /**
     * 计算按钮的 CSS 类
     */
    buttonClasses() {
      const classes = ['pwc-button']
      
      // 变体类型
      if (this.variant && this.variant !== 'default') {
        classes.push(`pwc-button--${this.variant}`)
      }
      
      // 尺寸
      if (this.size && this.size !== 'medium') {
        classes.push(`pwc-button--${this.size}`)
      }
      
      // 状态
      if (this.internalState && this.internalState !== 'default') {
        classes.push(`pwc-button--${this.internalState}`)
      }
      
      // 样式变体
      if (this.plain) {
        classes.push('pwc-button--plain')
      }
      
      if (this.round) {
        classes.push('pwc-button--round')
      }
      
      if (this.circle) {
        classes.push('pwc-button--circle')
      }
      
      if (this.block) {
        classes.push('pwc-button--block')
      }
      
      // 加载状态
      if (this.isLoading) {
        classes.push('pwc-button--loading')
      }
      
      // 禁用状态
      if (this.isDisabled) {
        classes.push('pwc-button--disabled')
      }
      
      return classes
    }
  }
}