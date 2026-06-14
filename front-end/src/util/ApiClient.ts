import {message} from 'antd';
import UrlHelper from './UrlHelper.ts';
import StorageHelper from './StorageHelper.ts';
import {ClientScope, StorageKey} from '../enums/EnumKeeper.ts';
import PropertiesHelper from './PropertiesHelper.ts';
import {httpClient} from './HttpClient.ts';

const apiZIndex = 20001;

export interface HyggeResponse<T> {
    code: number;
    msg?: string;
    main?: T;
}

const emptyResponse = {} as HyggeResponse<any>;

export interface ApiHook<T> {
    successHook?: (input?: HyggeResponse<T>) => void;
    beforeHook?: () => void;
    finallyHook?: () => void;
}

export interface UserDto {
    uid: string;
    userAvatar: string;
    userSex: string;
    biography?: string;
    birthday?: number;
    phone?: string;
    email?: string;
}

export interface SignInRequest {
    ac?: string;
    pw?: string
}

export interface SignInResponse {
    user?: UserDto;
    token: string;
    refreshKey: string;
    deadline: number;
}

export class UserClient {
    private static clientScopeInstance: ClientScope = ClientScope.WEB;

    /** 初始化（在 App 组件中调用一次） */
    static init(clientScope: ClientScope) {
        this.clientScopeInstance = clientScope;
    }

    static getCurrentUser(): UserDto | undefined {
        return StorageHelper.get<UserDto>(StorageKey.USER_INFO);
    }

    static removeCurrentUser() {
        StorageHelper.remove(StorageKey.USER_UID);
        StorageHelper.remove(StorageKey.USER_TOKEN);
        StorageHelper.remove(StorageKey.USER_REFRESH_KEY);
        StorageHelper.remove(StorageKey.USER_INFO);
    }

    static getCurrentScope(): ClientScope {
        return this.clientScopeInstance;
    }

    static getDefaultContentType(): string {
        return 'application/json';
    }

    static getHeader(currentHeader?: any): any {
        let result;

        if (currentHeader == null) {
            result = {};
            // @ts-ignore
            result['Content-Type'] = this.getDefaultContentType();
        } else {
            result = currentHeader;
        }
        result.scope = this.getCurrentScope();

        let currentSecretKey = UrlHelper.getQueryString('secretKey');
        if (currentSecretKey != null) {
            result.secretKey = currentSecretKey;
        }

        let currentUId = StorageHelper.get<string>(StorageKey.USER_UID);
        let currentToken = StorageHelper.get<string>(StorageKey.USER_TOKEN);
        let currentRefreshKey = StorageHelper.get<string>(StorageKey.USER_REFRESH_KEY);
        if (currentUId != null && currentToken != null && currentRefreshKey != null) {
            result.uid = currentUId;
            result.token = currentToken;
        }
        return result;
    }

    static async signIn(input?: SignInRequest): Promise<SignInResponse> {
        let requestHeader = null;
        let requestData;
        if (input != null && PropertiesHelper.isStringNotEmpty(input.ac) && PropertiesHelper.isStringNotEmpty(input.pw)) {
            requestData = {
                'password': input.pw,
                'userName': input.ac
            };
        } else {
            requestData = {};
            requestHeader = UserClient.getHeader();
            if (PropertiesHelper.isStringNotEmpty(requestHeader.uid)) {
                requestHeader.refreshKey = StorageHelper.get(StorageKey.USER_REFRESH_KEY);
            } else {
                requestHeader = null;
            }
        }
        let clientResponse;

        if (requestHeader != null) {
            StorageHelper.set(StorageKey.AUTO_LOGIN_DISABLED, '已禁止再次触发自动登陆');
            message.success({content: '尝试用令牌刷新秘钥自动登录。', style: {zIndex: apiZIndex}});
            // 刷新令牌
            clientResponse = await httpClient.post('/sign/in', {}, {headers: requestHeader});
        } else {
            // 账号密码登录
            clientResponse = await httpClient.post('/sign/in', requestData, {headers: UserClient.getHeader()});
        }

        // 能到这里说明没被拦截器拦截，已经正确请求到后端服务器
        const response: HyggeResponse<any> = clientResponse.data;

        if (clientResponse.data.main.code == 200) {
            // 每次登录成功则运行重试刷新令牌至少一次
            let user = response.main.user;
            StorageHelper.set(StorageKey.USER_UID, user.uid);
            StorageHelper.set(StorageKey.USER_TOKEN, response.main.token);
            StorageHelper.set(StorageKey.USER_REFRESH_KEY, response.main.refreshKey);
            StorageHelper.set(StorageKey.USER_INFO, user);
            StorageHelper.remove(StorageKey.AUTO_LOGIN_DISABLED);
        }

        return response.main;
    }
}
