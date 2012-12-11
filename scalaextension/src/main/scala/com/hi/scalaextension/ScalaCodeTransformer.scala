package com.hi.scalaextension

import org.eobjects.analyzer.beans.api.Transformer
import org.eobjects.analyzer.beans.api.TransformerBean
import org.eobjects.analyzer.beans.api.Description
import org.eobjects.analyzer.beans.api.Categorized
import org.eobjects.analyzer.beans.categories.StringManipulationCategory
import org.eobjects.analyzer.data.InputColumn
import org.eobjects.analyzer.beans.api.Configured
import org.eobjects.analyzer.beans.api.OutputColumns
import org.eobjects.analyzer.data.InputRow
import org.eobjects.analyzer.beans.categories.ScriptingCategory
import org.eobjects.analyzer.beans.api.StringProperty
import org.eobjects.analyzer.beans.api.Initialize
import scala.collection.JavaConversions._

@TransformerBean("Scala Transformer")
@Description("Supply your own piece of Scala code to do a custom transformation.")
@Categorized(Array(classOf[ScriptingCategory]))
class ScalaCodeTransformer(cols: Array[InputColumn[_]]) extends Transformer[java.lang.Object] {

  @Configured
  @Description("Column to compute string lengths from")
  var columns: Array[InputColumn[_]] = cols;

  @Configured
  var returnType: java.lang.Class[_] = classOf[String]

  def this() = this(null)
  var cls: java.lang.Class[_] = null
  var instance: Transformation = null;

  @Configured
  @Description("Scala scripting code")
  @StringProperty(multiline = true, mimeType = Array[String] { "text/scala" })
  var sourceCode: String = """
    class ScalaTransformer extends com.hi.scalaextension.Transformation 
  	{
		def execute(map: Map[String, _]) : Any = 
		/** Replace the below default implementation with your scala scriptlet.
		*  The arguement map contains key as column name and value as column value.
		*/ 
		map("A") 
  	}""";
  @Initialize
  def init() {
    cls = (new Eval).compileCode("ScalaTransformer", sourceCode, true)
    instance = cls.newInstance().asInstanceOf[Transformation]
  }

  def getOutputColumns() = {
    val columnNames = Array("ScalaOutput")
    val columnTypes: Array[java.lang.Class[_]] = Array(returnType)
    new OutputColumns(columnNames, columnTypes)
  }

  def transform(row: InputRow): Array[java.lang.Object] = {
    val map: Map[String, _] = columns.map { column => (column.getName(), row.getValue(column)) } toMap
    val result = instance.execute(map)

    return Array(result).map { T => T.asInstanceOf[java.lang.Object] }
  }
}