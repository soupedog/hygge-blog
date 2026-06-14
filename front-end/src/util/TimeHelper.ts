import {TimeType} from '../enums/EnumKeeper.ts';

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