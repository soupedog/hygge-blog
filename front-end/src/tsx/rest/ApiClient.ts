import axios, {AxiosResponse} from "axios";
import {message} from "antd";
import {PropertiesHelper, UrlHelper} from "../util/UtilContainer";
import {saveAs} from 'file-saver';

axios.defaults.baseURL = UrlHelper.getBaseApiUrl();
axios.interceptors.response.use(function (response) {
    let data = response.data;
    if (data == null) {
        return Promise.reject(response);
    }
    let code = data.code;

    // 对响应数据做点什么
    if (code == 200) {
        return response;
    } else if (code == 403002) {
        UserService.removeCurrentUser();
        // 令牌刷新失败，无法自动登录
        message.warning("自动刷新令牌失败，2 秒内为您跳转回主页", 2);
        UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
        return Promise.reject(response);
    } else if (code == 403003) {
        let flag = localStorage.getItem('autoRefreshDisableFlag');

        if (flag == null) {
            localStorage.setItem('autoRefreshDisableFlag', "已禁止再次触发自动登陆");

            UserService.signIn(undefined, undefined, (data) => {
                if (data?.code == 200) {
                    message.info("已为您成功自动登录，1 秒内为您跳转回主页", 1);
                    // 重新登陆成功后需要重置自动重试开关
                    localStorage.removeItem('autoRefreshDisableFlag');
                    UrlHelper.openNewPage({inNewTab: false, delayTime: 1000});
                } else {
                    message.info("自动登录失败，1 秒内为您跳转回登录页", 1);
                    // 刷新秘钥自动登录失败，需要清空本地身份信息
                    UserService.removeCurrentUser();
                    UrlHelper.openNewPage({inNewTab: false, path: "signin", delayTime: 1000});
                }
            });
        } else {
            UserService.removeCurrentUser();
            message.warning("该账号需要重新登陆，2 秒内为您跳转回登陆页", 2);
            UrlHelper.openNewPage({inNewTab: false, path: "signin", delayTime: 2000});
        }
        return Promise.reject(response);
    } else if (code == 403000) {
        // 账号、密码、令牌错误
        UserService.removeCurrentUser();
        message.warning("已清空错误登陆信息，2 秒内为您跳转回主页", 2);
        UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
        return response;
    } else if (code == 400 || code < 500000) {
        message.warning(data.msg, 5);
        return Promise.reject(response);
    } else {
        message.error(data.msg, 5);
        return Promise.reject(response);
    }
}, function (error) {
    // 对响应错误做点什么
    message.error(error.message, 5);
    return Promise.reject(error);
});

export interface HyggeResponse<T> {
    code: number;
    msg?: string;
    main?: T;
}

const emptyResponse = {} as HyggeResponse<any>;

export interface UserDto {
    uid: string;
    userAvatar: string;
    userSex: string;
    biography?: string;
    birthday?: number;
    phone?: string;
    email?: string;
}

export interface SignInResponse {
    user?: UserDto;
    token: string;
    refreshKey: string;
    deadline: number;
}

export enum ClientScope {
    WEB = "WEB",
    PHONE = "PHONE",
}

export class UserService {
    static getCurrentUser(): UserDto | null | undefined {
        let currentUserStringValue = localStorage.getItem('currentUser');
        if (PropertiesHelper.isStringNotEmpty()) {
            return null;
        }
        return JSON.parse(currentUserStringValue!) as UserDto;
    }

    static removeCurrentUser() {
        localStorage.removeItem('uid');
        localStorage.removeItem('token');
        localStorage.removeItem('refreshKey');
        localStorage.removeItem('currentUser');
    }

    static getCurrentScope(): ClientScope {
        return ClientScope.WEB;
    }

    static getContentType(): string {
        return "application/json";
    }

    static getHeader(currentHeader?: any): any {
        let result;

        if (currentHeader == null) {
            result = {};
            // @ts-ignore
            result["Content-Type"] = this.getContentType();
        } else {
            result = currentHeader;
        }
        result.scope = this.getCurrentScope();

        let currentSecretKey = UrlHelper.getQueryString("secretKey");
        if (currentSecretKey != null) {
            result.secretKey = currentSecretKey;
        }

        let currentUId = localStorage.getItem("uid");
        let currentToken = localStorage.getItem("token");
        let currentRefreshKey = localStorage.getItem("refreshKey");
        if (currentUId != null && currentToken != null && currentRefreshKey != null) {
            result.uid = currentUId;
            result.token = currentToken;
        }
        return result;
    }

    static signIn(ac?: string, pw?: string,
                  successHook?: (input?: HyggeResponse<SignInResponse>) => void,
                  beforeHook?: () => void,
                  finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        let requestHeader = null;
        let requestData;
        if (PropertiesHelper.isStringNotEmpty(ac) && PropertiesHelper.isStringNotEmpty(pw)) {
            requestData = {
                "password": pw,
                "userName": ac
            };
        } else {
            requestData = {};
            requestHeader = UserService.getHeader();
            if (PropertiesHelper.isStringNotEmpty(requestHeader.uid)) {
                requestHeader.refreshKey = localStorage.getItem("refreshKey");
            } else {
                requestHeader = null;
            }
        }
        let request;
        if (requestHeader != null) {
            message.info("尝试用令牌刷新秘钥自动登录")
            // 刷新令牌
            request = axios.post("/sign/in", {}, {headers: requestHeader});
        } else {
            // 账号密码登录
            request = axios.post("/sign/in", requestData, {headers: UserService.getHeader()});
        }

        request.then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<SignInResponse> = response.data;
                    if (data.main?.user != null) {
                        let user = data.main.user;
                        localStorage.setItem('uid', user.uid);
                        localStorage.setItem('token', data.main.token);
                        localStorage.setItem('refreshKey', data.main.refreshKey);
                        localStorage.setItem('currentUser', JSON.stringify(user));
                        message.info("用户信息已更新")
                    }

                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}


export interface ArticleConfiguration {
    backgroundMusicType: string,
    mediaPlayType: string,
    src: string,
    coverSrc?: string,
    name?: string,
    artist?: string,
    lrc?: string
}

export interface TopicDto {
    tid: string,
    topicName: string,
    orderVal: number
}

export interface CategoryDto {
    cid: string,
    categoryName: string,
    categoryType: string,
    orderVal: number,
    articleCount?: number
}

export interface CategoryTreeInfo {
    topicInfo: TopicDto,
    categoryList: CategoryDto[],
}

export interface ArticleDto {
    aid: string,
    configuration: ArticleConfiguration,
    categoryTreeInfo: CategoryTreeInfo,
    cid: string,
    uid: string,
    title: string,
    imageSrc: string,
    summary: string,
    content: string,
    wordCount: number,
    pageViews: number,
    selfPageViews: number,
    orderGlobal: number,
    orderCategory: number,
    articleState: string,
    createTs: number,
    lastUpdateTs: number
}

export class ArticleService {
    static findArticleByAid(aid?: string | null,
                            successHook?: (input?: HyggeResponse<ArticleDto>) => void,
                            beforeHook?: () => void,
                            finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        if (!PropertiesHelper.isStringNotEmpty(aid)) {
            if (successHook != null) {
                successHook();
            }
            return;
        }

        axios.get("/main/article/" + aid, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static createArticle(article: ArticleDto,
                         successHook?: (input?: HyggeResponse<ArticleDto>) => void,
                         beforeHook?: () => void,
                         finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.post("/main/article", article, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static updateArticle(aid: string,
                         article: ArticleDto,
                         successHook?: (input?: HyggeResponse<ArticleDto>) => void,
                         beforeHook?: () => void,
                         finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.put("/main/article/" + aid, article, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}

export interface TopicOverviewInfo {
    topicInfo: TopicDto,
    categoryListInfo: CategoryDto[],
    totalCount?: number
}

export interface AllOverviewInfo {
    topicOverviewInfoList: TopicOverviewInfo[];
    articleSummaryInfo: ArticleSummaryResponse;
    quoteInfo: QuoteResponse;
    announcementInfoList: AnnouncementDto[];
}

export interface AnnouncementDto {
    announcementId: number,
    paragraphList: string[],
    color: string,
    createTs: number
}

export interface ArticleSummaryInfo {
    aid: string,
    categoryTreeInfo: {
        topicInfo: TopicDto,
        categoryList: CategoryDto[]
    },
    cid: string,
    uid: string,
    title: string,
    imageSrc: string,
    summary: string,
    wordCount: number,
    orderGlobal: number,
    orderCategory: number,
    pageViews: number,
    selfPageViews: number,
    articleState: string,
    createTs: number,
    lastUpdateTs: number
}

export interface ArticleSummaryResponse {
    articleSummaryList: ArticleSummaryInfo[],
    totalCount: number
}

export interface QuoteDto {
    quoteId: number,
    uid: string,
    imageSrc?: string,
    content: string,
    source?: string,
    portal?: string,
    remarks?: string,
    orderVal?: number,
    quoteState?: string,
}

export interface QuoteResponse {
    quoteList: QuoteDto[],
    totalCount: number
}

export class HomePageService {
    static fetch(successHook?: (input?: HyggeResponse<AllOverviewInfo>) => void,
                 beforeHook?: () => void,
                 finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/fetch", {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<AllOverviewInfo> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static fetchAnnouncement(successHook?: (input?: HyggeResponse<AnnouncementDto[]>) => void,
                             beforeHook?: () => void,
                             finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/fetch/announcement", {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<AnnouncementDto[]> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static fetchQuote(currentPage: number,
                      pageSize: number,
                      successHook?: (input?: HyggeResponse<QuoteResponse>) => void,
                      beforeHook?: () => void,
                      finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/fetch/quote" + "?currentPage=" + currentPage + "&pageSize=" + pageSize, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<QuoteResponse> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static fetchArticleSummaryByTid(tid: string,
                                    currentPage: number,
                                    pageSize: number,
                                    successHook?: (input?: HyggeResponse<ArticleSummaryResponse>) => void,
                                    beforeHook?: () => void,
                                    finallyHook?: () => void): void {

        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/fetch/topic/" + tid + "?currentPage=" + currentPage + "&pageSize=" + pageSize, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleSummaryResponse> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static fetchArticleSummaryByCid(cid: string,
                                    currentPage: number,
                                    pageSize: number,
                                    successHook?: (input?: HyggeResponse<ArticleSummaryResponse>) => void,
                                    beforeHook?: () => void,
                                    finallyHook?: () => void): void {

        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/fetch/category/" + cid + "?currentPage=" + currentPage + "&pageSize=" + pageSize, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleSummaryResponse> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static fetchArticleSummaryByKeyword(keyword: string,
                                        currentPage: number,
                                        pageSize: number,
                                        successHook?: (input?: HyggeResponse<ArticleSummaryResponse>) => void,
                                        beforeHook?: () => void,
                                        finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/search/article?keyword=" + keyword + "&currentPage=" + currentPage + "&pageSize=" + pageSize, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<ArticleSummaryResponse> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static fetchQuoteByKeyword(keyword: string,
                               currentPage: number,
                               pageSize: number,
                               successHook?: (input?: HyggeResponse<QuoteResponse>) => void,
                               beforeHook?: () => void,
                               finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("main/home/search/quote?keyword=" + keyword + "&currentPage=" + currentPage + "&pageSize=" + pageSize, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<QuoteResponse> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}

export class QuoteService {
    static findQuoteByQuoteId(quoteId?: string | null,
                              successHook?: (input?: HyggeResponse<QuoteDto>) => void,
                              beforeHook?: () => void,
                              finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        if (!PropertiesHelper.isStringNotEmpty(quoteId)) {
            if (successHook != null) {
                successHook();
            }
            return;
        }

        axios.get("/main/quote/" + quoteId, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<QuoteDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static createQuote(quote: QuoteDto,
                       successHook?: (input?: HyggeResponse<QuoteDto>) => void,
                       beforeHook?: () => void,
                       finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.post("/main/quote", quote, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<QuoteDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static updateQuote(quoteId: string,
                       quote: QuoteDto,
                       successHook?: (input?: HyggeResponse<QuoteDto>) => void,
                       beforeHook?: () => void,
                       finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.put("/main/quote/" + quoteId, quote, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<QuoteDto> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}

export interface FileInfo {
    fileNo: string,
    name: string,
    src: string,
    extension: string,
    fileType: string,
    fileSize: string,
    isInHardDisk?: Boolean,
    description?: FileDescription
}

export interface FileInfoInfo {
    fileInfoList: FileInfo[],
    totalCount: number,
}

export interface FileDescription {
    content: string,
    timePointer: number
}

export class FileService {
    static findFileInfo(fileNo: string,
                        successHook?: (input?: HyggeResponse<FileInfo>) => void,
                        beforeHook?: () => void,
                        finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        axios.get("/main/file/" + fileNo, {
            headers: UserService.getHeader({})
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<FileInfo> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static findFileInfoMulti(type?: string[],
                             successHook?: (input?: HyggeResponse<FileInfoInfo>) => void,
                             beforeHook?: () => void,
                             finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        let actualPath: string = type == undefined ? "" : "?type=" + type.join(",")

        axios.get("/main/file" + actualPath, {
            headers: UserService.getHeader({})
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<FileInfoInfo> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static updateFileInfo(fileInfo: FileInfo,
                          successHook?: (input?: any) => void,
                          beforeHook?: () => void,
                          finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }
        axios.put("/main/file/" + fileInfo.fileNo, fileInfo, {
            headers: UserService.getHeader({})
        }).then((response) => {
                if (successHook != null && response != null) {
                    successHook();
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }

    static downloadFilePromise(fileNo: string): Promise<AxiosResponse> {
        // 全局被加了特殊拦截器，不适用于下载文件，此处为新创建实例
        return axios.create().get("/main/file/static/" + fileNo, {
            headers: UserService.getHeader({}),
            responseType: 'arraybuffer'
        })
    }

    static saveFile(responsePromise: Promise<AxiosResponse>, fileName: string) {
        responsePromise.then((response) => {
            let type: string = response.headers['content-type'];
            let blob = new Blob([response.data], {type: type});
            saveAs(blob, fileName);
        })
    }

    static assignBlobImageToElement(responsePromise: Promise<AxiosResponse>, ...elements: HTMLImageElement[]) {
        if (elements.length < 1) {
            message.warning("未找到对应图片展示标签！");
            return;
        }
        responsePromise.then((response) => {
            let type: string = response.headers['content-type'];
            let blob = new Blob([response.data], {type: type});
            let url = URL.createObjectURL(blob);

            elements.forEach((element) => {
                element.src = url;
                element.onload = function () {
                    URL.revokeObjectURL(url);
                };
            });
            message.info("图片加载成功")
        })
    }

    static deleteFile(fileNo: string,
                      successHook?: (input?: HyggeResponse<string>) => void,
                      beforeHook?: () => void,
                      finallyHook?: () => void): void {
        axios.delete("/main/file/" + fileNo, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<string> = response.data;
                    successHook(data);
                }
            }
        ).finally(() => {
            if (finallyHook != null) {
                finallyHook();
            }
        });
    }
}
