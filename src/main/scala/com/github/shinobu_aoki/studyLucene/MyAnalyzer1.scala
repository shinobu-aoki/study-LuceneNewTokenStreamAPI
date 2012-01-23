package com.github.shinobu_aoki.studyLucene

import java.io.Reader
import org.apache.lucene.analysis.{Analyzer, TokenStream, WhitespaceTokenizer}
import org.apache.lucene.util.Version

class MyAnalyzer1 extends Analyzer {
  def tokenStream(fieldName:String, reader:Reader): TokenStream = {
    new WhitespaceTokenizer(Version.LUCENE_35, reader)
  }
}
