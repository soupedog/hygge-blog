// 普通枚举类型会被擦除，erasableSyntaxOnly 激活后用 const 类实例替代 enum
export class StorageKey {
    static readonly USER_INFO = 'USER_INFO';
    static readonly USER_UID = 'USER_UID';
    static readonly USER_TOKEN = 'USER_TOKEN';
    static readonly USER_REFRESH_KEY = 'USER_REFRESH_KEY';
    static readonly AUTO_LOGIN_DISABLED = 'AUTO_LOGIN_DISABLED';

    private constructor() {
    }
}

export class ClientScope {
    static readonly WEB = 'WEB';
    static readonly PHONE = 'PHONE';

    private constructor() {
    }
}

export class HomeKeywordType {
    static readonly POST = 'POST';
    static readonly QUOTE = 'QUOTE';

    private constructor() {
    }
}

export class TimeType {
    // 2022-8-4
    static readonly yyyy_mm_dd = '0';
    // 21:39:44
    static readonly hh_mm_ss = '1';
    // 2022-8-4 21:39:44
    static readonly yyyy_mm_dd_hh_mm_ss = '2';

    private constructor() {
    }
}