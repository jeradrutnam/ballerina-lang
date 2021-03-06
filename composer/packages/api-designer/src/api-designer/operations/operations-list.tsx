/**
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import * as Swagger from "openapi3-ts";
import * as React from "react";
import { Accordion, AccordionTitleProps } from "semantic-ui-react";

import { OpenApiContext, OpenApiContextConsumer } from "../components/context/open-api-context";

import InlineEdit from "../components/utils/inline-edit";

import OpenApiParameterList from "../parameter/parameter-list";
import OpenApiResponseList from "../response/response-list";

import OpenApiAddParameter from "../parameter/add-parameter";

interface OpenApiOperationProp {
    pathItem: Swagger.PathItemObject;
    path: string;
    showType: string;
}

interface OpenApiOperationState {
    activeIndex: number[];
    showAddParameter: number[];
}

class OpenApiOperation extends React.Component<OpenApiOperationProp, OpenApiOperationState> {
    constructor(props: OpenApiOperationProp) {
        super(props);

        this.state = {
            activeIndex: [],
            showAddParameter: []
        };

        this.onAccordionTitleClick = this.onAccordionTitleClick.bind(this);
    }

    public componentWillReceiveProps(nextProps: OpenApiOperationProp) {
        const { pathItem, showType } = nextProps;
        const activeOperations: number[] = [];

        if (showType === "operations" || showType === "all") {
            Object.keys(pathItem).sort().map((openApiOperation, index) => {
                activeOperations.push(index);
            });
        }

        this.setState({
            activeIndex: activeOperations
        });
    }

    public render() {
        const { pathItem, path } = this.props;
        const { activeIndex, showAddParameter } = this.state;

        return (
            <Accordion>
                {Object.keys(pathItem).sort().map((openApiOperation, index) => {
                    return(
                        <React.Fragment>
                            <Accordion.Title
                                active={activeIndex.includes(index)}
                                index={index}
                                onClick={this.onAccordionTitleClick} >
                                <span className="op-method">{openApiOperation}</span>
                                <OpenApiContextConsumer>
                                    {(appContext: OpenApiContext) => {
                                        return (
                                            <InlineEdit
                                                changeModel={appContext.openApiJson}
                                                changeAttribute={{
                                                    changeValue: openApiOperation,
                                                    key: "operation.summary",
                                                    path
                                                }}
                                                text={pathItem[openApiOperation].summary}
                                                placeholderText="Add a summary"
                                                onInlineValueChange={appContext.onInlineValueChange}
                                            />
                                        );
                                    }}
                                </OpenApiContextConsumer>
                            </Accordion.Title>
                            <Accordion.Content active={activeIndex.includes(index)}>
                                <OpenApiContextConsumer>
                                    {(appContext: OpenApiContext) => {
                                        return (
                                            <InlineEdit
                                                changeModel={appContext.openApiJson}
                                                changeAttribute={{
                                                    changeValue: openApiOperation,
                                                    key: "operation.description",
                                                    path
                                                }}
                                                text={pathItem[openApiOperation].description}
                                                placeholderText="Add a description"
                                                onInlineValueChange={appContext.onInlineValueChange}
                                            />
                                        );
                                    }}
                                </OpenApiContextConsumer>
                                <div className="op-section">
                                    <div className="title">
                                        <p>Parameters</p>
                                        <a onClick={() => {
                                            this.handleShowAddParameter(index);
                                        }} >Add Parameter</a>
                                    </div>
                                    {showAddParameter.includes(index) &&
                                        <OpenApiContextConsumer>
                                            {(appContext: OpenApiContext) => {
                                                return (
                                                    <OpenApiAddParameter
                                                        openApiJson={appContext.openApiJson}
                                                        onAddParameter={appContext!.onAddOpenApiParameter}
                                                        operation={openApiOperation}
                                                        resourcePath={path}
                                                    />
                                                );
                                            }}
                                        </OpenApiContextConsumer>

                                    }
                                    {pathItem[openApiOperation].parameters &&
                                        <OpenApiParameterList
                                            parameterList={pathItem[openApiOperation].parameters}
                                        />
                                    }
                                </div>
                                <div className="op-section">
                                    <div className="title">
                                        <p>Responses</p>
                                    </div>
                                    {pathItem[openApiOperation].responses &&
                                        <OpenApiResponseList
                                            responsesList={pathItem[openApiOperation].responses}
                                        />
                                    }
                                </div>
                            </Accordion.Content>
                        </React.Fragment>
                    );
                })}
            </Accordion>
        );
    }

    public handleShowAddParameter(id: number) {
        if (this.state.showAddParameter.includes(id)) {
            this.setState({showAddParameter: this.state.showAddParameter.filter((index) => {
                return index !== id;
            })});

        } else {
            this.setState({
                showAddParameter: [...this.state.showAddParameter, id],
            });
        }
    }

    private onAccordionTitleClick(e: React.MouseEvent, titleProps: AccordionTitleProps) {
        const { index } = titleProps;
        const { activeIndex } = this.state;

        this.setState({
            activeIndex: !activeIndex.includes(Number(index)) ?
                [...this.state.activeIndex, Number(index)] : this.state.activeIndex.filter((i) => i !== index)
        });
    }
}

export default OpenApiOperation;
