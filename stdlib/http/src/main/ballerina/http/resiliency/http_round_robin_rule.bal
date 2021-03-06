// Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

# Implementation of round robin load balancing strategy.
#
# + index - Keep tracks the current point of the Client[]
public type LoadBalancerRounRobinRule object {

    public int index = 0;

    # Provides an HTTP client which is choosen according to the round robin algorithm.
    #
    # + loadBalanceCallerActionsArray - Array of HTTP clients which needs to be load balanced
    # + return - Choosen `Client` from the algorithm or an `error` for a failure in
    #            the algorithm implementation
    public function getNextClient(Client[] loadBalanceCallerActionsArray) returns Client|error;
};

public function LoadBalancerRounRobinRule.getNextClient(Client[] loadBalanceCallerActionsArray)
                                       returns Client|error {
    Client httpClient = loadBalanceCallerActionsArray[self.index];
    lock {
        if (self.index == ((loadBalanceCallerActionsArray.length()) - 1)) {
            httpClient = loadBalanceCallerActionsArray[self.index];
            self.index = 0;
        } else {
            httpClient = loadBalanceCallerActionsArray[self.index];
            self.index += 1;
        }
    }
    return httpClient;
}
