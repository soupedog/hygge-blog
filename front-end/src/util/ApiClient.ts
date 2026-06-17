import {message} from 'antd';
import UrlHelper from './UrlHelper.ts';
import StorageHelper from './StorageHelper.ts';
import {ClientScope, StorageKey} from '../enums/EnumKeeper.ts';
import PropertiesHelper from './PropertiesHelper.ts';
import {httpClient} from './HttpClient.ts';
import {appConfiguration} from '../configuration/app.configuration.ts';

const toastZIndex = appConfiguration.toastDefaultZIndex;

export interface HyggeResponse<T> {
    code: number;
    msg?: string;
    main?: T;
}

const emptyResponse = {} as HyggeResponse<unknown>;

export interface PageQuery {
    currentPage: number;
    pageSize: number;
}

export interface PageQueryResponse {
    totalCount: number;
}

export interface UserDto {
    uid: string;
    userAvatar: string;
    userSex: string;
    biography?: string;
    birthday?: number;
    phone?: string;
    email?: string;
}

export interface SignInRequest {
    ac?: string;
    pw?: string;
}

export interface SignInResponse {
    user?: UserDto;
    token: string;
    refreshKey: string;
    deadline: number;
}

export class UserClient {
    private static clientScopeInstance: ClientScope = ClientScope.WEB;

    /** 初始化（在 App 组件中调用一次） */
    static init(clientScope: ClientScope) {
        this.clientScopeInstance = clientScope;
    }

    static getCurrentUser(): UserDto | undefined {
        return StorageHelper.get<UserDto>(StorageKey.USER_INFO);
    }

    static removeCurrentUser() {
        StorageHelper.remove(StorageKey.USER_UID);
        StorageHelper.remove(StorageKey.USER_TOKEN);
        StorageHelper.remove(StorageKey.USER_REFRESH_KEY);
        StorageHelper.remove(StorageKey.USER_INFO);
    }

    static getCurrentScope(): ClientScope {
        return this.clientScopeInstance;
    }

    static getDefaultContentType(): string {
        return 'application/json';
    }

    static getHeader(currentHeader?: any): any {
        let result;

        if (currentHeader == null) {
            result = {};
            // @ts-ignore
            result['Content-Type'] = this.getDefaultContentType();
        } else {
            result = currentHeader;
        }
        result.scope = this.getCurrentScope();

        let currentSecretKey = UrlHelper.getQueryString('secretKey');
        if (currentSecretKey != null) {
            result.secretKey = currentSecretKey;
        }

        let currentUId = StorageHelper.get<string>(StorageKey.USER_UID);
        let currentToken = StorageHelper.get<string>(StorageKey.USER_TOKEN);
        let currentRefreshKey = StorageHelper.get<string>(StorageKey.USER_REFRESH_KEY);
        if (currentUId != null && currentToken != null && currentRefreshKey != null) {
            result.uid = currentUId;
            result.token = currentToken;
        }
        return result;
    }

    static async signIn(input?: SignInRequest): Promise<SignInResponse> {
        let requestHeader = null;
        let requestData;
        if (input != null && PropertiesHelper.isStringNotEmpty(input.ac) && PropertiesHelper.isStringNotEmpty(input.pw)) {
            requestData = {
                'password': input.pw,
                'userName': input.ac
            };
        } else {
            requestData = {};
            requestHeader = UserClient.getHeader();
            if (PropertiesHelper.isStringNotEmpty(requestHeader.uid)) {
                requestHeader.refreshKey = StorageHelper.get(StorageKey.USER_REFRESH_KEY);
            } else {
                requestHeader = null;
            }
        }
        let clientResponse;

        if (requestHeader != null) {
            StorageHelper.set(StorageKey.AUTO_LOGIN_DISABLED, '已禁止再次触发自动登陆');
            message.info({content: '尝试用令牌刷新秘钥自动登录。', style: {zIndex: toastZIndex}});
            // 刷新令牌
            clientResponse = await httpClient.post('/sign/in', {}, {headers: requestHeader});
        } else {
            // 账号密码登录
            clientResponse = await httpClient.post('/sign/in', requestData, {headers: UserClient.getHeader()});
        }

        // 能到这里说明没被拦截器拦截，已经正确请求到后端服务器
        const response: HyggeResponse<any> = clientResponse.data;

        if (response.code == 200) {
            // 每次登录成功则运行重试刷新令牌至少一次
            let user = response.main.user;
            StorageHelper.set(StorageKey.USER_UID, user.uid);
            StorageHelper.set(StorageKey.USER_TOKEN, response.main.token);
            StorageHelper.set(StorageKey.USER_REFRESH_KEY, response.main.refreshKey);
            StorageHelper.set(StorageKey.USER_INFO, user);
            StorageHelper.remove(StorageKey.AUTO_LOGIN_DISABLED);
            message.info({content: '用户登录缓存信息已更新！', style: {zIndex: toastZIndex}});
        }

        return response.main;
    }
}

export interface ArticleConfiguration {
    backgroundMusicType: string;
    mediaPlayType: string;
    src: string;
    coverSrc?: string;
    name?: string;
    artist?: string;
    lrc?: string;
}

export interface TopicDto {
    tid: string;
    topicName: string;
    orderVal: number;
}

export interface CategoryDto {
    cid: string;
    permissionId: number;
    categoryName: string;
    categoryType: string;
    orderVal: number;
    articleCount: number;
}

export interface CategoryTreeInfo {
    topicInfo: TopicDto;
    categoryList: CategoryDto[];
}

export interface ArticleDto {
    aid: string;
    configuration: ArticleConfiguration;
    categoryTreeInfo: CategoryTreeInfo;
    cid: string;
    uid: string;
    title: string;
    imageSrc: string;
    coverFileNo: string;
    summary: string;
    content: string;
    wordCount: number;
    pageViews: number;
    selfPageViews: number;
    orderGlobal: number;
    orderCategory: number;
    articleState: string;
    createTs: number;
    lastUpdateTs: number;
    editable: boolean;
}

export class PostClient {

    static async findArticleByAid(aid: string): Promise<ArticleDto> {
        const clientResponse = await httpClient
            .get(`/main/article/${aid}`,
                {
                    headers: UserClient.getHeader()
                }
            );
        return clientResponse.data.main;
    }
}

export interface KeywordSearchInput extends PageQuery {
    keyword: string;
}

export interface PostInCategorySearchInput extends PageQuery {
    cid: string;
}

export interface ArticleSummaryResponse extends PageQueryResponse {
    articleSummaryList: ArticleDto[];
}

export interface QuoteDto {
    quoteId: number;
    uid: string;
    imageSrc?: string;
    coverFileNo: string;
    content: string;
    source?: string;
    portal?: string;
    remarks?: string;
    orderVal?: number;
    quoteState?: string;
    editable: boolean;
}

// 纯前端的业务对象
export interface FePageQueryResponse extends PageQueryResponse {
    dataSet: Array<QuoteDto | ArticleDto>;
}

export interface QuoteResponse extends PageQueryResponse {
    quoteList: QuoteDto[];
}

export interface TopicOverviewInfo extends PageQueryResponse {
    topicInfo: TopicDto,
    categoryListInfo: CategoryDto[],
}

export interface AnnouncementDto {
    announcementId: number,
    paragraphList: string[],
    color: string,
    createTs: number
}

export interface AllOverviewInfo {
    topicOverviewInfoList: TopicOverviewInfo[];
    articleSummaryInfo: ArticleSummaryResponse;
    quoteInfo: QuoteResponse;
    announcementInfoList: AnnouncementDto[];
}

export class HomeClient {

    static async fetch(): Promise<AllOverviewInfo> {
        const clientResponse = await httpClient
            .get(`main/home/fetch`,
                {
                    headers: UserClient.getHeader()
                }
            );
        return clientResponse.data.main;
    }

    static async fetchPostSummaryByCid(input: PostInCategorySearchInput): Promise<ArticleSummaryResponse> {
        const clientResponse = await httpClient
            .get(`main/home/fetch/category/${input.cid}?currentPage=${input.currentPage}&pageSize=${input.pageSize}`, {
                    headers: UserClient.getHeader()
                }
            );
        return clientResponse.data.main;
    }

    static async searchPostSummaryByKeyword(input: KeywordSearchInput): Promise<ArticleSummaryResponse> {
        const clientResponse = await httpClient
            .get(`main/home/search/article?keyword=${input.keyword}&currentPage=${input.currentPage}&pageSize=${input.pageSize}`,
                {
                    headers: UserClient.getHeader()
                }
            );
        return clientResponse.data.main;
    }

    static async searchQuoteByKeyword(input: KeywordSearchInput): Promise<QuoteResponse> {
        const clientResponse = await httpClient
            .get(`main/home/search/quote?keyword=${input.keyword}&currentPage=${input.currentPage}&pageSize=${input.pageSize}`,
                {
                    headers: UserClient.getHeader()
                }
            );
        return clientResponse.data.main;
    }
}
