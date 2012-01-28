package com.github.shinobu_aoki.studyLucene

import org.apache.lucene.analysis.{TokenFilter, TokenStream}
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute
import org.apache.lucene.util.{Attribute, AttributeImpl, Version}

object PartOfSpeech extends Enumeration {
  val Noun, Verb, Adjective, Adverb, Pronoun, Preposition, Conjunction, Article, Unknown = Value
}

trait PartOfSpeechAttribute extends Attribute {
  def setPartOfSpeech(pos: PartOfSpeech.Value):Unit
  def getPartOfSpeech(): PartOfSpeech.Value
}

final class PartOfSpeechAttributeImpl extends AttributeImpl with PartOfSpeechAttribute {
  private var pos:PartOfSpeech.Value = PartOfSpeech.Unknown
  def setPartOfSpeech(pos: PartOfSpeech.Value) = this.pos = pos
  def getPartOfSpeech() = pos
  def clear() = pos = PartOfSpeech.Unknown
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
  
  final def incrementToken() = {
    if (!input.incrementToken()) {
      false
    } else {
      posAtt.setPartOfSpeech(determinePOS(termAtt.buffer(), 0, termAtt.length()))
      true
    }
  }
  
  protected def determinePOS(term:Array[Char], offset:Int, length:Int) = 
    if (length > 0 && Character.isUpperCase(term(0))) PartOfSpeech.Noun else PartOfSpeech.Unknown
}
