import React, {createContext, useMemo, useState} from 'react';
import {Layout} from "antd";
import IndexLeft from "../component/index/IndexLeft";
import IndexRight from "../component/index/IndexRight";

import "../../../style/index.less"

export interface IndexState {
    // 菜单是否折叠收起
    menuFolded: boolean;
    updateMenuFolded: Function;
}

function Index() {
    const [menuFolded, updateMenuFolded] = useState(true);

    const state = useMemo(() => ({
        menuFolded: menuFolded,
        updateMenuFolded: updateMenuFolded
    }), [menuFolded]);

    return (
        <IndexContext.Provider value={state}>
            <Layout>
                <IndexLeft/>
                <IndexRight/>
            </Layout>
        </IndexContext.Provider>
    );
}

export const IndexContext = createContext<IndexState>({} as IndexState);
export default Index;