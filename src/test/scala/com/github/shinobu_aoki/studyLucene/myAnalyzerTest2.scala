package com.github.shinobu_aoki.studyLucene

import java.io.StringReader
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, PositionIncrementAttribute}

class MyAnalyzerTest2 extends FunSuite with ShouldMatchers {
  test("test MyAnalyzer2") {
    val text = "This is a demo of the new TokenStream API"
    // 3文字未満の文字列はスキップされるはず
    val expected = List(("This",1), ("demo",3), ("the",2), ("new",1), ("TokenStream",1), ("API",1))
    val analyzer = new MyAnalyzer2
    val stream = analyzer.tokenStream("field", new StringReader(text))
    val termAtt = stream.addAttribute(classOf[CharTermAttribute])
    val posIncAtt = stream.addAttribute(classOf[PositionIncrementAttribute])
    stream.reset()
    Stream.continually(stream.incrementToken).takeWhile(b => b).map { b =>
      // term textと位置増分情報
      (new String(termAtt.buffer(), 0, termAtt.length()), posIncAtt.getPositionIncrement)
    }.toList should be (expected)
    stream.end()
    stream.close()
  }
}
