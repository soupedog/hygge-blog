interface ImportMetaEnv {
    readonly VITE_ICP_INFO: string
    readonly VITE_CODE_PREFIX: string
    readonly VITE_CODE: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}