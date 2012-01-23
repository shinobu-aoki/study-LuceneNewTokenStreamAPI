package com.github.shinobu_aoki.studyLucene

import java.io.StringReader
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

class MyAnalyzerTest1 extends FunSuite with ShouldMatchers {
  test("test MyAnalyzer1") {
    val text = "This is a demo of the new TokenStream API"
    val expected = List("This", "is", "a", "demo", "of", "the", "new", "TokenStream", "API")
    val analyzer = new MyAnalyzer1
    val stream = analyzer.tokenStream("field", new StringReader(text))
    // TokenStreamからTermAttributeを取得(demoのTermAttributeはdeprecated)
    val termAtt = stream.addAttribute(classOf[CharTermAttribute])
    stream.reset()
    Stream.continually(stream.incrementToken).takeWhile(b => b).map { b =>
      // CharTermAttributeでは、termメソッドがないので、bufferメソッドとlengthを使う
      // char[]が最利用されているので、lengthを使うこと
      new String(termAtt.buffer(), 0, termAtt.length())
    }.toList should be (expected)
    stream.end()
    stream.close()
  }
}
