import axios, {type AxiosResponse} from 'axios';
import {message} from 'antd';
import UrlHelper from './UrlHelper.ts';
import StorageHelper from "./StorageHelper.ts";
import {ClientScope, StorageKey} from "../enums/EnumKeeper.ts";
import PropertiesHelper from "./PropertiesHelper.ts";

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

axios.defaults.baseURL = UrlHelper.getApiPrefix();
axios.interceptors.response.use(function (axiosResponse) {
    // http 状态码非 200
    if (axiosResponse.status != 200) {
        message.error({content: '网络请求异常！！！', style: {zIndex: apiZIndex}});
        // 相当于控制台抛出异常
        return Promise.reject(axiosResponse);
    }

    // 服务端返回 http 状态码 200
    let response: HyggeResponse<any> = axiosResponse.data;
    let code = response.code;

    // 没有 code 可能不是 application/json 类型 response，既然 http 状态码是 200 ，那也无需拒绝
    if (code == null || code == 200) {
        return axiosResponse;
    }

    // code 不为 200 是后端有特殊规则
    // 处理自动登录相关 code
    if (code == 403002) {
        // 令牌刷新失败，无法自动登录
        UserClient.removeCurrentUser();
        message.warning({content: '自动刷新令牌失败，2 秒内为您跳转回主页', duration: 2, style: {zIndex: apiZIndex}});
        UrlHelper.navigateTo({path: '/', delayTime: 2000});
    } else if (code == 403003) {
        let autoLoginDisabledFlag = StorageHelper.get<string>(StorageKey.AUTO_LOGIN_DISABLED);

        if (autoLoginDisabledFlag) {
            // 已尝试自动登录过仍然失败
            UserClient.removeCurrentUser();
            message.warning({content: '该账号需要重新登陆，2 秒内为您跳转回登陆页', duration: 2, style: {zIndex: apiZIndex}});
            UrlHelper.navigateTo({path: '/signin', delayTime: 2000});
        } else {
            // 自动刷新默认至多刷新一次
            StorageHelper.set(StorageKey.AUTO_LOGIN_DISABLED, '已禁止再次触发自动登陆');

            UserClient.signIn(undefined, undefined, {
                successHook: (response) => {
                    if (response?.code === 200) {
                        message.info({content: '已为您成功自动登录，1 秒内为您跳转回主页', duration: 1, style: {zIndex: apiZIndex}});
                        // 重新登陆成功后需要重置已自动刷新次数为 0
                        StorageHelper.remove(StorageKey.AUTO_LOGIN_DISABLED);
                        UrlHelper.navigateTo({path: '/', delayTime: 1000});
                    } else {
                        // 没 code、code 非 200，都是登录失败，要求重新登录
                        UserClient.removeCurrentUser();
                        message.warning({content: '自动登录失败，1 秒内为您跳转回登录页', duration: 1, style: {zIndex: apiZIndex}});
                        // 刷新秘钥自动登录失败，需要清空本地身份信息
                        UrlHelper.navigateTo({path: '/signin', delayTime: 1000});
                    }
                }
            });
        }
    } else if (code == 403000) {
        // 账号、密码、令牌错误
        UserClient.removeCurrentUser();
        message.warning({content: '已清空错误登陆信息，2 秒内为您跳转回主页', duration: 2, style: {zIndex: apiZIndex}});
        UrlHelper.navigateTo({path: '/', delayTime: 2000});
    } else {
        message.warning({content: response.msg, duration: 10, style: {zIndex: apiZIndex}});
    }
    return axiosResponse;
}, function (error) {
    message.error('未知请求异常！！！')
    // 相当于控制台抛出异常
    return Promise.reject(error);
});

export interface UserDto {
    uid: string;
    userAvatar: string;
    userSex: string;
    biography?: string;
    birthday?: number;
    phone?: string;
    email?: string;
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
        return "application/json";
    }

    static getHeader(currentHeader?: any): any {
        let result;

        if (currentHeader == null) {
            result = {};
            // @ts-ignore
            result["Content-Type"] = this.getDefaultContentType();
        } else {
            result = currentHeader;
        }
        result.scope = this.getCurrentScope();

        let currentSecretKey = UrlHelper.getQueryString("secretKey");
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

    static signIn(ac?: string, pw?: string, hook?: ApiHook<SignInResponse>): Promise<AxiosResponse> {
        if (hook != null && hook.beforeHook != null) {
            hook.beforeHook();
        }

        let requestHeader = null;
        let requestData;
        if (PropertiesHelper.isStringNotEmpty(ac) && PropertiesHelper.isStringNotEmpty(pw)) {
            requestData = {
                "password": pw,
                "userName": ac
            };
        } else {
            requestData = {};
            requestHeader = UserClient.getHeader();
            if (PropertiesHelper.isStringNotEmpty(requestHeader.uid)) {
                requestHeader.refreshKey = localStorage.getItem("refreshKey");
            } else {
                requestHeader = null;
            }
        }
        let request;
        if (requestHeader != null) {
            message.info("尝试用令牌刷新秘钥自动登录")
            // 刷新令牌
            request = axios.post("/sign/in", {}, {headers: requestHeader});
        } else {
            // 账号密码登录
            request = axios.post("/sign/in", requestData, {headers: UserClient.getHeader()});
        }

        request.then((axiosResponse) => {
                let response: HyggeResponse<SignInResponse> = axiosResponse.data;

                if (hook != null && hook.successHook != null && response.code == 200) {
                    let user = response.main!.user!;
                    StorageHelper.set(StorageKey.USER_UID, user.uid);
                    StorageHelper.set(StorageKey.USER_TOKEN, response.main!.token);
                    StorageHelper.set(StorageKey.USER_REFRESH_KEY, response.main!.refreshKey);
                    StorageHelper.set(StorageKey.USER_INFO, user);
                    message.success({content: '登录成功！', duration: 2, style: {zIndex: apiZIndex}});
                    hook.successHook(response);
                }
            }
        ).finally(() => {
            if (hook != null && hook.finallyHook != null) {
                hook.finallyHook();
            }
        });

        return request;
    }
}
