interface ImportMetaEnv {
    readonly VITE_ICP_INFO: string
    readonly VITE_CODE_PREFIX: string
    readonly VITE_CODE: string
    readonly VITE_HOST_FE: string
    readonly VITE_HOST_BE: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}