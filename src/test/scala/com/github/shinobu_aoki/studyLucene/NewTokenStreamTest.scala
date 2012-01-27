package com.github.shinobu_aoki.studyLucene

import java.io.{Reader,StringReader}
import org.apache.lucene.analysis.{TokenFilter,TokenStream,Tokenizer,WhitespaceTokenizer}
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute,OffsetAttribute}
import org.apache.lucene.util.Version
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

class NewTokenStreamTest extends Spec with ShouldMatchers {
  
  // テスト用Tokenizer
  class EmptyTokenizer(in:Reader) extends Tokenizer(in) {
    def incrementToken() = false
  }
  // テスト用のTokenFilter
  class EmptyTokenFilter(input:TokenStream) extends TokenFilter(input) {
    def incrementToken() = false
  }
  
  describe("AttributeSource#getAttribute") {
    
    it ("should throw IllegalArgumentException if Attributes is empty") {
      val tokenizer = new EmptyTokenizer(new StringReader("hoge1"))
      evaluating { tokenizer.getAttribute(classOf[CharTermAttribute]) } should produce [IllegalArgumentException]
      tokenizer.addAttribute(classOf[CharTermAttribute]) should not be (null)
      tokenizer.getAttribute(classOf[CharTermAttribute]) should not be (null)
    }
    
    it ("addAttribute should always return the same attribute") {
      val tokenizer = new EmptyTokenizer(new StringReader("hoge2"))
      val charTermAtt = tokenizer.addAttribute(classOf[CharTermAttribute])
      charTermAtt should not be (null)
      charTermAtt.isInstanceOf[CharTermAttribute] should be (true)
      val sameTermAtt = tokenizer.addAttribute(classOf[CharTermAttribute])
      sameTermAtt.eq(charTermAtt) should be (true)
    }
    
    it ("TokenStream and TokenFilters should share Attributes") {
      val tokenizer = new EmptyTokenizer(new StringReader("hoge3"))
      val filter = new EmptyTokenFilter(tokenizer)
      val att = tokenizer.addAttribute(classOf[CharTermAttribute])
      val attFromFilter = filter.addAttribute(classOf[CharTermAttribute])
      att.eq(attFromFilter) should be (true)
      val att2 = filter.addAttribute(classOf[OffsetAttribute])
      val att2FromTokenizer = tokenizer.addAttribute(classOf[OffsetAttribute])
      att2.eq(att2FromTokenizer) should be (true)
    }
    
    it ("Attribute instances should be reused for all tokens of a document") {
      val tokenizer = new WhitespaceTokenizer(Version.LUCENE_35, new StringReader("hoge4 hoge5"))
      // WhitespaceTokenizerはCharTermAttributeとOffsetAttributeを持っている（実際にはCharTokenizer）
      val charTermAtt = tokenizer.addAttribute(classOf[CharTermAttribute])
      while (tokenizer.incrementToken) {
        tokenizer.getAttribute(classOf[CharTermAttribute]).eq(charTermAtt) should be (true)
      }
    }
  }
}
