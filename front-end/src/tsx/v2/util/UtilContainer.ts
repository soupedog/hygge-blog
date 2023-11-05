export enum LogLevel {
    DEBUG = 0,
    INFO = 10,
    POINT_INFO = 15,
    WARN = 20,
    ERROR = 30,
    MUST = 100
}

export interface LogParam {
    level: LogLevel;
    className: string;
    tag?: string;
    msg?: any;
    isJson?: boolean;
}

export interface LogParam2 {
    className: string;
    tag?: string;
    msg?: any;
    isJson?: boolean;
}

export class LogHelper {
    static actualLogLevel: LogLevel = LogLevel.WARN;

    static log(inputParam: LogParam): void {
        if (LogHelper.actualLogLevel > inputParam.level) {
            return;
        }

        let finalMsg: string;
        let finalTag: string = PropertiesHelper.stringOfNullable({target: inputParam.tag, defaultValue: ""});

        if (inputParam.msg !== undefined) {
            if (inputParam.isJson === false || PropertiesHelper.isStringNotEmpty(inputParam.msg)) {
                finalMsg = inputParam.msg;
            } else {
                try {
                    finalMsg = JSON.stringify(inputParam.msg);
                } catch (e) {
                    finalMsg = inputParam.msg;
                }
            }
        } else {
            finalMsg = "";
        }
        console.log(LogLevel[inputParam.level] + " [" + inputParam.className + "] " + finalTag + " :" + finalMsg);
    }

    static debug(inputParam: LogParam2): void {
        this.log({
            level: LogLevel.DEBUG,
            className: inputParam.className,
            tag: inputParam.tag,
            msg: inputParam.msg,
            isJson: inputParam.isJson
        });
    }

    static info(inputParam: LogParam2): void {
        this.log({
            level: LogLevel.INFO,
            className: inputParam.className,
            tag: inputParam.tag,
            msg: inputParam.msg,
            isJson: inputParam.isJson
        });
    }

    static pointInfo(inputParam: LogParam2): void {
        this.log({
            level: LogLevel.POINT_INFO,
            className: inputParam.className,
            tag: inputParam.tag,
            msg: inputParam.msg,
            isJson: inputParam.isJson
        });
    }

    static warn(inputParam: LogParam2): void {
        this.log({
            level: LogLevel.WARN,
            className: inputParam.className,
            tag: inputParam.tag,
            msg: inputParam.msg,
            isJson: inputParam.isJson
        });
    }

    static error(inputParam: LogParam2): void {
        this.log({
            level: LogLevel.ERROR,
            className: inputParam.className,
            tag: inputParam.tag,
            msg: inputParam.msg,
            isJson: inputParam.isJson
        });
    }

    static must(inputParam: LogParam2): void {
        this.log({
            level: LogLevel.MUST,
            className: inputParam.className,
            tag: inputParam.tag,
            msg: inputParam.msg,
            isJson: inputParam.isJson
        });
    }
}

console.log("MUST [LogHelper] " + "初始化成功 CurrentLogLevel:" + LogLevel[LogHelper.actualLogLevel]);

export interface OfNullableStringInputParam {
    target?: any;
    defaultValue: string;
}

export interface OfNullableBooleanInputParam {
    target?: any;
    defaultValue: boolean;
}

export interface ArrayFormatInputParam {
    // 是否需要保留 "[" 和 "]"
    isStandard?: boolean;
    array: Array<any>;
    // 遍历提取数组元素的特定 key 的值时使用
    itemKey?: string;
}

export class CoreHelper {
    static sleep(ms: number): Promise<any> {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    static async scrollTop(top: number, ms: number): Promise<void> {
        let currentScrollY = window.scrollY;

        let value = 0;
        let deltaTime = ms / 30;
        let deltaTop = Math.abs(currentScrollY - top) / 30;

        while (value < top) {
            window.scrollTo({top: value})
            await this.sleep(deltaTime);
            value = value + deltaTop
        }
    }
}

export class PropertiesHelper {
    static stringOfNullable(inputParam: OfNullableStringInputParam): string {
        let resultTemp = inputParam.target;
        if (PropertiesHelper.isStringNotEmpty(resultTemp)) {
            return resultTemp;
        } else {
            return inputParam.defaultValue;
        }
    }

    static isStringNotEmpty(target?: any): boolean {
        let result = false;
        if (target != null) {
            if (typeof target == "string") {
                result = target.length > 0;
            }
        }
        return result;
    }

    static isNumberNotNull(target?: any): boolean {
        let result = false;
        if (target != null) {
            if (typeof target == "number") {
                result = true;
            }
        }
        return result;
    }

    static isBooleanNotNull(target?: any): boolean {
        let result = false;
        if (target != null) {
            if (typeof target == "boolean") {
                result = true;
            }
        }
        return result;
    }

    static booleanOfNullable(inputParam: OfNullableBooleanInputParam): boolean {
        if (PropertiesHelper.isBooleanNotNull(inputParam.target)) {
            return inputParam.target;
        } else {
            return inputParam.defaultValue;
        }
    }

    static isObjectNotNull(target?: any): boolean {
        let result = false;
        if (target != null) {
            if (typeof target == "object") {
                result = true;
            }
        }
        return result;
    }

    static isArrayNotNull(target?: any): boolean {
        let result = false;
        if (this.isObjectNotNull(target)) {
            if (target instanceof Array) {
                result = true;
            }
        }
        return result;
    }

    static isFunctionNotNull(target?: any): boolean {
        let result = false;
        if (target != null) {
            if (typeof target == "function") {
                result = true;
            }
        }
        return result;
    }

    static arrayToString(inputParam: ArrayFormatInputParam): string {
        let arrayStringVal = "";
        if (inputParam.isStandard) {
            arrayStringVal += "["
        }

        for (let item of inputParam.array) {
            if (inputParam.itemKey != null) {
                arrayStringVal += item[inputParam.itemKey] + ","
            } else {
                arrayStringVal += item + ","
            }
        }

        // 去除额外 ","
        if (arrayStringVal.length > 0) {
            arrayStringVal = arrayStringVal.substring(0, arrayStringVal.length - 1);
        }

        if (inputParam.isStandard) {
            arrayStringVal += "]"
        }
        return arrayStringVal;
    }
}

const baseUrl = "http://localhost:9000/";
const baseApiUrl = "http://localhost:8080/blog-service/api";
const baseStaticSourceUrl = "http://localhost:8080/static";

export interface OpenNewPageConfig {
    inNewTab: boolean;
    path?: string;
    finalUrl?: string;
    delayTime?: number;
}

export class UrlHelper {
    static getBaseUrl(): string {
        return baseUrl;
    }

    static getBaseApiUrl(): string {
        return baseApiUrl;
    }

    static getBaseStaticSourceUrl(): string {
        return baseStaticSourceUrl;
    }

    static removeBaseStaticSourceUrl(target: string): string {
        return target.replace(baseStaticSourceUrl, "");
    }

    static getUrl(path?: string): string {
        return path == null ? this.getBaseUrl() : this.getBaseUrl() + path;
    }

    static getQueryString(key: string): string | null {
        let fullURL = window.location.href;
        let startPoint = fullURL.indexOf('?') + 1;
        let queryStringTarget = fullURL.substring(startPoint);

        let endPoint = queryStringTarget.indexOf("#");

        if (endPoint > 0) {
            queryStringTarget = queryStringTarget.substring(0, endPoint);
        }

        let queryStringArray = queryStringTarget.split('&');
        let searchObj: any = {};

        queryStringArray.forEach((item => {
            let arr: string[] = item.split('=');
            searchObj[arr[0]] = arr[1];
        }))

        if (searchObj[key]) {
            return decodeURI(searchObj[key]);
        } else {
            return null;
        }
    }

    static openNewPage(config: OpenNewPageConfig): void {
        let actualUrl: string;

        if (config.path != null) {
            actualUrl = this.getBaseUrl() + config.path;
        } else if (config.finalUrl != null) {
            actualUrl = config.finalUrl;
        } else {
            actualUrl = this.getBaseUrl();
        }

        let secretKey = this.getQueryString("secretKey");
        if (PropertiesHelper.isStringNotEmpty(secretKey)) {
            if (actualUrl.indexOf("?") > 0) {
                actualUrl = actualUrl + "&secretKey=" + secretKey;
            } else {
                actualUrl = actualUrl + "?secretKey=" + secretKey;
            }
        }

        if (!config.inNewTab) {
            if (config.delayTime != null) {
                window.setTimeout(function () {
                    window.location.href = actualUrl;
                }, config.delayTime);
            } else {
                window.location.href = actualUrl;
            }
        } else {
            if (config.delayTime != null) {
                window.setTimeout(function () {
                    window.open(actualUrl);
                }, config.delayTime);
            } else {
                window.open(actualUrl);
            }
        }
    }
}

export enum TimeType {
    // 2022-8-4
    yyyy_mm_dd = 0,
    // 21:39:44
    hh_mm_ss = 1,
    // 2022-8-4 21:39:44
    yyyy_mm_dd_hh_mm_ss = 2,
}

export class TimeHelper {
    // 一天的毫秒数
    static getDayMsec(): number {
        return 86400000;
    }

    // 填充 timeStamp 为 outPutSize 位数
    static formatNumber(timeStamp: number, outPutSize?: number): string {
        if (outPutSize == null) {
            outPutSize = 2;
        }

        let compareNumber = Math.pow(10, outPutSize);
        let needAddCount = 0;
        let result = "";
        let prefix = "";
        if (timeStamp < 0) {
            prefix = "-";
        }
        timeStamp = Math.abs(timeStamp);
        if (timeStamp < 2) {
            needAddCount = outPutSize - 1;
        } else {
            while (Math.pow(10, needAddCount + 1) * timeStamp < compareNumber) {
                needAddCount += 1;
            }
        }
        for (let i = 0; i < needAddCount; i++) {
            result += "0";
        }
        result += timeStamp;
        return prefix + result;
    }

    // 获取当前时间 ± x 天的毫秒级时间戳
    static getTimeStamp(x: number): number {
        let currentTs = new Date().getTime();
        if (x == null) {
            return currentTs;
        }
        currentTs -= x * this.getDayMsec();
        return currentTs;
    }

    // 目标毫秒级时间戳格式化成字符串,默认格式为 yyyy-mm-dd hh:mm:ss
    static formatTimeStampToString(timeStamp: number, type?: TimeType): string {
        let currentDate = new Date(timeStamp);
        let year = currentDate.getFullYear();
        let month = currentDate.getMonth() + 1;
        let day = currentDate.getDate();
        let hour = currentDate.getHours();
        let minute = currentDate.getMinutes();
        let second = currentDate.getSeconds();
        switch (type) {
            case TimeType.yyyy_mm_dd:
                return year + "-" + this.formatNumber(month) + "-" + this.formatNumber(day);
            case TimeType.hh_mm_ss:
                return this.formatNumber(hour) + ":" + this.formatNumber(minute) + ":" + this.formatNumber(second);
            default:
                return year + "-" + this.formatNumber(month) + "-" + this.formatNumber(day) + " " + this.formatNumber(hour) + ":" + this.formatNumber(minute) + ":" + this.formatNumber(second);
        }
    }

    // 获取目标时间戳 ± x 个自然天的 00:00:00 时刻时间戳
    static getNaturalDayTimeStamp(timeStamp: number, deltaDay?: number): number {
        let currentDate = new Date(timeStamp);
        let year = currentDate.getFullYear();
        let month = currentDate.getMonth();
        let day = currentDate.getDate();

        let resultDate = new Date(year, month, day, 0, 0, 0, 0);

        if (deltaDay == null) {
            deltaDay = 0;
        }
        return resultDate.getTime() + deltaDay * this.getDayMsec();
    }
}

interface EventCallbackItem {
    name: string;
    callbackFunction?: EventCallBackFunction;
    delta?: number;
}

export interface EventCallBackFunction {
    (event: any): void;
}

const Scroll_FunctionMap = new Map<string, any>();
const Resize_FunctionMap = new Map<string, any>();
const FunctionLimiter_Map = new Map<string, boolean>();
const FunctionLimiter_Time = new Map<string, number>();
LogHelper.info({className: "WindowsEventHelper", msg: "初始化成功"});

export class WindowsEventHelper {

    static addCallback_Scroll(eventCallbackItem: EventCallbackItem): void {
        if (Scroll_FunctionMap.has(eventCallbackItem.name) || FunctionLimiter_Time.has(eventCallbackItem.name)) {
            LogHelper.warn({
                className: "WindowsEventHelper",
                msg: "AddCallback_Scroll duplicate callback key : " + eventCallbackItem.name
            });
        }
        Scroll_FunctionMap.set(eventCallbackItem.name, eventCallbackItem.callbackFunction);
        if (eventCallbackItem.delta != null) {
            FunctionLimiter_Time.set(eventCallbackItem.name, eventCallbackItem.delta);
        }
        LogHelper.info({className: "WindowsEventHelper", msg: "AddCallback_Scroll : " + eventCallbackItem.name});
    }

    static getScroll_FunctionMap() {
        return Scroll_FunctionMap;
    }

    static addCallback_Resize(eventCallbackItem: EventCallbackItem): void {
        if (Scroll_FunctionMap.has(eventCallbackItem.name) || FunctionLimiter_Time.has(eventCallbackItem.name)) {
            LogHelper.warn({
                className: "WindowsEventHelper",
                msg: "AddCallback_Resize duplicate callback key : " + eventCallbackItem.name
            });
        }
        Resize_FunctionMap.set(eventCallbackItem.name, eventCallbackItem.callbackFunction);
        if (eventCallbackItem.delta != null) {
            FunctionLimiter_Time.set(eventCallbackItem.name, eventCallbackItem.delta);
        }
        LogHelper.info({className: "WindowsEventHelper", msg: "AddCallback_Resize : " + eventCallbackItem.name});
    }

    static getResize_FunctionMap() {
        return Resize_FunctionMap;
    }

    static start_OnScroll(): void {
        window.onscroll = () => {
            LogHelper.info({className: "WindowsEventHelper", msg: "OnScroll----------" + Scroll_FunctionMap.size});
            let currentScrollY = window.scrollY;
            Scroll_FunctionMap.forEach((value, key) => {
                if (!FunctionLimiter_Map.has(key)) {
                    FunctionLimiter_Map.set(key, true);
                    let callbackFunction = value as EventCallBackFunction;
                    callbackFunction({currentScrollY});
                    let delta = FunctionLimiter_Time.has(key) ? FunctionLimiter_Time.get(key) : 500;
                    window.setTimeout(function () {
                        FunctionLimiter_Map.delete(key)
                    }, delta);
                }
            })
        }
    }

    static start_OnResize(): void {
        window.onresize = () => {
            LogHelper.info({className: "WindowsEventHelper", msg: "OnResize----------" + Resize_FunctionMap.size});
            let currentHeight = window.innerHeight;
            let currentWidth = window.innerWidth;

            Resize_FunctionMap.forEach((value, key) => {
                if (!FunctionLimiter_Map.has(key)) {
                    FunctionLimiter_Map.set(key, true);
                    let callbackFunction = value as EventCallBackFunction;
                    callbackFunction({currentWidth: currentWidth, currentHeight: currentHeight});
                    let delta = FunctionLimiter_Time.has(key) ? FunctionLimiter_Time.get(key) : 500;
                    window.setTimeout(function () {
                        FunctionLimiter_Map.delete(key)
                    }, delta);
                }
            });
        }
    }
}