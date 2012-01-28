package com.github.shinobu_aoki.studyLucene

import java.io.{Reader, StringReader}
import org.apache.lucene.analysis.{Analyzer, TokenFilter, TokenStream, WhitespaceTokenizer, LengthFilter}
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, OffsetAttribute}
import org.apache.lucene.util.{Attribute, AttributeImpl, Version}

trait FirstTokenOfSentenceAttribute extends Attribute {
  def setFirstToken(firstToken: Boolean):Unit
  def isFirstToken():Boolean
}

class FirstTokenOfSentenceAttributeImpl extends AttributeImpl with FirstTokenOfSentenceAttribute {
  private var firstToken:Boolean = false
  def setFirstToken(firstToken: Boolean) = this.firstToken = firstToken
  def isFirstToken() = firstToken
  def clear() = firstToken = false
  def copyTo(target: AttributeImpl) = 
    target.asInstanceOf[FirstTokenOfSentenceAttributeImpl].firstToken = firstToken
  override def equals(other:Any) = {
    if (other == this) {
      true
    } else {
      other match {
        case o:FirstTokenOfSentenceAttributeImpl => firstToken == o.firstToken
        case _ => false
      }
    }
  }
  override def hashCode() = firstToken.hashCode()
}

class FirstTokenOfSentenceFilter(input: TokenStream) extends TokenFilter(input) {
  private val firstTokenAtt = addAttribute(classOf[FirstTokenOfSentenceAttribute])
  private val charTermAtt = addAttribute(classOf[CharTermAttribute])
  private var lastToken:Option[String] = None
  final def incrementToken() = {
    if (!input.incrementToken()) {
      false
    } else {
      firstTokenAtt.setFirstToken(lastToken.foldLeft(true)((b, a) => a.endsWith(".")))
      lastToken = Some(new String(charTermAtt.buffer(), 0, charTermAtt.length()))
      true
    }
  }
}

class PartOfSpeechTaggingFilter2(input: TokenStream) extends PartOfSpeechTaggingFilter(input) {
  val firstTokenAtt = addAttribute(classOf[FirstTokenOfSentenceAttribute])
  // 文の最初のTokenを名詞にしない
  override protected def determinePOS(term:Array[Char], offset:Int, length:Int) = 
    if (!firstTokenAtt.isFirstToken() && length > 0 && Character.isUpperCase(term(0))) PartOfSpeech.Noun else PartOfSpeech.Unknown
}

class MyAnalyzer4 extends Analyzer {
  
  def tokenStream(fieldName:String, reader:Reader): TokenStream = {
    // PartOfSpeechTaggingFilter追加
    new PartOfSpeechTaggingFilter2(new FirstTokenOfSentenceFilter(new LengthFilter(
      true, new WhitespaceTokenizer(Version.LUCENE_35, reader), 3, Integer.MAX_VALUE)))
  }
}
