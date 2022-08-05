import axios from "axios";
import {message} from "antd";

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

export interface SignInResponse {
    token: string;
    refreshKey: string;
    deadline: number;
}

export class UserService {

    static signIn(ac: string, pw: string,
                  successHook?: (i: HyggeResponse<SignInResponse>) => void,
                  beforeHook?: () => void,
                  finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.post("/sign/in", {
            "password": pw,
            "userName": ac
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<SignInResponse> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        })
    }
}