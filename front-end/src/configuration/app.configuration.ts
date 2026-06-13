export interface AppConfiguration {
    icpInfo: string
    codePrefix: string
    code: string
}

// 直接导出配置对象
// 请根据 .env.example 作为模板，在其同级目录创建 .env.local 与 .env.production.local 文件
export const appConfiguration: AppConfiguration = {
    icpInfo: import.meta.env.VITE_ICP_INFO || "默认ICP备号",
    codePrefix: import.meta.env.VITE_CODE_PREFIX || "默认公网安备",
    code: import.meta.env.VITE_CODE || "00000000"
}

// 开发环境打印配置（仅开发环境）
if (import.meta.env.DEV) {
    console.log('App Config:', appConfiguration)
}