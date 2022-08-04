import * as React from "react"
import {IndexContainerStatus} from "../IndexContainer";


export const IndexContainerContext = React.createContext<IndexContainerStatus>(
    {}
);