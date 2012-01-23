package com.github.shinobu_aoki.studyLucene

import java.io.Reader
import org.apache.lucene.analysis.{Analyzer, TokenStream, WhitespaceTokenizer, LengthFilter}
import org.apache.lucene.util.Version

// LengthFilterを追加して2文字以下のTokenを切り捨てる
// http://lucene.apache.org/java/3_5_0/api/all/org/apache/lucene/analysis/package-summary.html
class MyAnalyzer2 extends Analyzer {
  
  def tokenStream(fieldName:String, reader:Reader): TokenStream = {
    new LengthFilter(true, new WhitespaceTokenizer(Version.LUCENE_35, reader), 3, Integer.MAX_VALUE)
  }
}
