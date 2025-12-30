/**
 * PWC Button 状态管理
 */

export const buttonState = {
  data() {
    return {
      internalState: this.state,
      internalLoading: this.loading,
      internalDisabled: this.disabled
    }
  },
  
  computed: {
    // 计算是否加载中
    isLoading() {
      return this.internalState === 'loading' || this.internalLoading
    },
    
    // 计算是否禁用
    isDisabled() {
      return this.internalState === 'disabled' || this.internalDisabled
    }
  },
  
  watch: {
    state(newVal) {
      this.internalState = newVal
    },
    
    loading(newVal) {
      this.internalLoading = newVal
    },
    
    disabled(newVal) {
      this.internalDisabled = newVal
    }
  },
  
  methods: {
    /**
     * 设置按钮状态
     */
    setState(newState) {
      if (this.internalState !== newState) {
        const oldState = this.internalState
        this.internalState = newState
        
        this.$emit('state-change', newState, oldState)
        
        // 触发特定状态事件
        if (newState === 'loading') {
          this.$emit('loading-start')
        } else if (oldState === 'loading') {
          const success = newState === 'success'
          this.$emit('loading-end', success)
        }
      }
    },
    
    /**
     * 带状态管理的异步操作
     */
    async withAsyncState(asyncFn, options = {}) {
      const {
        autoReset = this.autoReset,
        resetDelay = this.resetDelay,
        successState = 'success',
        errorState = 'error'
      } = options
      
      try {
        this.setState('loading')
        const result = await asyncFn()
        this.setState(successState)
        
        // 自动重置状态
        if (autoReset && successState !== 'default') {
          setTimeout(() => this.setState('default'), resetDelay)
        }
        
        return result
      } catch (error) {
        this.setState(errorState)
        
        // 自动重置状态
        if (autoReset && errorState !== 'default') {
          setTimeout(() => this.setState('default'), resetDelay)
        }
        
        throw error
      }
    },
    
    /**
     * 点击事件处理
     */
    handleClick(event) {
      if (this.isDisabled || this.isLoading) {
        event.preventDefault()
        return
      }
      
      this.$emit('click', event)
    }
  },
  
  mounted() {
    // 设置初始状态
    if (this.loading) {
      this.internalState = 'loading'
    }
    if (this.disabled) {
      this.internalState = 'disabled'
    }
  }
}