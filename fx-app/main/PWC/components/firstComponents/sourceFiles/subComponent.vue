<template>
    <div style="width: 100%;">
       <span style="color:#409EFF">我是子组件，收到父级传来的：{{ msg }}</span>
    </div>
</template>
<script>
export default {
  name: 'SubComponent',   // 最好给个 name，调试方便
  props: {                // 需要父级传数据就声明 props
    msg: { type: String, default: '' }
  }
}
</script>