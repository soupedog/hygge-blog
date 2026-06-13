import "./Loading.css"
import {appConfiguration} from '../configuration/app.configuration'

export default function Loading() {
    const icpInfo: string = appConfiguration.icpInfo;
    const codePrefix: string = appConfiguration.codePrefix;
    const code: string = appConfiguration.code;

    return (
        <div className="lazy-fallback-wrapper">
            <div className="lazy-fallback-card">
                <div className="lazy-fallback-main">
                    <div className="lazy-fallback-header">
                        <div className="lazy-fallback-icon">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                                 xmlns="http://www.w3.org/2000/svg">
                                <path d="M12 8v4l2.5 2.5" stroke="currentColor" strokeWidth="1.6"
                                      strokeLinecap="round"/>
                                <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="1.6"/>
                            </svg>
                        </div>
                        <div className="lazy-fallback-title">
                            <h3>依赖正在准备</h3>
                            <p>核心资源加载中，请稍后</p>
                        </div>
                    </div>

                    <div className="lazy-fallback-list">
                        <div className="lazy-fallback-item">
                            <div className="lazy-fallback-label">
                                <span>🌐</span>
                                <span>当前站点网络带宽</span>
                            </div>
                            <div className="lazy-fallback-value"><span
                                className="lazy-fallback-highlight">≈ 625 kb/s</span>
                            </div>
                        </div>
                        <div className="lazy-fallback-item">
                            <div className="lazy-fallback-label">
                                <span>📦</span>
                                <span>总依赖大小</span>
                            </div>
                            <div className="lazy-fallback-value"><span
                                className="lazy-fallback-highlight">≈ 1.3 MB</span>
                            </div>
                        </div>
                        <div className="lazy-fallback-item">
                            <div className="lazy-fallback-label">
                                <span>⏱️</span>
                                <span>预计加载时间</span>
                            </div>
                            <div className="lazy-fallback-value"><span
                                className="lazy-fallback-highlight">≈ 2.1 秒(理论最优)</span>
                            </div>
                        </div>
                    </div>

                    <div className="lazy-fallback-compat">
                        <div className="lazy-fallback-compat-content">
                            <p>
                                <strong>⏱️ 长时间停留此页面？</strong>
                            </p>
                            <p>
                                如果加载时间过长，请检查您的浏览器兼容性。本网站使用了现代 Web 标准，<strong>推荐使用
                                WebKit
                                内核的现代化浏览器</strong> 以获得最佳体验，例如：
                            </p>
                            <div className="lazy-fallback-browser-list">
                                <span className="lazy-fallback-browser-badge">Google Chrome</span>
                                <span className="lazy-fallback-browser-badge">Microsoft Edge (Chromium)</span>
                                <span className="lazy-fallback-browser-badge">Safari 15+</span>
                                <span className="lazy-fallback-browser-badge">Opera</span>
                            </div>
                            <p>
                                如您仍在使用老旧浏览器（如 IE 或部分旧版国产浏览器），请升级或切换至以上浏览器，确保页面功能正常渲染。
                            </p>
                        </div>
                    </div>

                    <div className="lazy-fallback-icp">
                        <div className="lazy-fallback-icp-line">
                            <a href={"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=" + code}
                               target="_blank" rel="noopener noreferrer" className="lazy-fallback-icp-shield">
                                <span className="lazy-fallback-icp-shield-icon">🛡️</span>
                                <span>{codePrefix + code}</span>
                            </a>
                            <span className="lazy-fallback-icp-sep">|</span>
                            <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">
                                {icpInfo}
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
