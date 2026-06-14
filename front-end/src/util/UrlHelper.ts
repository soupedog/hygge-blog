import {appConfiguration} from '../configuration/app.configuration'
import PropertiesHelper from "./PropertiesHelper.ts";
import type {NavigateFunction} from "react-router-dom";

export interface QueryStringValue {
    [key: string]: string | number | boolean | null | undefined;
}

export interface AppNavigateConfig {
    inNewTab: boolean;
    path?: string;
    finalUrl?: string;
    delayTime?: number;
    navigateFunction?: NavigateFunction,
    canBack?: boolean;
}

export default class UrlHelper {
    static getHomePagePrefix(): string {
        return appConfiguration.host_FE;
    }

    static getApiPrefix(): string {
        return appConfiguration.host_BE;
    }

    /**
     * 处理完整 URL，合并旧的 queryString 和新传入的 queryString
     * @param url - 完整 URL（必须包含 http:// 或 https://）
     * @param queryStrings - 0 个或多个 query string 对象
     * @returns 合并参数后的完整 URL
     *
     * @example
     * UrlBuilder.mergeUrl('https://example.com/api?page=1', { size: 10 }, { sort: 'desc' })
     * // => 'https://example.com/api?page=1&size=10&sort=desc'
     *
     * @example
     * UrlBuilder.mergeUrl('https://example.com/api?a=1', { a: 2, b: 3 })
     * // => 'https://example.com/api?a=2&b=3'
     *
     * @example
     * UrlBuilder.mergeUrl('https://example.com/api')
     * // => 'https://example.com/api'
     */
    static mergeUrl(url: string, ...queryStrings: QueryStringValue[]): string {
        const urlObj = new URL(url);

        for (const qs of queryStrings) {
            if (qs && typeof qs === 'object') {
                for (const [key, value] of Object.entries(qs)) {
                    if (value != null) {
                        urlObj.searchParams.set(key, String(value));
                    } else {
                        urlObj.searchParams.delete(key);
                    }
                }
            }
        }

        return urlObj.href;
    }

    /**
     * 自动处理 host 、path 和可能存在的 queryString，拼接成完整 URL
     * @param host host 如 http://test.com
     * @param path - 路径（如 /api/user 或 api/user，自动确保以 / 开头）
     * @param queryStrings - 0 个或多个 query string 对象
     * @returns 拼接后的完整 URL
     *
     * @example
     * UrlBuilder.buildFromPath('/api/user', { id: 1 })
     * // => 'http://test.com/api/user?id=1'
     *
     * @example
     * UrlBuilder.buildFromPath('api/user', { page: 1, size: 10 })
     * // => 'http://test.com/api/user?page=1&size=10'
     *
     * @example
     * UrlBuilder.buildFromPath('/api/user')
     * // => 'http://test.com/api/user'
     */
    private static buildUrl(host: string, path: string, ...queryStrings: QueryStringValue[]): string {
        // 确保 path 以 / 开头
        const normalizedPath = path.startsWith('/') ? path : `/${path}`;
        const baseUrl = `${host}${normalizedPath}`;

        return this.mergeUrl(baseUrl, ...queryStrings);
    }

    static buildApiUrl(path: string, ...queryStrings: QueryStringValue[]): string {
        return this.buildUrl(this.getApiPrefix(), path, ...queryStrings)
    }

    static buildLinkUrl(path: string, ...queryStrings: QueryStringValue[]) {
        return this.buildUrl(this.getHomePagePrefix(), path, ...queryStrings)
    }

    /**
     * 获取 URL 查询参数的值
     * @param key - 参数名
     * @returns 参数值（已解码），不存在时返回 null
     * @example
     * // 当前 URL: https://example.com?name=hello%20world&age=18#top
     * getQueryString('name') // "hello world"
     * getQueryString('age')  // "18"
     * getQueryString('none') // null
     */
    static getQueryString(key: string): string | null {
        if (typeof window === 'undefined' || !window.location) {
            return null; // 支持 SSR 环境
        }

        const urlParams = new URLSearchParams(window.location.search);
        const value = urlParams.get(key);

        return value !== null ? decodeURIComponent(value) : null;
    }

    static navigateTo(config: AppNavigateConfig): void {
        if (!config.inNewTab && config.navigateFunction == null) {
            throw new Error(`[navigateFunction] can't be null when the [inNewTab] is ${config.inNewTab}`);
        }

        let actualUrl: string;

        if (config.path != null) {
            // 确保 path 以 / 开头
            const normalizedPath = config.path.startsWith('/') ? config.path : `/${config.path}`;

            actualUrl = this.getHomePagePrefix() + normalizedPath;
        } else if (config.finalUrl != null) {
            actualUrl = config.finalUrl;
        } else {
            actualUrl = this.getHomePagePrefix();
        }

        const secretKey = this.getQueryString("secretKey");
        actualUrl = this.mergeUrl(actualUrl, {secretKey: secretKey})

        if (config.inNewTab) {
            if (config.delayTime != null) {
                window.setTimeout(function () {
                    window.open(actualUrl);
                }, config.delayTime);
            } else {
                window.open(actualUrl);
            }
            return;
        } else {
            // 非新开窗口用单页面应用跳转工具
            // 前面共享了 URL 处理工具需要把 host 去除
            actualUrl = actualUrl.replace(this.getHomePagePrefix(), "");
            const actualCanBack = PropertiesHelper.booleanOfNullable({target: config.canBack, defaultValue: true});
            const actualReplace = !actualCanBack;

            if (config.delayTime != null) {
                window.setTimeout(function () {
                    config.navigateFunction!(actualUrl, {replace: actualReplace});
                }, config.delayTime);
            } else {
                config.navigateFunction!(actualUrl, {replace: actualReplace});
            }
        }
    }
}