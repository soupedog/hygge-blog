export const md_template_table: string = "|     表头     |     表头     |     表头     |\n" +
    "|:----------:|:----------:|:----------:|\n" +
    "| 示例内容 | 示例内容 | 示例内容 |\n\n";
export const md_template_code: string = "\n```java\n" +
    "```\n";

export const md_template_scheduled_tasks: string = "- [ ] 未完成任务\n" +
    "- [x] 完成任务\n";

export const md_template_summary: string = "<details>\n" +
    "  <summary>摘要</summary>\n" +
    "  <p>详情内容</p>\n" +
    "</details>\n";

export const md_template_acronym: string = "<acronym title=\"全称详情\">缩略语</acronym>";

export const key_draft: string = "md_draft";

export const editor_id_for_browser: string = "editor_id_browser";
export const editor_id_for_editor: string = "editor_id_editor";

// 就是说不做安全校验直接渲染 Markdown 文本里的 html 标签等，常规方案是通过 sanitize-html 库做验证
export function allowAll(html: string) {
    return html
}