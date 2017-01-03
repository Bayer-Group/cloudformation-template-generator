package com.monsanto.arch.cloudformation.model.simple

import com.monsanto.arch.cloudformation.model.ResourceRef
import com.monsanto.arch.cloudformation.model.resource.{`AWS::EC2::SecurityGroup`, `AWS::RDS::DBInstance`}

trait SecurityGroupRoutableMakerExtensions {

  implicit object RDSMaker extends SecurityGroupRoutableMaker[`AWS::RDS::DBInstance`] {
    def withSG(r: `AWS::RDS::DBInstance`, sgr: ResourceRef[`AWS::EC2::SecurityGroup`]) =
      r.copy(VPCSecurityGroups = Some(r.VPCSecurityGroups.getOrElse(Seq()) :+ sgr))
  }
}
