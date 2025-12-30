<template>
  <fx-dialog :visible.sync="visible" title="选择使用类型" width="400px">
    <fx-select v-model="selectedType" placeholder="请选择使用类型">
      <fx-option label="类型A" value="A" />
      <fx-option label="类型B" value="B" />
    </fx-select>
    <template #footer>
      <fx-button @click="visible = false">取消</fx-button>
      <fx-button type="primary" @click="handleConfirm">确认</fx-button>
    </template>
  </fx-dialog>
</template>

<script>
export default {
  data() {
    return {
      visible: true,
      selectedType: '',
    };
  },
  methods: {
    async handleConfirm() {
      const result = await FxUI.callAPL('DocuSignService__c', {
        useType: this.selectedType,
        recordId: this.$route.params.id, // 当前记录ID
      });
      alert(result.message || '执行完成');
      this.visible = false;
    },
  },
};
</script>