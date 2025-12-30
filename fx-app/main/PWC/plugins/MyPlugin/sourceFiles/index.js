export default class Plugin {
  apply() {
    return [
      {
        event: "form.change.end",
        functional: this.formChangeEnd.bind(this)
      }
    ];
  }
  formChangeEnd(context) {
    let obj = context.collectChange();
    if(obj.masterUpdate.field_2eg9J__c) {//判断当前变更包含某个字段
      return context.bizApi.triggerUIEvent({triggerField: 'field_2eg9J__c'});
    }
  }
} // field_2eg9J__c  换成字段的api即可。