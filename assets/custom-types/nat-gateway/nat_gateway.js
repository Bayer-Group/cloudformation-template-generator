// Source http://www.spacevatican.org/2015/12/20/cloudformation-nat-gateway/
// https://gist.github.com/fcheung/baec53381350a4b11037

var aws = require('aws-sdk');

exports.handler = function(event, context) {
    if (event.ResourceType === 'Custom::NatGateway') {
        handleGateway(event, context);
    } else if (event.ResourceType === 'Custom::NatGatewayRoute') {
        handleRoute(event, context);
    } else {
        errMsg = "unknown resource type: " + event.ResourceType;
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
    }
};

var handleRoute = function(event, context) {
    var destinationCidrBlock = event.ResourceProperties.DestinationCidrBlock;
    var routeTableId = event.ResourceProperties.RouteTableId;
    if (!destinationCidrBlock) {
        errMsg = "missing parameter DestinationCidrBlock";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return;
    }
    else {
        if (!routeTableId) {
            errMsg = "missing parameter RouteTableId";
            console.log(errMsg);
            response.send(event, context, response.FAILED, errMsg);
            return;
        }
    }

    if (event.RequestType === 'Delete') {
        deleteRoute(event, context);
    } else if (event.RequestType === 'Create') {
        createRoute(event, context);
    } else if (event.RequestType === 'Update') {
        if (event.ResourceProperties.DestinationCIDRBlock === event.OldResourceProperties.DestinationCIDRBlock &&
            event.ResourceProperties.RouteTableId === event.OldResourceProperties.RouteTableId) {
            replaceRoute(event, context);
        } else {
            createRoute(event, context);
        }
    } else {
        errMsg = "unknown request type: " + event.RequestType;
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
    }
};

var deleteRoute = function(event, context) {
    var destinationCidrBlock = event.ResourceProperties.DestinationCidrBlock;
    var routeTableId = event.ResourceProperties.RouteTableId;

    if(event.PhysicalResourceId.match(/^gateway-route-/)){
        var ec2 = new aws.EC2();
        ec2.deleteRoute({
            RouteTableId: routeTableId,
            DestinationCidrBlock: destinationCidrBlock
        }, function(err, data) {
            if (err) {
                if (err.code != "InvalidRoute.NotFound") {
                    errMsg = "WARNING: " + err;
                    console.log(errMsg);
                    response.send(event, context, response.SUCCESS, errMsg, {}, physicalId(event.ResourceProperties));
                } else {
                    errMsg = "delete route failed" + err;
                    console.log(errMsg);
                    response.send(event, context, response.FAILED, errMsg);
                }
            } else {
                response.send(event, context, response.SUCCESS, null, {}, physicalId(event.ResourceProperties));
            }
        });
    } else {
        errMsg = "unexpected physical id for route " + event.PhysicalResourceId + " - ignoring";
        console.log(errMsg);
        response.send(event, context, response.SUCCESS, errMsg);
    }
};


var createRoute = function(event, context) {
    var destinationCidrBlock = event.ResourceProperties.DestinationCidrBlock;
    var routeTableId = event.ResourceProperties.RouteTableId;
    var natGatewayId = event.ResourceProperties.NatGatewayId;

    if (natGatewayId) {
        var ec2 = new aws.EC2();
        ec2.createRoute({
            RouteTableId: routeTableId,
            DestinationCidrBlock: destinationCidrBlock,
            NatGatewayId: natGatewayId
        }, function(err, data) {
            if (err) {
                errMsg = "create route failed: " + err;
                console.log(errMsg);
                response.send(event, context, response.FAILED, errMsg);

            } else {
                response.send(event, context, response.SUCCESS, null, {}, physicalId(event.ResourceProperties));
            }
        });
    } else {
        errMsg = "missing parameter natGatewayId";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return;
    }
};

var replaceRoute = function(event, context) {
    var destinationCidrBlock = event.ResourceProperties.DestinationCidrBlock;
    var routeTableId = event.ResourceProperties.RouteTableId;
    var natGatewayId = event.ResourceProperties.NatGatewayId;

    if (natGatewayId) {
        var ec2 = new aws.EC2();
        ec2.replaceRoute({
            RouteTableId: routeTableId,
            DestinationCidrBlock: destinationCidrBlock,
            NatGatewayId: natGatewayId
        }, function(err, data) {
            if (err) {
                errMsg = "create route failed: " + err;
                console.log(errMsg);
                response.send(event, context, response.FAILED, errMsg);

            } else {
                response.send(event, context, response.SUCCESS, null, {}, physicalId(event.ResourceProperties));
            }
        });
    } else {
        errMsg = "missing parameter natGatewayId";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return;
    }
};


var physicalId = function(properties) {
    return 'gateway-route-' + properties.RouteTableId + '-' + properties.DestinationCIDRBlock;
};


var handleGateway = function(event, context) {
    if (event.RequestType === 'Delete') {
        deleteGateway(event, context);
    } else if (event.RequestType === 'Update' || event.RequestType === 'Create') {
        createGateway(event, context);
    } else {
        errMsg = "unknown type: " + event.RequestType;
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
    }
};

var createGateway = function(event, context) {
    var subnetId = event.ResourceProperties.SubnetId;
    var allocationId = event.ResourceProperties.AllocationId;
    var waitHandle = event.ResourceProperties.WaitHandle;

    if (subnetId && allocationId) {
        var ec2 = new aws.EC2();
        ec2.createNatGateway({
            AllocationId: allocationId,
            SubnetId: subnetId
        }, function(err, data) {
            if (err) {
                errMsg = "create gateway failed: " + err;
                console.log(errMsg);
                response.send(event, context, response.FAILED, errMsg);
            } else {
                response.send(event, context, response.SUCCESS, null, {}, data.NatGateway.NatGatewayId, true);

                waitForGatewayStateChange(data.NatGateway.NatGatewayId, ['available', 'failed'], function(state){
                    if(waitHandle){
                        signalData = {
                            "Status": state == 'available' ? 'SUCCESS' : 'FAILURE',
                            "UniqueId": data.NatGateway.NatGatewayId,
                            "Data": "Gateway has state " + state,
                            "Reason": ""
                        };
                        sendSignal(waitHandle, context, signalData);
                    }else{
                        if(state != 'available'){
                            console.log("gateway state is not available");
                        }
                        context.done();
                    }
                });
            }
        })
    } else {
        if (!subnetId) {
            errMsg = "subnet id not specified";
            console.log(errMsg);
            response.send(event, context, response.FAILED, errMsg);
        } else {
            errMsg = "allocationId not specified";
            console.log(errMsg);
            response.send(event, context, response.FAILED, errMsg);
        }
    }
};

var waitForGatewayStateChange = function (id, states, onComplete){
    var ec2 = new aws.EC2();
    ec2.describeNatGateways({NatGatewayIds: [id], Filter: [{Name: "state", Values: states}]}, function(err, data){
        if(err){
            console.log("could not describeNatGateways " + err);
            onComplete('failed');
        }else{
            if(data.NatGateways.length > 0){
                onComplete(data.NatGateways[0].State)
            }else{
                console.log("gateway not ready; waiting");
                setTimeout(function(){ waitForGatewayStateChange(id, states, onComplete);}, 15000);
            }
        }
    });
};

var deleteGateway = function(event, context) {
    if (event.PhysicalResourceId && event.PhysicalResourceId.match(/^nat-/)) {
        var ec2 = new aws.EC2();
        ec2.deleteNatGateway({
            NatGatewayId: event.PhysicalResourceId
        }, function(err, data) {
            if (err) {
                errMsg = "delete gateway failed " + err;
                console.log(errMsg);
                response.send(event, context, response.FAILED, errMsg, null, event.PhysicalResourceId);
            } else {
                waitForGatewayStateChange(event.PhysicalResourceId, ['deleted'], function(state){
                    response.send(event, context, response.SUCCESS, null, {}, event.PhysicalResourceId);
                });
            }
        })
    } else {
        errMsg = "No valid physical resource id passed to destroy - ignoring " + event.PhysicalResourceId
        console.log(errMsg);
        response.send(event, context, response.SUCCESS, errMsg, null, event.PhysicalResourceId);
    }
};


var sendSignal = function(handle, context, data){
    var body = JSON.stringify(data);
    var https = require("https");
    var url = require("url");
    console.log("signal body:\n", body);

    var parsedUrl = url.parse(handle);
    var options = {
        hostname: parsedUrl.hostname,
        port: 443,
        path: parsedUrl.path,
        method: "PUT",
        headers: {
            "content-type": "",
            "content-length": body.length
        }
    };

    var request = https.request(options, function(response) {
        console.log("Status code: " + response.statusCode);
        console.log("Status message: " + response.statusMessage);
        context.done();
    });

    request.on("error", function(error) {
        console.log("sendSignal(..) failed executing https.request(..): " + error);
        context.done();
    });

    request.write(body);
    request.end();
};
/* The below section is adapted from the cfn-response module, as published at:

 http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-lambda-function-code.html

 */

/* Copyright 2015 Amazon Web Services, Inc. or its affiliates. All Rights Reserved.
 This file is licensed to you under the AWS Customer Agreement (the "License").
 You may not use this file except in compliance with the License.
 A copy of the License is located at http://aws.amazon.com/agreement/.
 This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied.
 See the License for the specific language governing permissions and limitations under the License. */
var response = {};

response.SUCCESS = "SUCCESS";
response.FAILED = "FAILED";

response.send = function(event, context, responseStatus, responseReason, responseData, physicalResourceId, continueFuncton) {
    reason = "CloudWatch Log Stream: " + context.logGroupName + " -- " + context.logStreamName
    if (responseReason) {
        reason = responseReason + " - " + reason
    }

    if (!responseData) {
        responseData = {}
    }

    var responseBody = JSON.stringify({
        Status: responseStatus,
        Reason: reason,
        PhysicalResourceId: physicalResourceId || context.logStreamName,
        StackId: event.StackId,
        RequestId: event.RequestId,
        LogicalResourceId: event.LogicalResourceId,
        Data: responseData
    });

    console.log("Response body:\n", responseBody);

    var https = require("https");
    var url = require("url");

    var parsedUrl = url.parse(event.ResponseURL);
    var options = {
        hostname: parsedUrl.hostname,
        port: 443,
        path: parsedUrl.path,
        method: "PUT",
        headers: {
            "content-type": "",
            "content-length": responseBody.length
        }
    };

    var request = https.request(options, function(response) {
        console.log("Status code: " + response.statusCode);
        console.log("Status message: " + response.statusMessage);
        if(!continueFuncton){
            context.done();
        }
    });

    request.on("error", function(error) {
        console.log("send(..) failed executing https.request(..): " + error);
        context.done();
    });

    request.write(responseBody);
    request.end();
};
