import * as React from "react"
import {IndexContainerState} from "../IndexContainer";
import {EditArticleContainerState} from "../EditArticleContainer";


export const IndexContainerContext = React.createContext<IndexContainerState>(
    {}
);

export const EditArticleContainerContext = React.createContext<EditArticleContainerState>(
    {}
);