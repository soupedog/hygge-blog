export interface OfNullableInputParam<T> {
    target: T | null | undefined;
    defaultValue: T;
}

export interface ArrayFormatInputParam<T = any> {
    array: T[];
    isStandard?: boolean;      // 是否输出为标准 JSON 数组格式（带 []）
    itemKey?: keyof T;         // 若数组元素为对象，指定取哪个属性
}

export default class PropertiesHelper {
    /**
     * 安全获取字符串值，若 target 无效则返回默认值
     */
    static stringOfNullable(inputParam: OfNullableInputParam<string>): string {
        return this.isStringNotEmpty(inputParam.target) ? inputParam.target : inputParam.defaultValue;
    }

    /**
     * 安全获取布尔值，若 target 无效则返回默认值
     */
    static booleanOfNullable(inputParam: OfNullableInputParam<boolean>): boolean {
        return this.isBooleanNotNull(inputParam.target) ? inputParam.target : inputParam.defaultValue;
    }

    /**
     * 判断是否为非空字符串
     */
    static isStringNotEmpty(target: unknown): target is string {
        return typeof target === "string" && target.length > 0;
    }

    /**
     * 判断是否为有效数字（不包括 NaN）
     */
    static isNumberNotNull(target: unknown): target is number {
        return typeof target === "number" && !isNaN(target);
    }

    /**
     * 判断是否为有效布尔值
     */
    static isBooleanNotNull(target: unknown): target is boolean {
        return typeof target === "boolean";
    }

    /**
     * 判断是否为非空对象（不包含 null，且为 object 类型，不包括数组）
     */
    static isObjectNotNull(target: unknown): target is object {
        return target !== null && typeof target === "object" && !Array.isArray(target);
    }

    /**
     * 判断是否为非空数组
     */
    static isArrayNotNull<T = any>(target: unknown): target is T[] {
        return Array.isArray(target);
    }

    /**
     * 判断是否为有效函数
     */
    static isFunctionNotNull(target: unknown): target is Function {
        return typeof target === "function";
    }

    /**
     * 将数组转换为字符串
     * @example
     * PropertiesHelper.arrayToString({ array: [1,2,3], isStandard: true }) // "[1,2,3]"
     * PropertiesHelper.arrayToString({ array: [{id:1},{id:2}], itemKey: "id" }) // "1,2"
     */
    static arrayToString<T extends Record<string, any>>(inputParam: ArrayFormatInputParam<T>): string {
        const { array, isStandard = false, itemKey } = inputParam;

        if (!array.length) {
            return isStandard ? "[]" : "";
        }

        const parts = array.map(item => {
            if (itemKey != null && item && typeof item === "object") {
                return String(item[itemKey] ?? "");
            }
            return String(item);
        });

        const result = parts.join(",");
        return isStandard ? `[${result}]` : result;
    }
}