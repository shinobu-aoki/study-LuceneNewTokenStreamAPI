package com.github.shinobu_aoki.studyLucene

import java.io.{Reader, StringReader}
import org.apache.lucene.analysis.{Analyzer, TokenFilter, TokenStream, WhitespaceTokenizer, LengthFilter}
import org.apache.lucene.analysis.tokenattributes.{CharTermAttribute, PositionIncrementAttribute}
import org.apache.lucene.util.Version

class MyAnalyzer3 extends Analyzer {
  
  def tokenStream(fieldName:String, reader:Reader): TokenStream = {
    // PartOfSpeechTaggingFilter追加
    new PartOfSpeechTaggingFilter(new LengthFilter(
      true, new WhitespaceTokenizer(Version.LUCENE_35, reader), 3, Integer.MAX_VALUE))
  }
}
