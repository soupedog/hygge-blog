import axios, {type AxiosInstance, type AxiosRequestConfig, type AxiosResponse} from 'axios';
import {type HyggeResponse, UserClient} from './ApiClient.ts';
import {message} from 'antd';
import StorageHelper from './StorageHelper.ts';
import {StorageKey} from '../enums/EnumKeeper.ts';
import UrlHelper from './UrlHelper.ts';

export class HttpClient {
    private readonly axiosInstance: AxiosInstance;

    constructor(timeout?: number) {
        this.axiosInstance = axios.create({
            baseURL: UrlHelper.getApiPrefix(),
            timeout: timeout ? timeout : 0
        });
        this.initInterceptors();
    }

    private initInterceptors() {
        // 请求拦截器
        this.axiosInstance.interceptors.request.use(
            (config) => {
                // 可以 config.headers.xxxx = xxxx 自定义设置 Header 等，此处为啥也没干仅演示扩展方式
                return config;
            },
            // 不一定是 error 对象，实际是链路上 Promise.reject(T) 所传入的 T
            (error) => Promise.reject(error)
        );

        // 返回值拦截器
        this.axiosInstance.interceptors.response.use(
            // 返回值处理器
            async (axiosResponse) => {
                // http 状态码非 200
                if (axiosResponse.status != 200) {
                    // 相当于中断正常流程的 Promise 流程，主动触发异常处理器方法
                    return Promise.reject(axiosResponse);
                }

                // 服务端返回 http 状态码 200
                const response: HyggeResponse<any> = axiosResponse.data;
                const code = response.code;

                // 没有 code 可能不是 application/json 类型 response，既然 http 状态码是 200 ，那也无需拒绝
                if (code == null || code == 200) {
                    return axiosResponse;
                }

                let autoLoginDisabled = StorageHelper.get<string>(StorageKey.AUTO_LOGIN_DISABLED);

                // token 过期，唯一有自动登录必要的请求类型
                if (!autoLoginDisabled && code == 403003) {
                    // 允许自动登录
                    const signInResponse = await UserClient.signIn();
                    if (signInResponse != null) {
                        // 自动登录成功
                        const originalRequest = axiosResponse.config;
                        // 更新身份认证信息 重试原请求
                        originalRequest.headers.uid = StorageHelper.get(StorageKey.USER_UID);
                        originalRequest.headers.token = StorageHelper.get(StorageKey.USER_TOKEN);

                        return this.axiosInstance(originalRequest);
                    }
                }

                // token 刷新令牌类登录信息有误
                if (autoLoginDisabled && code == 403002) {
                    UserClient.removeCurrentUser();
                    UrlHelper.navigateTo({path: '/signin', needReload: true, delayTime: 2000});
                }

                // 账号密码类登录信息有误
                if (code == 403000) {
                    // token 校验不匹配
                    // 清空本地错误用户信息
                    UserClient.removeCurrentUser();
                    message.warning({content: `错误的用户登录缓存信息已清空！2 秒内即将跳转回主页。`, duration: 2});
                    UrlHelper.navigateTo({path: '/', needReload: true, delayTime: 2000});
                }

                // 这种是网络请求成功，但业务码错误，仅需提示。
                message.warning({content: `业务码：${response.code} 错误信息：${response.msg}`});

                // 相当于中断正常流程的 Promise 流程
                return Promise.reject(axiosResponse);
            },
            // 异常处理器
            (axiosResponseWhenError) => {
                const httpStatus = axiosResponseWhenError.status;
                if (httpStatus == null || httpStatus != 200) {
                    message.error({content: `网络请求异常！HttpStats:${httpStatus}`});
                }

                // 相当于中断正常流程的 Promise 流程
                return Promise.reject(axiosResponseWhenError);
            }
        );
    }

    // 提供公共方法供外部使用
    async get<T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<HyggeResponse<T>>> {
        return this.axiosInstance.get(url, config);
    }

    async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<HyggeResponse<T>>> {
        return this.axiosInstance.post(url, data, config);
    }

    async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<AxiosResponse<HyggeResponse<T>>> {
        return this.axiosInstance.put(url, data, config);
    }

    async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<HyggeResponse<T>>> {
        return this.axiosInstance.delete(url, config);
    }
}

// 默认请求使用，15 秒的超时时间
export const httpClient = new HttpClient(15000);

// 无超时时间
export const httpClient_file = new HttpClient();