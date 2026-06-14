/**
 * localStorage 工具类
 * 提供类型安全的存储操作
 */
export default class StorageHelper {
    /**
     * 存储对象
     * @param key 存储键名
     * @param value 要存储的值
     */
    public static set<T>(key: string, value: T): void {
        try {
            const serialized = JSON.stringify(value);
            localStorage.setItem(key, serialized);
        } catch (error) {
            console.error(`[StorageUtil] Failed to save ${key}:`, error);
        }
    }

    /**
     * 获取对象
     * @param key 存储键名
     * @returns 解析后的对象或 null
     */
    public static get<T>(key: string): T | undefined {
        try {
            const serialized = localStorage.getItem(key);
            if (!serialized) return undefined;
            return JSON.parse(serialized) as T;
        } catch (error) {
            console.error(`[StorageUtil] Failed to get ${key}:`, error);
            return undefined;
        }
    }

    /**
     * 获取对象（带默认值）
     * @param key 存储键名
     * @param defaultValue 默认值
     */
    public static getOrDefault<T>(key: string, defaultValue: T): T {
        const value = this.get<T>(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 更新对象的部分属性
     * @param key 存储键名
     * @param partial 要更新的部分属性
     */
    public static update<T extends object>(key: string, partial: Partial<T>): void {
        const current = this.get<T>(key);
        if (current) {
            const updated = {...current, ...partial};
            this.set(key, updated);
        } else {
            console.warn(`[StorageUtil] Cannot update ${key}: no existing data found`);
        }
    }

    /**
     * 删除指定键
     * @param key 存储键名
     */
    public static remove(key: string): void {
        localStorage.removeItem(key);
    }

    /**
     * 检查键是否存在
     * @param key 存储键名
     */
    public static has(key: string): boolean {
        return localStorage.getItem(key) !== null;
    }

    /**
     * 清空所有存储
     */
    public static clear(): void {
        localStorage.clear();
    }

    /**
     * 获取所有存储的键
     */
    public static keys(): string[] {
        return Object.keys(localStorage);
    }

    /**
     * 获取存储大小（字节）
     */
    public static getSize(): number {
        let total = 0;
        for (let i = 0; i < localStorage.length; i++) {
            const key = localStorage.key(i);
            if (key) {
                const value = localStorage.getItem(key);
                total += (key.length + (value?.length || 0)) * 2; // UTF-16 编码
            }
        }
        return total;
    }
}