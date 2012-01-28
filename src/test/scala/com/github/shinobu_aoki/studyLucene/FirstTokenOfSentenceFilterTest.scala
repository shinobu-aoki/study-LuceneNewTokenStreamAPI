package com.github.shinobu_aoki.studyLucene

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.apache.lucene.analysis.{TokenStream,WhitespaceTokenizer}
import java.io.StringReader
import org.apache.lucene.util.Version

class FirstTokenOfSentenceFilterTest extends Spec with ShouldMatchers {
  describe("FirstTokenOfSentenceFilter#incrementToken()") {
    it ("should be true if the first word of a sentence") {
      val stream:TokenStream = new FirstTokenOfSentenceFilter(
          new WhitespaceTokenizer(Version.LUCENE_35, 
              new StringReader("This is a demo. This is a hogehoge.")))
      val expected = List(true, false, false, false, true, false, false, false)
      val firstTokenAtt = stream.addAttribute(classOf[FirstTokenOfSentenceAttribute])
      stream.reset()
      Stream.continually(stream.incrementToken()).takeWhile(b => b).map { b =>
        firstTokenAtt.isFirstToken()
      }.toList should be (expected)
      stream.end()
      stream.close()
    }
  }
}