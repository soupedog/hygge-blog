export interface TextAreaCursorInfo {
    selectionStart: number;
    selectionEnd: number;
}

// 结尾为换行符或文本最后一个字符(闭区间)
export interface TextAreaContentLineInfo {
    start: number;
    end: number;
}

export interface TextAreaContentRemoveLineInfo {
    leftPart: string;
    rightPart: string;
}

export interface AppendTextToTextAreaInput {
    appendTarget: string;
    leftPart: string;
    selectedPart: string;
    rightPart: string;
}

export default class InputElementHelper {
    static getTextAreaCursorInfo(element: HTMLTextAreaElement): TextAreaCursorInfo {
        let result = {} as TextAreaCursorInfo;

        result.selectionStart = element.selectionStart;// 获取选区的开始位置
        result.selectionEnd = element.selectionEnd;// 获取选区的结束位置
        return result
    }

    static removeSelectedLine(element: HTMLTextAreaElement, successHook?: (input: TextAreaContentRemoveLineInfo) => void) {
        let textAreaCursorInfo: TextAreaCursorInfo = this.getTextAreaCursorInfo(element);

        let oldContent = element.textContent;

        if (successHook == null || oldContent == null) {
            return;
        }

        let lineBreakIndexArray = new Array<number>();

        let flag = true;
        let startIndex = 0;

        lineBreakIndexArray.push(0);

        while (flag) {
            let item = oldContent!.indexOf("\n", startIndex);
            if (item != -1) {
                if (item != 0 && item != oldContent.length) {
                    lineBreakIndexArray.push(item);
                }
                startIndex = item + 1;
            } else {
                flag = false;
            }
        }

        let lineInfoArray = new Array<TextAreaContentLineInfo>();

        lineBreakIndexArray.forEach((value, index, array) => {
            let start;
            let end;

            if (index == 0) {
                // 首行
                start = 0;
                end = array[index + 1];

            } else if (index == array.length - 1) {
                // 最后一行
                start = value + 1;
                end = oldContent!.length - 1
            } else {
                // 首行外 +1
                start = value + 1;
                end = array[index + 1];
            }

            lineInfoArray.push({
                start: start,
                end: end
            });
        });

        let leftCutIndex: number = 0;
        let rightCutIndex: number = oldContent.length - 1;

        // 找出选中区域左端点对应的行起始点
        lineInfoArray.forEach((item, index, array) => {

            if (item.start <= textAreaCursorInfo.selectionStart) {
                leftCutIndex = item.start;
            }
        });

        // 找出选中区域右端点对应的行结束点
        for (let i = lineInfoArray.length - 1; i >= 0; i--) {
            let item = lineInfoArray[i];

            if (item.end >= textAreaCursorInfo.selectionEnd) {
                rightCutIndex = item.end;
            }
        }

        let leftPart: string;

        if (leftCutIndex != 0) {
            leftPart = oldContent.slice(0, leftCutIndex);
        } else {
            leftPart = "";
        }

        let rightPart: string = oldContent.slice(rightCutIndex + 1);

        successHook({
            leftPart: leftPart,
            rightPart: rightPart
        });
    }

    static appendTextToTextArea(element: HTMLTextAreaElement, appendTarget: string,
                                successHook?: (input: AppendTextToTextAreaInput) => void): string {
        let leftPart: string = "";
        let selectedPart: string = "";
        let rightPart: string = "";

        let old: string | null = element.textContent;

        if (old == null) {
            // 原始内容为空，直接覆盖
            return appendTarget;
        } else {
            // 原始内容不为空，覆盖掉选中内容
            let cursorInfo: TextAreaCursorInfo = this.getTextAreaCursorInfo(element);
            selectedPart = old.slice(cursorInfo.selectionStart, cursorInfo.selectionEnd);

            leftPart = old.slice(0, cursorInfo.selectionStart);
            rightPart = old.slice(cursorInfo.selectionEnd);
        }

        if (successHook != null) {
            successHook({
                appendTarget: appendTarget,
                leftPart: leftPart,
                selectedPart: selectedPart,
                rightPart: rightPart
            });
        }

        // 默认返回用 appendTarget 强制覆盖掉原选中内容后的文本
        return leftPart + appendTarget + rightPart;
    }
}
