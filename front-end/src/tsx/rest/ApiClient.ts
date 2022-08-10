import axios from "axios";
import {message} from "antd";
import {PropertiesHelper, UrlHelper} from "../utils/UtilContainer";

axios.defaults.baseURL = "http://localhost:8080/blog-service/api";
axios.interceptors.response.use(function (response) {
    if (response.data == null) {
        return null;
    }
    let code = response.data?.code;

    // 对响应数据做点什么
    if (code == 200) {
        return response;
    } else if (code == 403002) {
        // 令牌刷新失败，无法自动登录
        message.warn("自动刷新令牌失败，2 秒内为您跳转回主页", 2);
        UrlHelper.openNewPage({inNewTab: false, delayTime: 2000});
        return null;
    } else if (code == 403003) {
        // 令牌过期，尝试自动刷新
        UrlHelper.openNewPage({inNewTab: false, path: "#/signin/auto"});
        return null;
    } else if (code == 403000) {
        // 账号、密码、令牌错误允许外部组件自行处理
        message.warn(response.data.msg, 3);
        return response;
    } else if (code == 400 || code < 500000) {
        message.warn(response.data.msg, 5);
        return null;
    } else {
        message.error(response.data.msg, 5);
        return null;
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
        } else {
            result = currentHeader;
        }
        result["Content-Type"] = this.getContentType();
        result.scope = this.getCurrentScope();

        let currentSecretKey = UrlHelper.getQueryString("secretKey");
        if (currentSecretKey != null) {
            result.secretKey = currentSecretKey;
        }

        let currentUId = localStorage.getItem("uid");
        let currentToken = localStorage.getItem("token");
        let currentRefreshKey = localStorage.getItem("refreshKey");
        if (currentUId == null || currentToken == null || currentRefreshKey == null) {
            message.warn("登录信息不完整，已清空")
            this.removeCurrentUser();
        } else {
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
        let requestData = null;
        if (PropertiesHelper.isStringNotEmpty(ac) && PropertiesHelper.isStringNotEmpty(pw)) {
            requestData = {
                "password": pw,
                "userName": ac
            };
        } else {
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
            request = axios.post("/sign/in", requestData);
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
    title: string,
    imageSrc: string,
    summary: string,
    content: string,
    wordCount: number,
    pageViews: number,
    selfPageViews: number,
    orderGlobal: number,
    orderCategory: number,
    articleState: string
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
    title: string,
    imageSrc: string,
    summary: string,
    wordCount: number,
    orderGlobal: number,
    orderCategory: number,
    pageViews: number,
    selfPageViews: number,
    createTs: number,
    lastUpdateTs: number
}

export interface ArticleSummaryResponse {
    articleSummaryList: ArticleSummaryInfo[],
    totalCount: number
}

export interface QuoteDto {
    quoteId: number,
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
    src: string,
    name: string,
    fileSize: number
}

export class FileService {
    static findFileInfo(type: string[],
                        successHook?: (input?: HyggeResponse<FileInfo[]>) => void,
                        beforeHook?: () => void,
                        finallyHook?: () => void): void {
        if (beforeHook != null) {
            beforeHook();
        }

        let typeInfo: string = "";
        type.forEach((item) => {
            typeInfo = typeInfo + item;
        });

        axios.get("/main/file?type=" + typeInfo, {
            headers: UserService.getHeader()
        }).then((response) => {
                if (successHook != null && response != null) {
                    let data: HyggeResponse<FileInfo[]> = response.data;
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
