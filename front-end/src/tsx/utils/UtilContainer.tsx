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
            if (inputParam.isJson === false || PropertiesHelper.isStringNotNull(inputParam.msg)) {
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

console.log("[LogHelper] " + "constructor---------- CurrentLogLevel:" + LogLevel[LogHelper.actualLogLevel]);

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
    static sleep(ms: number) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    static async scrollTop(top: number, ms: number) {
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
    static stringOfNullable(inputParam: OfNullableStringInputParam) {
        let resultTemp = inputParam.target;
        if (PropertiesHelper.isStringNotNull(resultTemp)) {
            return resultTemp.trim() == "" ? inputParam.defaultValue : resultTemp;
        } else {
            return inputParam.defaultValue;
        }
    }

    static isStringNotNull(target?: any): boolean {
        let result = false;
        if (target != null) {
            if (typeof target == "string") {
                result = true;
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

    static booleanOfNullable(inputParam: OfNullableBooleanInputParam) {
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

const baseUrl = "https://www.xavierwang.cn/";
// const baseUrl = "http://localhost:9000/";
const baseStaticSourceUrl = "https://www.xavierwang.cn/static/";

export interface OpenNewPageConfig {
    finalUrl: string;
    inNewTab: boolean;
    delayTime?: number;
}

export class UrlHelper {

    static getBaseUrl() {
        return baseUrl;
    }

    static getBaseStaticSourceUrl() {
        return baseStaticSourceUrl;
    }

    static removeBaseStaticSourceUrl(target: string) {
        return target.replace(baseStaticSourceUrl, "");
    }

    static getUrl(path?: string) {
        return path == null ? this.getBaseUrl() : this.getBaseUrl() + path;
    }

    static getQueryString(key: string) {
        let fullURL = window.location.href;
        let start = fullURL.indexOf('?');
        let search = fullURL.substring(start + 1);
        let searchArr = search.split('&');
        let searchObj: any = {};
        for (let element of searchArr) {
            let arr: string[] = element.split('=');
            searchObj[arr[0]] = arr[1];
        }
        if (searchObj[key]) {
            return decodeURI(searchObj[key]);
        } else {
            return null;
        }
    }

    static openNewPage(config: OpenNewPageConfig) {
        let actualUrl = config.finalUrl;
        let secretKey = this.getQueryString("secretKey");
        if (PropertiesHelper.isStringNotNull(secretKey)) {
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

interface TimeInfo {
    timeStamp?: number;
    outPutType?: string;
    outPutSize?: number;
    deltaDay?: number;
}

export class TimeHelper {
    // 一天的毫秒数
    static getDayMsec() {
        return 86400000;
    }

    // 填充 inputParam.timeStamp 为 inputParam.outPutSize 位数
    static formatNumber(inputParam: TimeInfo) {
        let compareNumber = Math.pow(10, inputParam.outPutSize!);
        let needAddCount = 0;
        let result = "";
        let prefix = "";
        if (inputParam.timeStamp! < 0) {
            prefix = "-";
        }
        inputParam.timeStamp = Math.abs(inputParam.timeStamp!);
        if (inputParam.timeStamp < 2) {
            needAddCount = inputParam.outPutSize! - 1;
        } else {
            while (Math.pow(10, needAddCount + 1) * inputParam.timeStamp < compareNumber) {
                needAddCount += 1;
            }
        }
        for (let i = 0; i < needAddCount; i++) {
            result += "0";
        }
        result += inputParam.timeStamp;
        return prefix + result;
    }

    // 获取当前时间 ± x 天的毫秒级时间戳
    static getTimeStamp(x: number) {
        let currentTs = new Date().getTime();
        if (x == null) {
            return currentTs;
        }
        currentTs -= x * this.getDayMsec();
        return currentTs;
    }

    // 目标毫秒级时间戳格式化成字符串,默认格式为 yyyy-mm-dd hh:mm:ss
    static formatTimeStampToString(inputParam: TimeInfo) {
        if (typeof inputParam.timeStamp != "number") {
            throw new Error("TimeHelper:[inputParam.timeStamp] of formatTimeStampToString(inputParam.timeStamp, type) should be number.");
        }
        let currentDate = new Date(inputParam.timeStamp);
        let year = currentDate.getFullYear();
        let month = currentDate.getMonth() + 1;
        let day = currentDate.getDate();
        let hour = currentDate.getHours();
        let minute = currentDate.getMinutes();
        let second = currentDate.getSeconds();
        switch (inputParam.outPutType) {
            case "yyyy-mm-dd":
                return year + "-" + this.formatNumber({timeStamp: month, outPutSize: 2}) + "-" + this.formatNumber({
                    timeStamp: day,
                    outPutSize: 2
                });
            case "hh:mm:ss":
                return this.formatNumber({timeStamp: hour, outPutSize: 2}) + ":" +
                    this.formatNumber({
                        timeStamp: minute,
                        outPutSize: 2
                    }) + ":" +
                    this.formatNumber({timeStamp: second, outPutSize: 2});
            default:
                return year + "-" + this.formatNumber({timeStamp: month, outPutSize: 2}) + "-" + this.formatNumber({
                        timeStamp: day,
                        outPutSize: 2
                    }) + " " +
                    this.formatNumber({timeStamp: hour, outPutSize: 2}) + ":" + this.formatNumber({
                        timeStamp: minute,
                        outPutSize: 2
                    }) + ":" + this.formatNumber({timeStamp: second, outPutSize: 2});
        }
    }

    // 获取目标时间戳 ± x 个自然天的 00:00:00 时刻时间戳
    static getNaturalDayTimeStamp(inputParam: TimeInfo) {
        let currentDate = new Date(inputParam.timeStamp!);
        let year = currentDate.getFullYear();
        let month = currentDate.getMonth();
        let day = currentDate.getDate();
        if (typeof inputParam.timeStamp != "number") {
            throw new Error("TimeHelper:[inputParam.timeStamp] of getNaturalDayTimeStamp(inputParam.timeStamp, inputParam.deltaDay) should be number.");
        }
        let resultDate = new Date(year, month, day, 0, 0, 0, 0);
        if (inputParam.deltaDay == null) {
            inputParam.deltaDay = 0;
        }
        return resultDate.getTime() + inputParam.deltaDay * this.getDayMsec();
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
LogHelper.info({className: "WindowsEventHelper", msg: "constructor----------"});

export class WindowsEventHelper {

    static addCallback_Scroll(eventCallbackItem: EventCallbackItem) {
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

    static addCallback_Resize(eventCallbackItem: EventCallbackItem) {
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

    static start_OnScroll() {
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

    static start_OnResize() {
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