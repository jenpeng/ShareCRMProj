# TRAE增量闭环(≤1000)
规则：1先规划→2拆≤30min任务→3按TDD执行→4校验→5迭代。
命令：/plan /split /do ID /review /next
步骤：
/plan：写docs/ymd-功能/001-plan.md，含目标+验收。
/split：同目录002-task.md，任务表|ID|描述|估时|状态|哈希|
/do：切分支feat/ymd-功能，TDD+提交消息=T-ID-type:desc，填003-ID-report.md。
/review：全绿+验收清单✅，缺陷写004-review.md，不过则标REPLAN。react calmly.
/next：REPLAN回①；否则合并main，写005-metrics.md(误差、逃逸)。
全程md落盘，豁免热修24h补文。