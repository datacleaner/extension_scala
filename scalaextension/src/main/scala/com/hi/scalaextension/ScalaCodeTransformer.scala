package com.hi.scalaextension

import scala.collection.JavaConversions._
import javax.inject.Named
import org.datacleaner.api.InputColumn
import org.datacleaner.api.StringProperty
import org.datacleaner.api.OutputColumns
import org.datacleaner.api.InputRow
import org.datacleaner.api.Initialize
import org.datacleaner.api.Categorized
import org.datacleaner.api.Description
import org.datacleaner.api.Configured
import org.datacleaner.api.Transformer
import org.datacleaner.components.categories.ScriptingCategory

@Named("Scala Transformer")
@Description("Supply your own piece of Scala code to do a custom transformation.")
@Categorized(Array(classOf[ScriptingCategory]))
class ScalaCodeTransformer(cols: Array[InputColumn[_]]) extends Transformer {

  @Configured
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