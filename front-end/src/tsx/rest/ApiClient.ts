import axios from "axios";
import {message} from "antd";
import {PropertiesHelper, UrlHelper} from "../utils/UtilContainer";

axios.defaults.baseURL = "http://localhost:8080/blog-service/api";

axios.interceptors.response.use(function (response) {
    if (response.data == null) {
        return null;
    }
    let code = response.data?.code;

    // 对响应数据做点什么
    if (code == 200) {
        return response;
    } else if (code == 400 || code < 500000) {
        message.warn(response.data.msg, 5);
        return null;
    } else {
        message.error(response.data.msg, 5);
        return null;
    }
}, function (error) {
    // 对响应错误做点什么
    return Promise.reject(error);
});

export interface HyggeResponse<T> {
    code: number;
    msg?: string;
    main?: T;
}

export interface UserResponse {
    uid: string;
    userAvatar: string;
    userSex: string;
    biography?: string;
    birthday?: number;
    phone?: string;
    email?: string;
}

export interface SignInResponse {
    user?: UserResponse;
    token: string;
    refreshKey: string;
    deadline: number;
}

export class UserService {
    static getCurrentUser(): UserResponse | null | undefined {
        let currentUserStringValue = localStorage.getItem('currentUser');
        if (PropertiesHelper.isStringNotNull()) {
            return null;
        }
        return JSON.parse(currentUserStringValue!) as UserResponse;
    }

    static removeCurrentUser() {
        localStorage.removeItem('uid');
        localStorage.removeItem('token');
        localStorage.removeItem('refreshKey');
        localStorage.removeItem('currentUser');
    }

    static getHeader(currentHeader?: any): any {
        let result;

        if (currentHeader == null) {
            result = {};
        } else {
            result = currentHeader;
        }
        result.scope = "WEB";

        let currentSecretKey = UrlHelper.getQueryString("secretKey");
        if (currentSecretKey != null) {
            result.secretKey = currentSecretKey;
        }

        let currentUId = localStorage.getItem("uid");
        let currentToken = localStorage.getItem("token");
        let currentRefreshKey = localStorage.getItem("refreshKey");

        if (currentUId == null || currentToken == null || currentRefreshKey == null) {
            this.removeCurrentUser();
        } else {
            result.uid = currentUId;
            result.token = currentToken;
        }
        return result;
    }

    static signIn(ac: string, pw: string,
                  successHook?: (i: HyggeResponse<SignInResponse>) => void,
                  beforeHook?: () => void,
                  finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        let requestData = null;
        if (PropertiesHelper.isStringNotNull(ac) && PropertiesHelper.isStringNotNull(pw)) {
            requestData = {
                "password": pw,
                "userName": ac
            };
        }

        axios.post("/sign/in", requestData).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<SignInResponse> = response.data;
                    successHook(data);

                    if (data.main?.user != null) {
                        let user = data.main.user;
                        localStorage.setItem('uid', user.uid);
                        localStorage.setItem('token', data.main.token);
                        localStorage.setItem('refreshKey', data.main.token);
                        localStorage.setItem('currentUser', JSON.stringify(user));
                    }
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        })
    }
}