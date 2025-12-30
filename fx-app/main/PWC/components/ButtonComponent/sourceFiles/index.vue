<template>
  <div class="demo">
    <!-- 基础按钮 -->
    <pwc-button>默认按钮</pwc-button>
    <pwc-button variant="primary">主要按钮</pwc-button>
    <pwc-button variant="success">成功按钮</pwc-button>
    
    <!-- 状态按钮 -->
    <pwc-button :state="buttonState" @click="toggleState">
      状态: {{ buttonState }}
    </pwc-button>
  </div>
</template>

<script>
import PwcButton from './PwcButton.vue'

export default {
  name: 'ButtonDemo',
  components: {
    PwcButton
  },
  data() {
    return {
      buttonState: 'default'
    }
  },
  methods: {
    toggleState() {
      const states = ['default', 'loading', 'success', 'error']
      const currentIndex = states.indexOf(this.buttonState)
      const nextIndex = (currentIndex + 1) % states.length
      this.buttonState = states[nextIndex]
    }
  }
}
</script>