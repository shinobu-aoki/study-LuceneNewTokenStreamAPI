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

class PartOfSpeechTaggingFilter2(input: TokenStream) extends PartOfSpeechTaggingFilter(input) {
  val offsetAtt = addAttribute(classOf[OffsetAttribute])
  // オフセットが0のものをNounにしない
  override protected def determinePOS(term:Array[Char], offset:Int, length:Int) = 
    if (offsetAtt.startOffset > 0 && length > 0 && Character.isUpperCase(term(0))) PartOfSpeech.Noun else PartOfSpeech.Unknown
}

class MyAnalyzer4 extends Analyzer {
  
  def tokenStream(fieldName:String, reader:Reader): TokenStream = {
    // PartOfSpeechTaggingFilter追加
    new PartOfSpeechTaggingFilter2(new LengthFilter(
      true, new WhitespaceTokenizer(Version.LUCENE_35, reader), 3, Integer.MAX_VALUE))
  }
}
