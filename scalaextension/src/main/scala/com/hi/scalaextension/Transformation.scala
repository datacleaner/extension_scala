package com.hi.scalaextension

trait Transformation {
  def execute(map: Map[String, _]): Any 
}