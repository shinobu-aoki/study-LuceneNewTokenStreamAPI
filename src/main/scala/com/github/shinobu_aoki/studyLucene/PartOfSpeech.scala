package com.github.shinobu_aoki.studyLucene

import org.apache.lucene.analysis.{TokenFilter, TokenStream}
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.util.{Attribute, AttributeImpl, Version}

sealed trait PartOfSpeech
case object Noun extends PartOfSpeech
case object Verb extends PartOfSpeech
case object Adjective extends PartOfSpeech
case object Adverb extends PartOfSpeech
case object Pronoun extends PartOfSpeech
case object Preposition extends PartOfSpeech
case object Conjunction extends PartOfSpeech
case object Article extends PartOfSpeech
case object Unknown extends PartOfSpeech

trait PartOfSpeechAttribute extends Attribute {
  def setPartOfSpeech(pos: PartOfSpeech):Unit
  def getPartOfSpeech(): PartOfSpeech
}

final class PartOfSpeechAttributeImpl extends AttributeImpl with PartOfSpeechAttribute {
  private var pos:PartOfSpeech = Unknown
  def setPartOfSpeech(pos: PartOfSpeech) = this.pos = pos
  def getPartOfSpeech() = pos
  def clear() = pos = Unknown
  def copyTo(target: AttributeImpl) = target.asInstanceOf[PartOfSpeechAttributeImpl].pos = pos
  override def equals(other:Any) = {
    if (other == this) true
    other match {
      case o:PartOfSpeechAttributeImpl => pos == o.pos
      case _ => false
    }
  }
  override def hashCode() = pos.hashCode()
}

class PartOfSpeechTaggingFilter(input: TokenStream) extends TokenFilter(input) {
  val posAtt = addAttribute(classOf[PartOfSpeechAttribute])
  val termAtt = addAttribute(classOf[CharTermAttribute])
  
  def incrementToken() = {
    if (!input.incrementToken()) {
      false
    } else {
      posAtt.setPartOfSpeech(determinePOS(termAtt.buffer(), 0, termAtt.length()))
      true
    }
  }
  
  protected def determinePOS(term:Array[Char], offset:Int, length:Int) = 
    if (length > 0 && Character.isUpperCase(term(0))) Noun else Unknown
}
