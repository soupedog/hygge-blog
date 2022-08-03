export enum LogLevel {
    DEBUG = 0,
    INFO = 10,
    POINT_INFO = 15,
    WARN = 20,
    ERROR = 30,
    ALWAYS = 100
}

let CurrentLogLevel = LogLevel.DEBUG;    // 当前日志级别

export class PropertiesHelper {
    static stringOfNullable(target?: any, defaultValue?: string) {
        if (target != null) {
            let resultTemp = target.toString();
            return resultTemp.trim() == "" ? defaultValue : resultTemp;
        }
        return defaultValue;
    }
}

export class LogHelper {
    static getCurrentLogLevel(): number {
        return CurrentLogLevel.valueOf();
    }

    static setCurrentLogLevel(currentLogLevel: LogLevel): void {
        CurrentLogLevel = currentLogLevel;
    }

    static getLevelIndex(levelTag?: LogLevel): number {
        switch (levelTag) {
            case LogLevel.INFO:
                return 10;
            case LogLevel.POINT_INFO:
                return 15;
            case LogLevel.WARN:
                return 20;
            case LogLevel.ERROR:
                return 30;
            case LogLevel.ALWAYS:
                return 100;
            default :
                return 0;
        }
    }

    static getLevelName(levelNumber?: number): string {
        if (levelNumber == null) {
            return "unknown";
        } else if (levelNumber >= 100) {
            return "ALWAYS";
        } else if (levelNumber >= 30) {
            return "ERROR"
        } else if (levelNumber >= 20) {
            return "WARN"
        } else if (levelNumber >= 15) {
            return "POINT_INFO"
        } else if (levelNumber >= 10) {
            return "INFO"
        } else {
            return "DEBUG";
        }
    }

    static log(level: LogLevel, levelName: string, className: string, tag?: string, msg?: any, isJson?: boolean): void {
        if (LogHelper.getCurrentLogLevel() > level) {
            return;
        }

        let finalMsg = "";
        if (msg != null) {
            if (isJson) {
                finalMsg = JSON.stringify(msg);
            } else {
                finalMsg = msg.toString();
            }
        }
        console.log(levelName + " [" + className + "] " + tag + " :" + finalMsg);
    }

    static debug(className: string, tag?: string, msg?: any, isJson?: boolean): void {
        this.log(LogLevel.DEBUG, "DEBUG", className, tag, msg, isJson);
    }

    static info(className: string, tag?: string, msg?: any, isJson?: boolean): void {
        this.log(LogLevel.INFO, "INFO", className, tag, msg, isJson);
    }

    static pointInfo(className: string, tag?: string, msg?: any, isJson?: boolean): void {
        this.log(LogLevel.POINT_INFO, "POINT_INFO", className, tag, msg, isJson);
    }

    static warn(className: string, tag?: string, msg?: any, isJson?: boolean): void {
        this.log(LogLevel.WARN, "WARN", className, tag, msg, isJson);
    }

    static error(className: string, tag?: string, msg?: any, isJson?: boolean): void {
        this.log(LogLevel.ERROR, "ERROR", className, tag, msg, isJson);
    }

    static ALWAYS(className: string, tag?: string, msg?: any, isJson?: boolean): void {
        this.log(LogLevel.ALWAYS, "ALWAYS", className, tag, msg, isJson);
    }
}

console.log("[LogHelper] " + "constructor---------- CurrentLogLevel:" + LogHelper.getLevelName(CurrentLogLevel.valueOf()));