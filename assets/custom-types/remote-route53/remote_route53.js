// Some portions of this (around custom CF functions) are from:
// https://gist.github.com/fcheung/baec53381350a4b11037

var aws = require('aws-sdk');
var util = require('util');

exports.handler = function(event, context) {
    var args = getArgs(event);
    console.log("Handling '" + args.ResourceType + "' event '" + args.RequestType + "'");

    if (args.ResourceType === 'Custom::RemoteRoute53RecordSet') {
        handleRecordSet(args, context);
    } else {
        errMsg = "unknown resource type: " + args.ResourceType;
        console.log(errMsg);
        if (args.RequestType == 'Delete') {
            // Report success, because we can never delete the stack otherwise.
            response.send(args, context, response.SUCCESS, errMsg);
        } else {
            response.send(args, context, response.FAILED, errMsg);
        }
    }
};

var getArgs = function(event) {
    // Were we called by SNS?
    if (event.Records && event.Records.length > 0 &&
        event.Records[0].Sns) {
        return JSON.parse(event.Records[0].Sns.Message);
    } else {
        // Assume we were called directly
        return event;
    }
};

var handleRecordSet = function(event, context) {
    if (validateCombos(event, context)) { // This takes care of failing out if needed
        if (event.RequestType === 'Delete') {
            deleteRecordSet(event, context);
        } else if (event.RequestType === 'Create') {
            createRecordSet(event, context);
        } else if (event.RequestType === 'Update') {
            updateRecordSet(event, context);
        } else {
            errMsg = "unknown request type: " + event.RequestType;
            console.log(errMsg);
            response.send(event, context, response.FAILED, errMsg);
        }
    }
};

var createRecordSet = function(event, context) {
    withRemoteRoute53(event, context, function(event, context, route53) {
        withHostedZone(event, context, route53, function (event, context, route53, hostedZoneId) {
            var params = {
                ChangeBatch: {
                    Changes: [
                        {
                            Action: 'CREATE',
                            ResourceRecordSet: buildRecordSet(event)
                        }
                    ]
                },
                HostedZoneId: hostedZoneId
            };
            changeResourceRecordSetsHelper(route53, params, function (err, data) {
                if (err) {
                    errMsg = "create record set failed: " + err;
                    console.log(errMsg);
                    response.send(event, context, response.FAILED, errMsg);

                } else {
                    response.send(event, context, response.SUCCESS, null, {}, physicalId(event.ResourceProperties));
                }
            });
        });
    });
};

var updateRecordSet = function(event, context) {
    withRemoteRoute53(event, context, function(event, context, route53) {
        withHostedZone(event, context, route53, function (event, context, route53, hostedZoneId) {
            var params = {
                ChangeBatch: {
                    Changes: [
                        {
                            Action: 'UPSERT',
                            ResourceRecordSet: buildRecordSet(event)
                        }
                    ]
                },
                HostedZoneId: hostedZoneId
            };
            changeResourceRecordSetsHelper(route53, params, function (err, data) {
                if (err) {
                    errMsg = "update record set failed: " + err;
                    console.log(errMsg);
                    response.send(event, context, response.FAILED, errMsg);

                } else {
                    response.send(event, context, response.SUCCESS, null, {}, physicalId(event.ResourceProperties));
                }
            });
        });
    });
};

var deleteRecordSet = function(event, context) {
    if(event.PhysicalResourceId.match(/^route53-record-set-/)){
        withRemoteRoute53(event, context, function(event, context, route53) {
            withHostedZone(event, context, route53, function (event, context, route53, hostedZoneId) {
                var params = {
                    ChangeBatch: {
                        Changes: [
                            {
                                Action: 'DELETE',
                                ResourceRecordSet: buildRecordSet(event)
                            }
                        ]
                    },
                    HostedZoneId: hostedZoneId
                };
                changeResourceRecordSetsHelper(route53, params, function (err, data) {
                    if (err) {
                        if (err.code == "InvalidChangeBatch" && err.message && err.message.indexOf("not found") >= 0) {
                            errMsg = "WARNING: " + err;
                            console.log(errMsg);
                            response.send(event, context, response.SUCCESS, errMsg, {}, physicalId(event.ResourceProperties));
                        } else {
                            errMsg = "delete record set failed: " + err;
                            console.log(errMsg);
                            response.send(event, context, response.FAILED, errMsg);
                        }

                    } else {
                        response.send(event, context, response.SUCCESS, null, {}, physicalId(event.ResourceProperties));
                    }
                });
            });
        });
    } else {
        errMsg = "unexpected physical id for record set " + event.PhysicalResourceId + " - ignoring";
        console.log(errMsg);
        response.send(event, context, response.SUCCESS, errMsg);
    }
};

var changeResourceRecordSetsHelper = function(route53, params, callback, iteration) {
    var iterationLimit = 100;
    if (!iteration) {
        iteration = 0;
    }
    route53.changeResourceRecordSets(params, function (err, data) {
        if (err && err.code == "PriorRequestNotComplete") {
            if (iteration < iterationLimit) {
                console.log("WARNING: Prior request not complete.  Retrying");
                changeResourceRecordSetsHelper(route53, params, callback, ++iteration, iterationLimit);
            } else {
                console.log("ERR: Prior request not complete, but retry limit hit.  Giving up.");
                callback(err, data)
            }
        } else {
            callback(err, data);
        }
    });
};

var validateCombos = function(event, context) {
    if (!event.ResourceProperties.Name) {
        errMsg = "missing parameter Name";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return;
    }

    if (!event.ResourceProperties.Type) {
        errMsg = "missing parameter Type";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return;
    }

    if (!event.ResourceProperties.AliasTarget &&
            (!event.ResourceProperties.ResourceRecords || event.ResourceProperties.ResourceRecords.length == 0)) {
        errMsg = "Must specify ResourceRecords or AliasTarget.";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return false;
    }

    if (event.ResourceProperties.AliasTarget && event.ResourceProperties.TTL) {
        errMsg = "TTL not supported for Alias records.";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return false;
    }

    if (!event.ResourceProperties.AliasTarget && !event.ResourceProperties.TTL) {
        errMsg = "TTL is required for ResourceRecord records.";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return false;
    }

    if ((event.ResourceProperties.Type == 'CNAME' || event.ResourceProperties.Type == 'SOA')
        && event.ResourceProperties.ResourceRecords.length > 1) {
        errMsg = "Only one resource record allowed for CNAME or SOA records.";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return false;
    }
    if (!event.ResourceProperties.SetIdentifier && (
            event.ResourceProperties.Weight ||
            event.ResourceProperties.Region ||
            event.ResourceProperties.GeoLocation ||
            event.ResourceProperties.Failover
        )) {
        errMsg = "SetIdentifier is required for weighted, latency, failover, or geolocation records.";
        console.log(errMsg);
        response.send(event, context, response.FAILED, errMsg);
        return false;
    }
    return true;
};

var buildRecordSet = function(event) {
    var record = {
        Name: event.ResourceProperties.Name,
        Type: event.ResourceProperties.Type
    };
    if (event.ResourceProperties.TTL) {
        if (typeof event.ResourceProperties.TTL === 'number') {
            record.TTL = event.ResourceProperties.TTL;
        } else {
            record.TTL = parseInt(event.ResourceProperties.TTL);
        }
    }

    if (event.ResourceProperties.SetIdentifier) {
        record.SetIdentifier = event.ResourceProperties.SetIdentifier;
    }

    if (event.ResourceProperties.Weight) {
        if (typeof event.ResourceProperties.Weight === 'number') {
            record.Weight = event.ResourceProperties.Weight;
        } else {
            record.Weight = parseInt(event.ResourceProperties.Weight);
        }
    }

    if (event.ResourceProperties.Region) {
        record.Region = event.ResourceProperties.Region;
    }

    if (event.ResourceProperties.GeoLocation) {
        record.GeoLocation = event.ResourceProperties.GeoLocation;
    }

    if (event.ResourceProperties.Failover) {
        record.Failover = event.ResourceProperties.Failover;
    }

    if (event.ResourceProperties.AliasTarget) {
        record.AliasTarget = event.ResourceProperties.AliasTarget;
        // Comes in from CF as a string, needs to be a boolean
        if (typeof record.AliasTarget.EvaluateTargetHealth === 'string') {
            record.AliasTarget.EvaluateTargetHealth = record.AliasTarget.EvaluateTargetHealth.toLowerCase()=='true'?true:false;
        }
    }

    if (event.ResourceProperties.HealthCheckId) {
        record.HealthCheckId = event.ResourceProperties.HealthCheckId;
    }

    var resourceRecords = event.ResourceProperties.ResourceRecords;
    if (resourceRecords && resourceRecords.length > 0) {
        record.ResourceRecords = resourceRecords.map(function (input) {
            return {"Value": input};
        })
    }
    return record;
};

var getRoute53 = function(auth) {
    return new aws.Route53({
        accessKeyId: auth.AccessKeyId,
        secretAccessKey: auth.SecretAccessKey,
        sessionToken: auth.SessionToken,
        region: 'us-east-1' // Route53 always runs against us-east-1
    });
};

var withRemoteRoute53 = function(event, context, nextFunc) {
    var params = {
        RoleArn: event.ResourceProperties.DestinationRole,
        RoleSessionName: event.RequestId
    };
    var sts = new aws.STS();
    sts.assumeRole(params, function(err, data) {
        if (err) {
            console.log(err, err.stack);
            response.send(event, context, response.FAILED, err);
        }
        else {
            nextFunc(event, context, getRoute53(data.Credentials));
        }
    });
};

var withHostedZone = function(event, context, route53, nextFunc) {
    if (event.ResourceProperties.HostedZoneId) {
        // Make sure it exists and we can see it.
        var params = {
            Id: event.ResourceProperties.HostedZoneId
        };
        route53.getHostedZone(params, function(err, data) {
            if (err) {
                console.log(err, err.stack);
                response.send(event, context, response.FAILED, err);
            }
            else {
                nextFunc(event, context, route53, event.ResourceProperties.HostedZoneId)
            }
        });
    } else if (event.ResourceProperties.HostedZoneName) {
        route53.listHostedZones({}, function(err, data) {
            if (err) {
                console.log(err, err.stack);
                response.send(event, context, response.FAILED, err);
            }
            else {
                searchHostedZoneNames(event, context, route53, null, null, nextFunc);
            }
        });
    } else {
        response.send(event, context, response.FAILED, "Must specify HostedZoneId or HostedZoneName.");
    }
};

var searchHostedZoneNames = function(event, context, route53, marker, prevMatchResults, nextFunc) {
    var params = {};
    if (marker) {
        params = {'Marker': marker};
    }
    var fqdnToFind = event.ResourceProperties.HostedZoneName.endsWith('.')?event.ResourceProperties.HostedZoneName:(event.ResourceProperties.HostedZoneName + '.')

    route53.listHostedZones(params, function(err, data) {
        if (err) {
            console.log(err, err.stack);
            response.send(event, context, response.FAILED, err);
        }
        else {
            var matchResults = data.HostedZones.map(function(obj){
                var fqdnToCheck = obj.Name.endsWith('.')?obj.Name:(obj.Name + '.');

                if (fqdnToCheck == fqdnToFind) {
                    return obj.Id;
                } else {
                    return null;
                }
            }).filter(function(obj) {return obj != undefined});
            var aggregateResults = prevMatchResults?prevMatchResults.concat(matchResults):matchResults

            if (data.IsTruncated == true) {
                searchHostedZoneNames(event, context, route53, data.NextMarker, aggregateResults, nextFunc);
            } else {
                if (aggregateResults.length == 0) {
                    response.send(event, context, response.FAILED, "No zone found for HostedZoneName: " + event.ResourceProperties.HostedZoneName);
                } else if (aggregateResults.length > 1) {
                    response.send(event, context, response.FAILED, "Multiple zones found for HostedZoneName: " + event.ResourceProperties.HostedZoneName + " -- specify HostedZoneId.");
                } else {
                    nextFunc(event, context, route53, aggregateResults[0]);
                }
            }
        }
    });
};

var physicalId = function(properties) {
    return 'route53-record-set-' + properties.Name;
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


if (!String.prototype.endsWith) {
    String.prototype.endsWith = function(searchString, position) {
        var subjectString = this.toString();
        if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
            position = subjectString.length;
        }
        position -= searchString.length;
        var lastIndex = subjectString.indexOf(searchString, position);
        return lastIndex !== -1 && lastIndex === position;
    };
}


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
