package com.github.shinobu_aoki.studyLucene

import java.io.StringReader
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, PositionIncrementAttribute}

class MyAnalyzerTest4 extends FunSuite with ShouldMatchers {
  test("test MyAnalyzer4") {
    val text = "This is a demo of the new TokenStream API. This is a Hogehoge."
    import PartOfSpeech._
    val expected = List(("This",Unknown,1), ("demo",Unknown,3), ("the",Unknown,2), ("new",Unknown,1), 
        ("TokenStream",Noun,1), ("API.",Noun,1), ("This",Unknown,1), ("Hogehoge.",Noun,3))
    val analyzer = new MyAnalyzer4
    val stream = analyzer.tokenStream("field", new StringReader(text))
    val termAtt = stream.addAttribute(classOf[CharTermAttribute])
    val posIncAtt = stream.addAttribute(classOf[PositionIncrementAttribute])
    val posAtt = stream.addAttribute(classOf[PartOfSpeechAttribute])
    stream.reset()
    Stream.continually(stream.incrementToken).takeWhile(b => b).map { b =>
      // term textと品詞と位置増分情報
      (new String(termAtt.buffer(), 0, termAtt.length()), 
        posAtt.getPartOfSpeech, posIncAtt.getPositionIncrement)
    }.toList should be (expected)
    stream.end()
    stream.close()
  }
}
