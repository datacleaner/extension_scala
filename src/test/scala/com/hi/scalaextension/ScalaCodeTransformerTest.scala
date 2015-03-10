package com.hi.scalaextension

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.datacleaner.data.MockInputRow
import org.datacleaner.data.MockInputColumn

class ScalaCodeTransformerTest extends AssertionsForJUnit {

  @Test
  def testTransform() {
    val col: MockInputColumn[AnyRef] = new MockInputColumn[AnyRef]("A", classOf[String]);

    val transformer: ScalaCodeTransformer = new ScalaCodeTransformer();
    transformer.columns = Array(col)
    transformer.init
    var result = transformer.transform(new MockInputRow().put(col, "hello"))
    assertEquals("hello", result(0));
  }

  @Test
  def testNumberTransform() {
    val col: MockInputColumn[AnyRef] = new MockInputColumn[AnyRef]("A", classOf[Integer]);

    val transformer: ScalaCodeTransformer = new ScalaCodeTransformer();
    transformer.columns = Array(col)
    transformer.init
    var result = transformer.transform(new MockInputRow().put(col, 123))
    assertEquals(123, result(0));
  }
  
  @Test
  def testScalaIntegerTransform() {
    val col: MockInputColumn[AnyRef] = new MockInputColumn[AnyRef]("A", classOf[Integer]);
  
    val transformer: ScalaCodeTransformer = new ScalaCodeTransformer();
    transformer.columns = Array(col)
    transformer.sourceCode = """class ScalaTransformer extends com.hi.scalaextension.Transformation {
      								def execute(map: Map[String, _]) : Any = 1 
      							}
      						 """
    transformer.init
    var result = transformer.transform(new MockInputRow().put(col, 123))
    assertEquals(1, result(0));
  }
}
 