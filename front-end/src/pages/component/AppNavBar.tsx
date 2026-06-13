import {Link, NavLink, type NavLinkRenderProps} from "react-router-dom";

const navLinkStyle = ({isActive}: NavLinkRenderProps) => ({
    margin: "0 2rem",
    textDecoration: "none",
    color: isActive ? "tomato" : "black",
    fontWeight: isActive ? "bold" : "normal"
});

export default function AppNavBar() {
    return (
        <nav>
            <NavLink to={"/"} style={navLinkStyle}>主页</NavLink>
            <NavLink to={"/login"} style={navLinkStyle}>登录页</NavLink>
            {/*path前缀相同会导致 isActive 激活多个，此处改为 Link*/}
            {/*但如果顺序颠倒，path 长的在前，也可阻止重复激活*/}
            <Link to={"/goods/detail/8"} style={{margin: "0 2rem",}}>商品8</Link>
            <Link to={"/goods"} style={{margin: "0 2rem",}}>商品搜索页</Link>
            <NavLink to={"/musicPlayer"} style={navLinkStyle}>音乐播放器</NavLink>
        </nav>
    );
}
