import {Outlet} from "react-router-dom";
import AppFooter from "./AppFooter.tsx";
import AppNavBar from "./AppNavBar.tsx";
import {Layout} from "antd";

export default function AppLayout() {
    return (
        <Layout>
            <AppNavBar/>
            <main>
                {/*这个标签约等于 AppLayout 嵌套子对象的占位符替代*/}
                <Outlet/>
            </main>
            <AppFooter/>
        </Layout>
    );
}
